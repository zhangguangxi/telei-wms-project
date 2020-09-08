package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.WmsAdjtHeader;
import com.telei.wms.datasource.wms.model.WmsAdjtLine;
import com.telei.wms.datasource.wms.model.WmsInventory;
import com.telei.wms.datasource.wms.model.WmsIvTransaction;

import java.util.Date;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/8 15:47
 */
public interface IAdjustStrategy {
    default List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> process(WmsAdjtHeader wmsAdjtHeader, List<WmsAdjtLine> wmsAdjtLineList,
                                                                                     List<WmsInventory> WmsInventoryDbList,
                                                                                     List<WmsInventory> wmsInventoryAddList, List<WmsInventory> wmsInventoryUpdateList,
                                                                                     List<Long> deleteIvidList, List<WmsIvTransaction> ivTransaction, UserInfo userInfo, Date nowWithUtc){
        return null;
    };
}
