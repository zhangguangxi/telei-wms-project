package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.*;

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
                                                                                           List<Long> deleteIvidList, List<WmsIvTransaction> ivTransaction,
                                                                                           List<WmsIvSplit> wmsIvSplitList,UserInfo userInfo, Date nowWithUtc){
        return null;
    };
}
