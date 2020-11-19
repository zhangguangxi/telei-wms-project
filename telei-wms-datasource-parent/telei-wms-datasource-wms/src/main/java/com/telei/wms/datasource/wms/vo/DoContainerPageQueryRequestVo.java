package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 货柜分页查询
 */
@Data
public class DoContainerPageQueryRequestVo {

    /** 出库任务明细id */
    private Long dolId;
    /** 出库任务id */
    private Long dohId;
    /** 出库计划id */
    private Long spId;
    /** 销售订单id */
    private Long soId;
    /** 图 */
    private String mediaId;
    /** 产品id */
    private Long productId;
    /** sku编码 */
    private String productNo;
    /** 产品名称 */
    private String productName;
    /** 产品条码 */
    private String productBarcode;
    /** 产品品牌 */
    private String brand;
    /** 单位重量 */
    private BigDecimal unitGrossWeight;
    /** 单位体积 */
    private BigDecimal unitVol;
    /** 大包重量 */
    private BigDecimal bigBagWeight;
    /** 大包体积 */
    private BigDecimal bigBagVol;
    /** 中包重量 */
    private BigDecimal midBagWeight;
    /** 中包体积 */
    private BigDecimal midBagVol;
    /** 大包转换数 */
    private BigDecimal bigBagRate;
    /** 中包转换数 */
    private BigDecimal midBagRate;
    /** 已装柜数量 */
    private BigDecimal cQty;
    /** 装箱重量(KG) */
    private BigDecimal cWeight;
    /** 装箱体积(CBM) */
    private BigDecimal cVol;
    /** 装箱金额 */
    private BigDecimal cAmount;
    /** 退库数量 */
    private BigDecimal bQty;
    /** 已拣数量 */
    private BigDecimal pickedQty;
    /** 大包数量 */
    private BigDecimal bigBagQty;
    /** 中包数量 */
    private BigDecimal midBagQty;
    /** 小包数量 */
    private BigDecimal smallBagQty;
    /** 参考成本 */
    private BigDecimal costReference;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 库位编码 */
    private String lcCode;

}
