package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.google.common.collect.Lists;
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
@Component("reduceAdjustStrategy")
public class ReduceAdjustStrategy implements IAdjustStrategy{
    @Autowired
    private AdjustStrategyFactory adjustStrategyFactory;
    @Override
    public List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> process(WmsAdjtHeader wmsAdjtHeader, List<WmsAdjtLine> wmsAdjtLineList, List<WmsInventory> WmsInventoryDbList,
                                                                                    List<WmsInventory> wmsInventoryAddList, List<WmsInventory> wmsInventoryUpdateList, List<Long> deleteIvidList,
                                                                                    List<WmsIvTransaction> ivTransactionList,
                                                                                    List<WmsIvSplit> wmsIvSplitList,UserInfo userInfo, Date nowWithUtc) {
        List<WmsInventory> wmsInventories = DataConvertUtil.parseDataAsArray(WmsInventoryDbList, WmsInventory.class);
        BigDecimal totalIvQty = wmsInventories.stream().map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal ivQtyAdjt = wmsAdjtHeader.getIvQtyAdjt();/**调整数量*/
        String lcCodeAdjt = wmsAdjtHeader.getLcCodeAdjt(); /**调整库位*/
        int compareRet = totalIvQty.compareTo(ivQtyAdjt);
        if(compareRet < 0){
            ErrorCode.ADJT_ERROR_4013.throwError(wmsAdjtHeader.getLcCode(),wmsAdjtHeader.getProductId(),ivQtyAdjt,totalIvQty,wmsAdjtHeader.getAdjhType());
        }
        List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> list = Lists.newArrayList();
        for (WmsInventory inventory : wmsInventories) {
            BigDecimal ivQty = inventory.getIvQty();
            if(ivQty.compareTo(ivQtyAdjt) > 0){
                /**当前批次库存数 > 调整库存数*/
                inventory.setIvQty(ivQty.subtract(ivQtyAdjt));
                inventory.setIvTranstime(nowWithUtc);
                /**库存记录更新*/
                wmsInventoryUpdateList.add(inventory);
                /**库存变更记录新增*/
                ivTransactionList.add(adjustStrategyFactory.createTransactionRecored(inventory,lcCodeAdjt,"LESS",ivQtyAdjt,userInfo, nowWithUtc));
                /***oms库存记回写录*/
                adjustStrategyFactory.createOmsInventoryChangeWriteBackCondition(ivQtyAdjt, list, inventory,2);
                /***库存调整单明细记录*/
                adjustStrategyFactory.createAdjtLine(wmsAdjtHeader, wmsAdjtLineList, inventory,"REDUCE" ,ivQtyAdjt, lcCodeAdjt);
                break;
            }else if(ivQty.compareTo(ivQtyAdjt) == 0){
                /**当前批次库存数 = 调整库存数*/
                /**删除库存记录*/
                deleteIvidList.add(inventory.getIvId());
                /**库存变更记录*/
                ivTransactionList.add(adjustStrategyFactory.createTransactionRecored(inventory,lcCodeAdjt,"LESS",ivQtyAdjt,userInfo, nowWithUtc));
                /**OMS库存回写记录*/
                adjustStrategyFactory.createOmsInventoryChangeWriteBackCondition(ivQtyAdjt, list, inventory,2);
                /***库存调整单明细记录*/
                adjustStrategyFactory.createAdjtLine(wmsAdjtHeader, wmsAdjtLineList, inventory,"REDUCE" ,ivQtyAdjt, lcCodeAdjt);
                break;
            }else{
                /**当前批次库存数 < 调整库存数*/
                ivQtyAdjt = ivQtyAdjt.subtract(ivQty);
                /**删除库存记录*/
                deleteIvidList.add(inventory.getIvId());
                /**库存变更记录*/
                ivTransactionList.add(adjustStrategyFactory.createTransactionRecored(inventory,lcCodeAdjt,"LESS",ivQtyAdjt,userInfo, nowWithUtc));
                /**OMS库存回写记录*/
                adjustStrategyFactory.createOmsInventoryChangeWriteBackCondition(ivQtyAdjt, list, inventory,2);
                /***库存调整单明细记录*/
                adjustStrategyFactory.createAdjtLine(wmsAdjtHeader, wmsAdjtLineList, inventory,"REDUCE" ,ivQtyAdjt, lcCodeAdjt);
            }
        }
        return list;
    }


}
