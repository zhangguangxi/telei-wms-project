package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.component.commons.spring.SpringApplicationContext;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.WmsLocationService;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.utils.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author: leo
 * @date: 2020/9/8 16:24
 */
@Slf4j
@Component("adjustStrategyFactory")
public class AdjustStrategyFactory {
    @Autowired
    private Id idGenerator;

    @Autowired
    private WmsLocationService wmsLocationService;

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
    public  WmsIvTransaction createTransactionRecored(List<WmsIvTransaction> wmsIvTransactionList,WmsInventory inventory,String lcCodeTo,
                                                      String ivChangeType ,BigDecimal curIvIdQtyAdjt, UserInfo userInfo, Date nowWithUtc) {
        WmsIvTransaction wmsIvTransaction = DataConvertUtil.parseDataAsObject(inventory,WmsIvTransaction.class);
        wmsIvTransaction.setId(idGenerator.getUnique());
        wmsIvTransaction.setApCode("ADJT");/**应用类型*/
        wmsIvTransaction.setIvtDocumentCode(inventory.getIvDocumentCode());/**引起库存变动业务单据编码*/
        wmsIvTransaction.setIvtDocumentId(inventory.getIvDocumentId());/**引起库存变动的单据id*/
        wmsIvTransaction.setIvtDocumentlineId(inventory.getIvDocumentlineId());/**引起库存变动的单据明细id*/
        wmsIvTransaction.setIvtChangeType(ivChangeType);/**库存变动类型*/
        wmsIvTransaction.setLcCodeFrom(inventory.getLcCode());/**原库位编码*/
        wmsIvTransaction.setLcCodeTo(lcCodeTo);/**目标库位编码*/
        wmsIvTransaction.setIvQtyFrom(inventory.getIvQty());/**原数量*/
        wmsIvTransaction.setIvQtyTo(inventory.getIvQty().add(curIvIdQtyAdjt));/**调整后数量*/
        wmsIvTransaction.setDcQty(curIvIdQtyAdjt);/**调整数量*/
        wmsIvTransaction.setCreateTime(nowWithUtc);
        wmsIvTransaction.setCreateUser(userInfo.getUserName());
        wmsIvTransactionList.add(wmsIvTransaction);
        log.info("\n +++++++++++++++++++++ 库存调整::创建库存变动记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(wmsIvTransaction));
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
    public   WmsInventory createInventory(List<WmsInventory> wmsInventoryAddList, WmsInventory wmsInventory,String lcCodeAdjt, BigDecimal ivQtyAdjt, Date nowWithUtc) {
        WmsInventory wmsInventoryAdd = DataConvertUtil.parseDataAsObject(wmsInventory, WmsInventory.class);
        wmsInventoryAdd.setIvId(idGenerator.getUnique());
        wmsInventoryAdd.setIvIdFrom(wmsInventory.getIvId());
        wmsInventoryAdd.setLcCode(lcCodeAdjt);//库位编码
        WmsLocation wmsLocation = new WmsLocation();
        wmsLocation.setLcCode(lcCodeAdjt);
        wmsLocation.setWarehouseId(wmsInventory.getWarehouseId());
        wmsLocation = wmsLocationService.selectOneByEntity(wmsLocation);
        if(Objects.isNull(wmsLocation)){
            ErrorCode.ADJT_ERROR_4019.throwError(wmsInventory.getWarehouseId(),JSON.toJSONString(lcCodeAdjt));
        }
        BigDecimal[] bigBag = ivQtyAdjt.divideAndRemainder(new BigDecimal(wmsInventoryAdd.getBigBagRate()));
        BigDecimal[] midBag = ivQtyAdjt.divideAndRemainder(new BigDecimal(wmsInventoryAdd.getMidBagRate()));
        wmsInventoryAdd.setLcType(wmsLocation.getLcType());//库位类型
        wmsInventoryAdd.setIvQty(ivQtyAdjt); /**库存数量(调整数)*/
        wmsInventoryAdd.setBigBagQty(bigBag[0]);/**大包数量*/
        wmsInventoryAdd.setBigBagExtraQty(bigBag[1]);/**大包剩余数量*/
        wmsInventoryAdd.setMidBagQty(midBag[0]); /**中包数量*/
        wmsInventoryAdd.setMidBagExtraQty(midBag[1]);/**中包剩余数量*/
        wmsInventoryAdd.setIvTranstime(nowWithUtc);
        wmsInventoryAdd.setIvCreatetime(nowWithUtc);
        wmsInventoryAdd.setBizDate(nowWithUtc);
        log.info("\n +++++++++++++++++++++ 库存调整::创建库存记录 -> {} ++++++++++++++++++++ \n ", JSON.toJSONString(wmsInventoryAdd));
        wmsInventoryAddList.add(wmsInventoryAdd);
        return wmsInventoryAdd;
    }

    /**
     * 创建库存调整明细
     *
     * @param wmsAdjtHeader
     * @param wmsAdjtLineList
     * @param wmsInventoryDb
     * @param wmsInventoryAdd
     * @param adjhType
     * @param ivQtyAdjt
     * @param lcCodeAdjt
     */
    public  void createAdjtLine(WmsAdjtHeader wmsAdjtHeader, List<WmsAdjtLine> wmsAdjtLineList, WmsInventory wmsInventoryDb, WmsInventory wmsInventoryAdd,String adjhType,BigDecimal ivQtyAdjt, String lcCodeAdjt) {
        WmsAdjtLine wmsAdjtLine = new WmsAdjtLine();
        wmsAdjtLine.setAdjlId(idGenerator.getUnique());
        wmsAdjtLine.setIvAdjhType(adjhType);/**库存调整类型*/
        wmsAdjtLine.setAdjhId(wmsAdjtHeader.getAdjhId());/**库存调整单单头id*/
        wmsAdjtLine.setIvId(wmsInventoryDb.getIvId());/**库存id*/

        if(Objects.equals(adjhType,"MOVE") && (wmsInventoryDb.getIvQty().compareTo(ivQtyAdjt) > 0)){
            wmsAdjtLine.setIvIdAdjt(wmsInventoryAdd.getIvId());//调整后的库存id
        }else{
            wmsAdjtLine.setIvIdAdjt(wmsInventoryDb.getIvId());//调整后的库存id
        }

        wmsAdjtLine.setIvQty(wmsInventoryDb.getIvQty());/**库存数量*/
        if(Arrays.asList("MOVE","LIFTUP","LIFTDOWN").contains(adjhType)){
            wmsAdjtLine.setIvQtyAdjt(new BigDecimal(0));
        }else{
            wmsAdjtLine.setIvQtyAdjt(ivQtyAdjt);
        }
        wmsAdjtLine.setLcCode(wmsAdjtHeader.getLcCode());/**库位编码*/
        wmsAdjtLine.setLcCodeAdjt(lcCodeAdjt);/**调整库位*/
        log.info("\n +++++++++++++++++++++ 库存调整::创建库存调整明细 -> {} ++++++++++++++++++++ \n ",JSON.toJSONString(wmsAdjtLine));
        wmsAdjtLineList.add(wmsAdjtLine);
    }

    /**
     * 创建回写oms库存记录
     *
     * @param ivQtyAdjt
     * @param list
     * @param inventory
     */
    public  void  createOmsInventoryChangeWriteBackCondition(BigDecimal ivQtyAdjt, List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> list, WmsInventory inventory, int type,WmsAdjtHeader wmsAdjtHeader) {
        OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition omsInventoryChangeWriteBackCondition = DataConvertUtil.parseDataAsObject(inventory, OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition.class);
        omsInventoryChangeWriteBackCondition.setType(type);
        omsInventoryChangeWriteBackCondition.setQty(ivQtyAdjt);
        omsInventoryChangeWriteBackCondition.setBussinessId(wmsAdjtHeader.getAdjhId());
        omsInventoryChangeWriteBackCondition.setBussinessCode(wmsAdjtHeader.getAdjhCode());
        log.info("\n +++++++++++++++++++++ 库存调整::创建回写oms库存记录 -> {} ++++++++++++++++++++ \n ",JSON.toJSONString(omsInventoryChangeWriteBackCondition));
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
        wmsIvSplit.setIvQty(ivQtyAdjt.add(inventory.getIvQty()));/**原库存数量*/
        wmsIvSplit.setIvQtyTo(ivQtyAdjt);/**拆分库存数量*/
        wmsIvSplit.setIvQtyAfter(ivQtyAfter);/**拆后库存数量*/
        log.info("\n +++++++++++++++++++++ 库存调整::创建库存拆分记录 -> {} ++++++++++++++++++++ \n ",JSON.toJSONString(wmsIvSplit));
        wmsIvSplitList.add(wmsIvSplit);
    }
}
