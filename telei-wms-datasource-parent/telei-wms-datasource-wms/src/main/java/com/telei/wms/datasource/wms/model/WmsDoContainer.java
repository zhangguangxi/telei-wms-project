package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * wms_do_container 出库任务装箱信息
 */
@Data
public class WmsDoContainer implements Entity<Long> {
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
}