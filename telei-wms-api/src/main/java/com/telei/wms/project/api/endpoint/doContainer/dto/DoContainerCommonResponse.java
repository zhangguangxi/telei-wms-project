package com.telei.wms.project.api.endpoint.doContainer.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * wms_ro_header 出库任务
 */
@Data
public class DoContainerCommonResponse {
    /** id */
    private Long id;
    /** 出库任务明细id */
    private Long dolId;
    /** 出库任务id */
    private Long dohId;
    /** 柜id */
    private Long cId;
    /** 出库计划id */
    private Long spId;
    /** 销售订单id */
    private Long soId;
    /** 产品id */
    private Long productId;
    /** 大包数量 */
    private BigDecimal bigBagQty;
    /** 大包转换数 */
    private BigDecimal bigBagRate;
    /** 中包数量 */
    private BigDecimal midBagQty;
    /** 中包转换数 */
    private BigDecimal midBagRate;
    /** 小包数量 */
    private BigDecimal smallBagQty;
    /** 装箱数量 */
    private BigDecimal cQty;
    /** 装箱重量(KG) */
    private BigDecimal cWeight;
    /** 装箱体积(CBM) */
    private BigDecimal cVol;
    /** 参考成本 */
    private BigDecimal costReference;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 装箱金额 */
    private BigDecimal cAmount;


    /** 装柜时间 */
    private Date loadTime;
    /** 装柜员 */
    private String loadUser;
    /** 物流单 */
    private String shipmentCode;
    /** 大包数量 */
    private BigDecimal sumBigBagQty;
    /** 中包数量 */
    private BigDecimal sumMidBagQty;
    /** 小包数量 */
    private BigDecimal sumSmallBagQty;
    /** 装箱数量 */
    private BigDecimal sumQty;

}