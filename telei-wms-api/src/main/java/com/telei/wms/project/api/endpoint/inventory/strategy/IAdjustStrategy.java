package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.WmsAdjtHeader;
import com.telei.wms.datasource.wms.model.WmsAdjtLine;
import com.telei.wms.datasource.wms.model.WmsInventory;
import com.telei.wms.datasource.wms.model.WmsIvTransaction;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/8 15:47
 */
public interface IAdjustStrategy {
    default OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition process(WmsAdjtHeader wmsAdjtHeader, WmsAdjtLine wmsAdjtLine, List<WmsInventory> wmsInventoryList, WmsInventory wmsInventory, WmsIvTransaction ivTransaction){
        return null;
    };
}
