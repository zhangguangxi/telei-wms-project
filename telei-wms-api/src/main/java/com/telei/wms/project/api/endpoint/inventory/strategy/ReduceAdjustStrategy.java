package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.google.common.collect.Lists;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.WmsAdjtHeader;
import com.telei.wms.datasource.wms.model.WmsAdjtLine;
import com.telei.wms.datasource.wms.model.WmsInventory;
import com.telei.wms.datasource.wms.model.WmsIvTransaction;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author: leo
 * @date: 2020/9/8 16:30
 */
@Component("reduceAdjustStrategy")
public class ReduceAdjustStrategy implements IAdjustStrategy{
    @Override
    public List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> process(WmsAdjtHeader wmsAdjtHeader, List<WmsAdjtLine> wmsAdjtLineList, List<WmsInventory> WmsInventoryDbList,
                                                                                    List<WmsInventory> wmsInventoryAddList, List<WmsInventory> wmsInventoryUpdateList, List<Long> deleteIvidList,
                                                                                    List<WmsIvTransaction> ivTransaction, UserInfo userInfo, Date nowWithUtc) {
        List<WmsInventory> wmsInventories = DataConvertUtil.parseDataAsArray(WmsInventoryDbList, WmsInventory.class);
        BigDecimal totalIvQty = wmsInventories.stream().map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal ivQtyAdjt = wmsAdjtHeader.getIvQtyAdjt();/**调整数量*/
        String lcCodeAdjt = wmsAdjtHeader.getLcCodeAdjt(); /**调整库位*/
        int compareRet = totalIvQty.compareTo(ivQtyAdjt);
        if(compareRet < 0){
            // TODO: 2020/9/8  调减库存量不能大于现有库存量
        }
        List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> list = Lists.newArrayList();
        for (WmsInventory inventory : wmsInventories) {

            BigDecimal ivQty = inventory.getIvQty();
            if(ivQty.compareTo(ivQtyAdjt) > 0){
                /**当前批次库存数 > 调整库存数*/
                inventory.setIvQty(ivQty.subtract(ivQtyAdjt));
                inventory.setIvTranstime(nowWithUtc);
//              inventory.setLastupdateUser(userInfo.getUserName());
                wmsInventoryUpdateList.add(inventory);
                createOmsInventoryChangeWriteBackCondition(ivQtyAdjt, list, inventory);
                break;
            }else if(ivQty.compareTo(ivQtyAdjt) == 0){
                /**当前批次库存数 = 调整库存数*/
                deleteIvidList.add(inventory.getIvId());
                AdjustStrategyFactory.createTransactionRecored(inventory,lcCodeAdjt,"REDUCE",ivQtyAdjt,userInfo, nowWithUtc);
                break;
            }else{
                /**当前批次库存数 < 调整库存数*/
                deleteIvidList.add(inventory.getIvId());
                ivQtyAdjt = ivQtyAdjt.subtract(ivQty);
                AdjustStrategyFactory.createTransactionRecored(inventory,lcCodeAdjt,"REDUCE",ivQtyAdjt,userInfo, nowWithUtc);
            }
        }
        return list;
    }

    private void  createOmsInventoryChangeWriteBackCondition(BigDecimal ivQtyAdjt, List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> list, WmsInventory inventory) {
        OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition omsInventoryChangeWriteBackCondition = DataConvertUtil.parseDataAsObject(inventory, OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition.class);
        omsInventoryChangeWriteBackCondition.setType(2);
        omsInventoryChangeWriteBackCondition.setQty(ivQtyAdjt);
        list.add(omsInventoryChangeWriteBackCondition);
    }
}
