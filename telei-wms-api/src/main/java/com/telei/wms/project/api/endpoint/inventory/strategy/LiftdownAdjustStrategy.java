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
@Component("liftdownAdjustStrategy")
public class LiftdownAdjustStrategy implements IAdjustStrategy {
    @Autowired
    private AdjustStrategyFactory adjustStrategyFactory;

    @Override
    public List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> process(WmsAdjtHeader wmsAdjtHeader, List<WmsAdjtLine> wmsAdjtLineList, List<WmsInventory> WmsInventoryDbList,
                                                                                          List<WmsInventory> wmsInventoryAddList, List<WmsInventory> wmsInventoryUpdateList,
                                                                                          List<Long> deleteIvidList, List<WmsIvTransaction> wmsIvTransactionList,
                                                                                          List<WmsIvSplit> wmsIvSplitList, UserInfo userInfo, Date nowWithUtc) {
        WmsInventory wmsInventory = WmsInventoryDbList.get(0);
        BigDecimal ivQtyAdjt = wmsAdjtHeader.getIvQtyAdjt();/**降任务 库存调整数量*/
        String lcCodeAdjt = wmsAdjtHeader.getLcCodeAdjt();/**降任务 调整库位(目标库位)*/

        wmsAdjtHeader.setLcCodeAdjt(lcCodeAdjt);/**升降任务-目标库位*/
        wmsAdjtHeader.setBigBagRate(wmsInventory.getBigBagRate());/**大包转换率*/
        wmsAdjtHeader.setMidBagRate(wmsInventory.getMidBagRate());/**中包转换率*/

        List<WmsInventory> wmsInventories = DataConvertUtil.parseDataAsArray(WmsInventoryDbList, WmsInventory.class);
        BigDecimal totalIvQty = wmsInventories.stream().map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalIvQty.compareTo(ivQtyAdjt) < 0) {
            ErrorCode.ADJT_ERROR_4016.throwError(wmsAdjtHeader.getLcCode(), wmsAdjtHeader.getProductId(), ivQtyAdjt, totalIvQty, wmsAdjtHeader.getAdjhType());
        }
        for (WmsInventory inventory : wmsInventories) {
            BigDecimal ivQty = inventory.getIvQty();
            if (ivQty.compareTo(ivQtyAdjt) > 0) {
                /**当前批次库存数 > 调整库存数*/
                BigDecimal ivQtyAfter = ivQty.subtract(ivQtyAdjt);
                inventory.setIvQty(ivQtyAfter);
                inventory.setIvTranstime(nowWithUtc);
                wmsInventoryUpdateList.add(inventory);
                adjustStrategyFactory.createTransactionRecored(wmsIvTransactionList, inventory, lcCodeAdjt, "MOVE", ivQtyAdjt, userInfo, nowWithUtc);
                /***库存调整单明细记录*/
                adjustStrategyFactory.createAdjtLine(wmsAdjtHeader, wmsAdjtLineList, inventory, null, "LIFTDOWN", ivQtyAdjt, lcCodeAdjt);
                /**库存记录*/
                WmsInventory inventoryAdd = adjustStrategyFactory.createInventory(wmsInventoryAddList, inventory,lcCodeAdjt, ivQtyAdjt, nowWithUtc);
                /**库存拆分记录*/
                adjustStrategyFactory.createSplit(wmsIvSplitList, ivQtyAdjt, inventory, ivQtyAfter, inventoryAdd);
                break;
            }

            deleteIvidList.add(inventory.getIvId());
            /**库存记录*/
            WmsInventory inventoryAdd = adjustStrategyFactory.createInventory(wmsInventoryAddList, inventory,lcCodeAdjt, ivQtyAdjt, nowWithUtc);
            /**库存变更记录*/
            adjustStrategyFactory.createTransactionRecored(wmsIvTransactionList, inventory, lcCodeAdjt, "MOVE", ivQtyAdjt, userInfo, nowWithUtc);
            /***库存调整单明细记录*/
            adjustStrategyFactory.createAdjtLine(wmsAdjtHeader, wmsAdjtLineList, inventory, inventoryAdd, "LIFTDOWN", ivQtyAdjt, lcCodeAdjt);

            if (ivQty.compareTo(ivQtyAdjt) == 0) {
                /**当前批次库存数 = 调整库存数*/
                break;
            }
            /**当前批次库存数 < 调整库存数*/
            ivQtyAdjt = ivQtyAdjt.subtract(ivQty);
        }
        return null;
    }
}
