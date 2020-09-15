package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.nuochen.framework.component.commons.spring.SpringApplicationContext;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author: leo
 * @date: 2020/9/8 16:24
 */
@Component("adjustStrategyFactory")
public class AdjustStrategyFactory {
    @Autowired
    private Id idGenerator;

    /**
     * 通过库存调整类型获取其策略的实现
     *
     * @param adjustType
     * @return
     */
    public  IAdjustStrategy getAdjustStrategy(String adjustType){
        AdjustEnum adjustEnum = AdjustEnum.findAdjustEnumByadjustType(adjustType);
        if(Objects.isNull(adjustEnum)){
            return null;
        }
        return  SpringApplicationContext.getBean(adjustEnum.getBeanName(), IAdjustStrategy.class);
    }

    /**
     * 创建库存变动记录
     *
     * @param inventory
     * @param lcCodeTo
     * @param ivChangeType
     * @param curIvIdQtyAdjt
     * @param userInfo
     * @param nowWithUtc
     * @return
     */
    public  WmsIvTransaction createTransactionRecored(WmsInventory inventory,String lcCodeTo,  String ivChangeType ,BigDecimal curIvIdQtyAdjt, UserInfo userInfo, Date nowWithUtc) {
        WmsIvTransaction wmsIvTransaction = DataConvertUtil.parseDataAsObject(inventory,WmsIvTransaction.class);
        wmsIvTransaction.setApCode("ADJT");/**应用类型*/
        wmsIvTransaction.setIvtChangeType(ivChangeType);/**库存变动类型*/
        wmsIvTransaction.setLcCodeFrom(inventory.getLcCode());/**原库位编码*/
        wmsIvTransaction.setLcCodeTo(lcCodeTo);/**目标库位编码*/
        wmsIvTransaction.setIvQtyFrom(inventory.getIvQty());/**原数量*/
        wmsIvTransaction.setIvQtyTo(inventory.getIvQty().add(curIvIdQtyAdjt));/**调整后数量*/
        wmsIvTransaction.setDcQty(curIvIdQtyAdjt);/**调整数量*/
        wmsIvTransaction.setCreateTime(nowWithUtc);
        wmsIvTransaction.setCreateUser(userInfo.getUserName());
        return wmsIvTransaction;
    }


    /**
     * 创建库存记录
     *
     * @param wmsInventoryAddList
     * @param nowWithUtc
     * @param wmsInventory
     * @param ivQtyAdjt
     * @return
     */
    public   WmsInventory createInventory(List<WmsInventory> wmsInventoryAddList, WmsInventory wmsInventory, BigDecimal ivQtyAdjt, Date nowWithUtc) {
        WmsInventory wmsInventoryAdd = DataConvertUtil.parseDataAsObject(wmsInventory, WmsInventory.class);
        wmsInventoryAdd.setIvId(idGenerator.getUnique());
        wmsInventoryAdd.setIvQty(ivQtyAdjt); /**库存数量(调整数)*/
        wmsInventoryAdd.setBigBagQty(ivQtyAdjt.multiply(new BigDecimal(wmsInventoryAdd.getBigBagRate())));/**大包数量*/
        wmsInventoryAdd.setBigBagExtraQty(ivQtyAdjt.divideAndRemainder(new BigDecimal(wmsInventoryAdd.getBigBagRate()))[1]);/**大包剩余数量*/
        wmsInventoryAdd.setMidBagQty(ivQtyAdjt.multiply(new BigDecimal(wmsInventoryAdd.getMidBagRate()))); /**中包数量*/
        wmsInventoryAdd.setMidBagExtraQty(ivQtyAdjt.divideAndRemainder(wmsInventoryAdd.getMidBagExtraQty())[1]);/**中包剩余数量*/
        wmsInventoryAdd.setIvTranstime(nowWithUtc);
        wmsInventoryAdd.setIvCreatetime(nowWithUtc);
        wmsInventoryAdd.setBizDate(nowWithUtc);
        wmsInventoryAddList.add(wmsInventoryAdd);
        return wmsInventoryAdd;
    }

    /***
     * 创建库存调整明细
     *
     * @param wmsAdjtHeader
     * @param wmsAdjtLineList
     * @param wmsInventory
     * @param adjhType
     * @param ivQtyAdjt
     * @param lcCodeAdjt
     */
    public  void createAdjtLine(WmsAdjtHeader wmsAdjtHeader, List<WmsAdjtLine> wmsAdjtLineList, WmsInventory wmsInventory, String adjhType,BigDecimal ivQtyAdjt, String lcCodeAdjt) {
        WmsAdjtLine wmsAdjtLine = new WmsAdjtLine();
        wmsAdjtLine.setAdjlId(idGenerator.getUnique());
        wmsAdjtLine.setIvAdjhType(adjhType);/**库存调整类型*/
        wmsAdjtLine.setAdjhId(wmsAdjtHeader.getAdjhId());/**库存调整单单头id*/
        wmsAdjtLine.setIvId(wmsInventory.getIvId());/**库存id*/
        wmsAdjtLine.setIvQty(wmsInventory.getIvQty());/**库存数量*/
        wmsAdjtLine.setIvQtyAdjt(ivQtyAdjt);/**库存调整数量*/
        wmsAdjtLine.setLcCode(wmsAdjtHeader.getLcCode());/**库位编码*/
        wmsAdjtLine.setLcCodeAdjt(lcCodeAdjt);/**调整库位*/
        wmsAdjtLineList.add(wmsAdjtLine);
    }

    /**
     * 创建回写oms库存记录
     *
     * @param ivQtyAdjt
     * @param list
     * @param inventory
     */
    public  void  createOmsInventoryChangeWriteBackCondition(BigDecimal ivQtyAdjt, List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> list, WmsInventory inventory, int type) {
        OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition omsInventoryChangeWriteBackCondition = DataConvertUtil.parseDataAsObject(inventory, OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition.class);
        omsInventoryChangeWriteBackCondition.setType(type);
        omsInventoryChangeWriteBackCondition.setQty(ivQtyAdjt);
        list.add(omsInventoryChangeWriteBackCondition);
    }

    /**
     * 创建库存拆分记录
     *
     * @param wmsIvSplitList
     * @param ivQtyAdjt
     * @param inventory
     * @param ivQtyAfter
     * @param inventoryAdd
     */
    public  void createSplit(List<WmsIvSplit> wmsIvSplitList, BigDecimal ivQtyAdjt, WmsInventory inventory, BigDecimal ivQtyAfter, WmsInventory inventoryAdd) {
        WmsIvSplit wmsIvSplit = new WmsIvSplit();
        wmsIvSplit.setIvspId(idGenerator.getUnique());
        wmsIvSplit.setIvId(inventory.getIvId());/**原库存id*/
        wmsIvSplit.setIvIdTo(inventoryAdd.getIvId());/**新库存id*/
        wmsIvSplit.setIvQty(inventory.getIvQty());/**原库存数量*/
        wmsIvSplit.setIvQtyTo(ivQtyAdjt);/**拆分库存数量*/
        wmsIvSplit.setIvQtyAfter(ivQtyAfter);/**拆后库存数量*/
        wmsIvSplitList.add(wmsIvSplit);
    }
}
