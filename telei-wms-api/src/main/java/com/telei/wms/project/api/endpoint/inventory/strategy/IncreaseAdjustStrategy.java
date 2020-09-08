package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 库存调整-调增
 * @author: leo
 * @date: 2020/9/8 16:29
 */
@Component("increaseAdjustStrategy")
public class IncreaseAdjustStrategy implements IAdjustStrategy{

    @Override
    public OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition process(WmsAdjtHeader wmsAdjtHeader, WmsAdjtLine wmsAdjtLine, List<WmsInventory> wmsInventoryList,WmsInventory wmsInventory, WmsIvTransaction ivTransaction) {
        wmsAdjtLine.setIvAdjhType("INCREASE");
        wmsInventory.setIvIdFrom(wmsAdjtLine.getIvId());
        /**新增时-调整数即为库存数*/
        wmsInventory.setIvQty(wmsAdjtLine.getIvQtyAdjt());
        ivTransaction.setIvQtyTo(wmsInventoryList.get(0).getIvQty().add(wmsAdjtLine.getIvQtyAdjt()));
        OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition omsInventoryChangeWriteBackCondition = DataConvertUtil.parseDataAsObject(wmsInventoryList.get(0), OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition.class);
        omsInventoryChangeWriteBackCondition.setType(1);
        return omsInventoryChangeWriteBackCondition;
    }
}