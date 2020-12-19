package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.alibaba.fastjson.JSON;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.WmsInventoryService;
import com.telei.wms.datasource.wms.service.WmsLocationService;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author: leo
 * @date: 2020/9/8 16:30
 */
@Component("moveAdjustStrategy")
public class MoveAdjustStrategy  implements IAdjustStrategy {
    @Autowired
    private AdjustStrategyFactory adjustStrategyFactory;

    @Autowired
    private WmsLocationService wmsLocationService;

    @Autowired
    private WmsInventoryService wmsInventoryService;


    @Override
    public List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> process(WmsAdjtHeader wmsAdjtHeader, List<WmsAdjtLine> wmsAdjtLineList, List<WmsInventory> WmsInventoryDbList,
                                                                                          List<WmsInventory> wmsInventoryAddList, List<WmsInventory> wmsInventoryUpdateList,
                                                                                          List<Long> deleteIvidList, List<WmsIvTransaction> wmsIvTransactionList,
                                                                                          List<WmsIvSplit> wmsIvSplitList,UserInfo userInfo, Date nowWithUtc) {

        WmsInventory wmsInventory = WmsInventoryDbList.get(0);
        Long productId = wmsAdjtHeader.getProductId();

        BigDecimal ivQtyAdjt = wmsAdjtHeader.getIvQtyAdjt();/**库存调整数量*/
        String lcCodeAdjt = wmsAdjtHeader.getLcCodeAdjt();/**调整库位(目标库位)*/

        wmsAdjtHeader.setLcCodeAdjt(lcCodeAdjt);/**移库目标库位*/
        wmsAdjtHeader.setBigBagRate(wmsInventory.getBigBagRate());/**大包转换率*/
        wmsAdjtHeader.setMidBagRate(wmsInventory.getMidBagRate());/**中包转换率*/


        WmsLocation wmsLocation = new WmsLocation();
        wmsLocation.setLcCode(lcCodeAdjt);
        wmsLocation.setWarehouseId(wmsAdjtHeader.getWarehouseId());
        wmsLocation = wmsLocationService.selectOneByEntity(wmsLocation);

        if(Objects.isNull(wmsLocation)){
            ErrorCode.ADJT_ERROR_4017.throwError(wmsAdjtHeader.getLcCode(), JSON.toJSONString(productId),lcCodeAdjt,wmsAdjtHeader.getAdjhType());
        }

        /**高架库位限制*/
        adjustStrategyFactory.zLocationLimit(productId,lcCodeAdjt,1);

        List<WmsInventory> wmsInventories = DataConvertUtil.parseDataAsArray(WmsInventoryDbList, WmsInventory.class);
        BigDecimal dbIvQty = wmsInventories.stream().map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        if(dbIvQty.compareTo(ivQtyAdjt) < 0 ){
            ErrorCode.ADJT_ERROR_4014.throwError(wmsAdjtHeader.getLcCode(), productId,ivQtyAdjt,dbIvQty,wmsAdjtHeader.getAdjhType());
        }
        for (WmsInventory inventory : wmsInventories) {
            BigDecimal ivQty = inventory.getIvQty();
            if(ivQty.compareTo(ivQtyAdjt) > 0){
                /**当前批次库存数 > 调整库存数*/
                BigDecimal ivQtyAfter = ivQty.subtract(ivQtyAdjt);
                inventory.setIvQty(ivQtyAfter);
                inventory.setIvTranstime(nowWithUtc);
                wmsInventoryUpdateList.add(inventory);
                adjustStrategyFactory.createTransactionRecored(wmsIvTransactionList,inventory,lcCodeAdjt,"MOVE",ivQtyAdjt,userInfo, nowWithUtc);
                /**新增库存记录*/
                WmsInventory inventoryAdd = adjustStrategyFactory.createInventory(wmsInventoryAddList, inventory, lcCodeAdjt,ivQtyAdjt, nowWithUtc);
                /***库存调整单明细记录*/
                adjustStrategyFactory.createAdjtLine(wmsAdjtHeader, wmsAdjtLineList, inventory,inventoryAdd,"MOVE" ,ivQtyAdjt, lcCodeAdjt);
                /**库存拆分记录*/
                adjustStrategyFactory.createSplit(wmsIvSplitList, ivQtyAdjt, inventory, ivQtyAfter, inventoryAdd);
                break;
            }
            /**当前批次库存数 = 调整库存数*/
            deleteIvidList.add(inventory.getIvId());
            /**新增库存变更记录*/
            adjustStrategyFactory.createTransactionRecored(wmsIvTransactionList,inventory,lcCodeAdjt,"MOVE",ivQtyAdjt,userInfo, nowWithUtc);
            /**新增库存记录*/
            adjustStrategyFactory.createInventory(wmsInventoryAddList, inventory, lcCodeAdjt,ivQtyAdjt, nowWithUtc);
            /***库存调整单明细记录*/
            adjustStrategyFactory.createAdjtLine(wmsAdjtHeader, wmsAdjtLineList, inventory,null,"MOVE" ,ivQtyAdjt, lcCodeAdjt);
            if(ivQty.compareTo(ivQtyAdjt) == 0){
                break;
            }
            ivQtyAdjt = ivQtyAdjt.subtract(ivQty);
        }
        return null;
    }


}
