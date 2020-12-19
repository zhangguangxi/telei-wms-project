package com.telei.wms.customer.amqp.containerWriteBack;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: leo
 * @date: 2020/9/1 14:55
 */
@Data
public class OmsContainerWriteBack {

    /** 装箱重量(KG) */
    private BigDecimal cWeight;
    /** 装箱体积(CBM) */
    private BigDecimal cVol;
    /** 装箱金额 */
    private BigDecimal cAmount;
    /** 订单类型 */
    private String orderType;
    /** 柜id */
    private Long cId;
    /** 出库任务id */
    private Long dohId;
    /** 出库任务编号 */
    private String dohCode;
    /** 出库计划id */
    private Long spId;
    /** 销售订单id */
    private Long soId;

}
