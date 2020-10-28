package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.google.common.collect.Lists;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 库存调整-调增
 * @author: leo
 * @date: 2020/9/8 16:29
 */
@Component("increaseAdjustStrategy")
public class IncreaseAdjustStrategy implements IAdjustStrategy{
    @Autowired
    private AdjustStrategyFactory adjustStrategyFactory;

    @Override
    public List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> process(WmsAdjtHeader wmsAdjtHeader, List<WmsAdjtLine> wmsAdjtLineList, List<WmsInventory> WmsInventoryDbList,
                                                                                          List<WmsInventory> wmsInventoryAddList, List<WmsInventory> wmsInventoryUpdateList, List<Long> deleteIvidList,
                                                                                          List<WmsIvTransaction> wmsIvTransactionList,
                                                                                          List<WmsIvSplit> wmsIvSplitList, UserInfo userInfo, Date nowWithUtc) {
        WmsInventory wmsInventory = WmsInventoryDbList.get(0);
        BigDecimal ivQtyAdjt = wmsAdjtHeader.getIvQtyAdjt();/**库存调整数*/
        String lcCodeAdjt = wmsAdjtHeader.getLcCode(); /**库位编码*/

        wmsAdjtHeader.setLcCodeAdjt(lcCodeAdjt);/**调整库位*/
        wmsAdjtHeader.setBigBagRate(wmsInventory.getBigBagRate());/**大包转换率*/
        wmsAdjtHeader.setMidBagRate(wmsInventory.getMidBagRate());/**中包转换率*/


        /**新增库存记录*/
        WmsInventory wmsInventoryAdd = adjustStrategyFactory.createInventory(wmsInventoryAddList, wmsInventory, lcCodeAdjt,ivQtyAdjt, nowWithUtc);

        /**库存调整明细记录*/
        adjustStrategyFactory.createAdjtLine(wmsAdjtHeader ,wmsAdjtLineList, wmsInventory,wmsInventoryAdd,"INCREASE" ,ivQtyAdjt, lcCodeAdjt);

        /**库存变更记录*/
        adjustStrategyFactory.createTransactionRecored(wmsIvTransactionList,wmsInventoryAddList.get(0), lcCodeAdjt, "INCR", ivQtyAdjt, userInfo, nowWithUtc);

        /**OMS库存同步*/
        List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> list = Lists.newArrayList();

        adjustStrategyFactory.createOmsInventoryChangeWriteBackCondition(ivQtyAdjt, list, wmsInventory,1,wmsAdjtHeader);
        return list;
    }
}