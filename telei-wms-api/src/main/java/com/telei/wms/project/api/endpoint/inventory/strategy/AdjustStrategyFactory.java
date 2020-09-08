package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.nuochen.framework.component.commons.spring.SpringApplicationContext;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.wms.datasource.wms.model.WmsAdjtHeader;
import com.telei.wms.datasource.wms.model.WmsInventory;
import com.telei.wms.datasource.wms.model.WmsIvTransaction;
import com.telei.wms.project.api.utils.DataConvertUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author: leo
 * @date: 2020/9/8 16:24
 */
public class AdjustStrategyFactory {
    /**
     * 通过库存调整类型获取其策略的实现
     *
     * @param adjustType
     * @return
     */
    public static IAdjustStrategy getAdjustStrategy(String adjustType){
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
    public static WmsIvTransaction createTransactionRecored(WmsInventory inventory,String lcCodeTo,  String ivChangeType ,BigDecimal curIvIdQtyAdjt, UserInfo userInfo, Date nowWithUtc) {
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
}
