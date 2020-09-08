package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.WmsAdjtHeader;
import com.telei.wms.datasource.wms.model.WmsAdjtLine;
import com.telei.wms.datasource.wms.model.WmsInventory;
import com.telei.wms.datasource.wms.model.WmsIvTransaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/8 16:30
 */
@Component("reduceAdjustStrategy")
public class ReduceAdjustStrategy implements IAdjustStrategy{
    @Override
    public OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition process(WmsAdjtHeader wmsAdjtHeader, WmsAdjtLine wmsAdjtLine, List<WmsInventory> wmsInventoryList, WmsInventory wmsInventory, WmsIvTransaction ivTransaction) {
        // TODO: 2020/9/8 需要校验库存数量是否足够,然后逐级递减(labid asc)
        BigDecimal totalIvQty = wmsInventoryList.stream().map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        int compareRet = totalIvQty.compareTo(wmsAdjtHeader.getIvQtyAdjt());
        if(compareRet < 0){
            // TODO: 2020/9/8
        }
        for (WmsInventory inventory : wmsInventoryList) {

        }
        return null;
    }
}
