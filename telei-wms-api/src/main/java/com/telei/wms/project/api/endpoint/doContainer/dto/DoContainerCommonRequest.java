package com.telei.wms.project.api.endpoint.doContainer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuochen.framework.component.validation.Check;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DoContainerCommonRequest {

    /** 出库任务明细id */
    @Check
    private Long dolId;
    /** 出库任务id */
    @Check
    private Long dohId;
    /** 柜id */
    @JsonProperty("cId")
    @Check
    private Long cId;
    /** 出库计划id */
    @Check
    private Long spId;
    /** 销售订单id */
    @Check
    private Long soId;
    /** 产品id */
    @Check
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
    private BigDecimal qty;
    /** 装箱重量(KG) */
    @JsonProperty("cWeight")
    private BigDecimal cWeight;
    /** 装箱体积(CBM) */
    @JsonProperty("cVol")
    private BigDecimal cVol;
    /** 参考成本 */
    private BigDecimal costReference;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 装箱金额 */
    @JsonProperty("cAmount")
    private BigDecimal cAmount;
    /** 已装柜数量 */
    @JsonProperty("cQty")
    private BigDecimal cQty;
    /** 退库数量 */
    @JsonProperty("bQty")
    private BigDecimal bQty;
    /** 已拣数量 */
    private BigDecimal pickedQty;

}