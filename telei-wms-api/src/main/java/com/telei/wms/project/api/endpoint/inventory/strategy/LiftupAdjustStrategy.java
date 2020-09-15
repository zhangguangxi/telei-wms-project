package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/8 16:30
 */
@Component("liftupAdjustStrategy")
public class LiftupAdjustStrategy  implements  IAdjustStrategy{
    @Autowired
    private AdjustStrategyFactory adjustStrategyFactory;

    @Override
    public List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> process(WmsAdjtHeader wmsAdjtHeader, List<WmsAdjtLine> wmsAdjtLineList, List<WmsInventory> WmsInventoryDbList,
                                                                                          List<WmsInventory> wmsInventoryAddList, List<WmsInventory> wmsInventoryUpdateList,
                                                                                          List<Long> deleteIvidList, List<WmsIvTransaction> ivTransaction,
                                                                                          List<WmsIvSplit> wmsIvSplitList, UserInfo userInfo, Date nowWithUtc) {
        BigDecimal ivQtyAdjt = wmsAdjtHeader.getIvQtyAdjt();/**库存调整数量*/
        String lcCodeAdjt = wmsAdjtHeader.getLcCodeAdjt();/**调整库位(目标库位)*/
        List<WmsInventory> wmsInventories = DataConvertUtil.parseDataAsArray(WmsInventoryDbList, WmsInventory.class);
        BigDecimal totalIvQty = wmsInventories.stream().map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);

        if(totalIvQty.compareTo(ivQtyAdjt) < 0 ){
            ErrorCode.ADJT_ERROR_4015.throwError(wmsAdjtHeader.getLcCode(),wmsAdjtHeader.getProductId(),ivQtyAdjt,totalIvQty,wmsAdjtHeader.getAdjhType());
        }
        for (WmsInventory inventory : wmsInventories) {
            BigDecimal ivQty = inventory.getIvQty();
            if(ivQty.compareTo(ivQtyAdjt) > 0){
                /**当前批次库存数 > 调整库存数*/
                BigDecimal ivQtyAfter = ivQty.subtract(ivQtyAdjt);
                inventory.setIvQty(ivQtyAfter);
                inventory.setIvTranstime(nowWithUtc);
                wmsInventoryUpdateList.add(inventory);
                adjustStrategyFactory.createTransactionRecored(inventory,lcCodeAdjt,"MOVE",ivQtyAdjt,userInfo, nowWithUtc);
                /***库存调整单明细记录*/
                adjustStrategyFactory.createAdjtLine(wmsAdjtHeader, wmsAdjtLineList, inventory,null,"LIFTUP" ,ivQtyAdjt, lcCodeAdjt);
                /**新增库存记录*/
                WmsInventory inventoryAdd = adjustStrategyFactory.createInventory(wmsInventoryAddList, inventory, ivQtyAdjt, nowWithUtc);
                /**库存拆分记录*/
                adjustStrategyFactory.createSplit(wmsIvSplitList, ivQtyAdjt, inventory, ivQtyAfter, inventoryAdd);
                break;
            }else if(ivQty.compareTo(ivQtyAdjt) == 0){
                deleteIvidList.add(inventory.getIvId());
                /**新增库存变更记录*/
                adjustStrategyFactory.createTransactionRecored(inventory,lcCodeAdjt,"MOVE",ivQtyAdjt,userInfo, nowWithUtc);
                /**新增库存记录*/
                WmsInventory wmsInventoryAdd = adjustStrategyFactory.createInventory(wmsInventoryAddList, inventory, ivQtyAdjt, nowWithUtc);
                /***库存调整单明细记录*/
                adjustStrategyFactory.createAdjtLine(wmsAdjtHeader, wmsAdjtLineList, inventory,wmsInventoryAdd,"LIFTUP" ,ivQtyAdjt, lcCodeAdjt);
                break;
            }else{
                /**当前批次库存数 < 调整库存数*/
                deleteIvidList.add(inventory.getIvId());
                ivQtyAdjt = ivQtyAdjt.subtract(ivQty);
                /**新增库存记录*/
                WmsInventory wmsInventoryAdd = adjustStrategyFactory.createInventory(wmsInventoryAddList, inventory, ivQtyAdjt, nowWithUtc);
                /**新增库存变更记录*/
                adjustStrategyFactory.createTransactionRecored(inventory,lcCodeAdjt,"MOVE",ivQtyAdjt,userInfo, nowWithUtc);
                /***库存调整单明细记录*/
                adjustStrategyFactory.createAdjtLine(wmsAdjtHeader, wmsAdjtLineList, inventory,wmsInventoryAdd,"LIFTUP" ,ivQtyAdjt, lcCodeAdjt);
            }
        }
        /**新增库存记录(移库对应的数据)*/
        WmsInventory wmsInventoryAdd = adjustStrategyFactory.createInventory(wmsInventoryAddList, WmsInventoryDbList.get(0) , ivQtyAdjt,nowWithUtc);
        wmsInventoryAddList.add(wmsInventoryAdd);
        return null;
    }
}
