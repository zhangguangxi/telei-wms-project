package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.google.common.collect.Lists;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private Id idGenerator;

    @Override
    public List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> process(WmsAdjtHeader wmsAdjtHeader, List<WmsAdjtLine> wmsAdjtLineList, List<WmsInventory> WmsInventoryDbList,
                                                                                    List<WmsInventory> wmsInventoryAddList, List<WmsInventory> wmsInventoryUpdateList, List<Long> deleteIvidList,
                                                                                    List<WmsIvTransaction> ivTransaction, UserInfo userInfo, Date nowWithUtc) {
        WmsInventory wmsInventory = WmsInventoryDbList.get(0);
        BigDecimal ivQtyAdjt = wmsAdjtHeader.getIvQtyAdjt();
        /**库存调整明细记录*/
        WmsAdjtLine wmsAdjtLine = new WmsAdjtLine();
        wmsAdjtLine.setIvAdjhType("INCREASE");/**库存调整类型*/
        wmsAdjtLine.setAdjhId(wmsAdjtHeader.getAdjhId());/**库存调整单单头id*/
        wmsAdjtLine.setIvId(wmsInventory.getIvId());/**库存id*/
        wmsAdjtLine.setIvQty(wmsInventory.getIvQty());/**库存数量*/
        wmsAdjtLine.setIvQtyAdjt(ivQtyAdjt);/**库存调整数量*/
        wmsAdjtLine.setLcCode(wmsAdjtHeader.getLcCode());/**库位编码*/
        wmsAdjtLine.setLcCodeAdjt(wmsAdjtHeader.getLcCodeAdjt());/**调整库位*/
        wmsAdjtLineList.add(wmsAdjtLine);

        /**新增库存记录*/
        WmsInventory wmsInventoryAdd = DataConvertUtil.parseDataAsObject(wmsInventory, WmsInventory.class);
        wmsInventoryAdd.setIvId(idGenerator.getUnique());
        wmsInventoryAdd.setIvQty(wmsAdjtLine.getIvQtyAdjt()); /**库存数量(调整数)*/
        wmsInventoryAdd.setBigBagQty(ivQtyAdjt.multiply(wmsInventoryAdd.getBigBagRate()));/**大包数量*/
        wmsInventoryAdd.setBigBagExtraQty(ivQtyAdjt.divideAndRemainder(wmsInventoryAdd.getBigBagRate())[1]);/**大包剩余数量*/
        wmsInventoryAdd.setMidBagQty(ivQtyAdjt.multiply(wmsInventoryAdd.getMidBagRate())); /**中包数量*/
        wmsInventoryAdd.setMidBagExtraQty(ivQtyAdjt.divideAndRemainder(wmsInventoryAdd.getMidBagExtraQty())[1]);/**中包剩余数量*/
//      wmsInventoryAdd.setLastupdateUser(userInfo.getUserName()); long or STring?  transaction中的ivtChangeType需要做相应的同步
        wmsInventoryAdd.setIvTranstime(nowWithUtc);
        wmsInventoryAdd.setIvCreatetime(nowWithUtc);
        wmsInventoryAdd.setBizDate(nowWithUtc);
        wmsInventoryAddList.add(wmsInventoryAdd);

        /**库存变更记录*/
        WmsIvTransaction wmsIvTransaction = DataConvertUtil.parseDataAsObject(wmsInventoryAdd, WmsIvTransaction.class);
        wmsIvTransaction.setApCode("ADJT");/**应用类型*/
        wmsIvTransaction.setIvtChangeType("INCREASE");/**库存变动类型*/
        wmsIvTransaction.setLcCodeFrom(wmsInventory.getLcCode());/**原库位编码*/
        wmsIvTransaction.setIvQtyFrom(wmsInventory.getIvQty());/**原数量*/
        wmsIvTransaction.setIvQtyTo(wmsInventory.getIvQty().add(ivQtyAdjt));/**调整后数量*/
        wmsIvTransaction.setDcQty(ivQtyAdjt);/**调整数量*/
        wmsIvTransaction.setCreateTime(nowWithUtc);
        wmsIvTransaction.setCreateUser(userInfo.getUserName());

        /**OMS库存同步,同步记录还是数量？*/
        List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> list = Lists.newArrayList();
        OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition omsInventoryChangeWriteBackCondition = DataConvertUtil.parseDataAsObject(wmsInventoryAdd, OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition.class);
        omsInventoryChangeWriteBackCondition.setType(1);
        omsInventoryChangeWriteBackCondition.setQty(ivQtyAdjt);
        list.add(omsInventoryChangeWriteBackCondition);
        return list;
    }
}