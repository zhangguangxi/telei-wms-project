package com.telei.wms.project.api.endpoint.ro.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * wms_ro_line 入库计划明细
 */
@Data
public class RoLineDetailResponse {
    /** 入库任务明细id */
    private Long id;
    /** 入库任务id */
    private Long roId;
    /** 入库计划id */
    private Long rpId;
    /** 入库计划单明细id */
    private Long rpdId;
    /** 采购单id */
    private Long poId;
    /** 采购单明细id */
    private Long podId;
    /** 产品id */
    private Long productId;
    /** 图片 */
    private String mediaId;
    /** 产品编码-客户输入 */
    private String productNo;
    /** 产品名称 */
    private String productName;
    /** 产品本地名称 */
    private String productNameLocal;
    /** 条码 */
    private String productBarcode;
    /** UPC码 */
    private String productUpcCode;
    /** 规格型号 */
    private String specType;
    /** 品牌 */
    private String brand;
    /** 参考成本 */
    private BigDecimal costReference;
    /** 成本币种 */
    private Integer costReferenceCurrencyId;
    /** 采购单价 */
    private BigDecimal unitPrice;
    /** 计划入库数量 */
    private BigDecimal planQty;
    /** 实收数量 */
    private BigDecimal receiptsQty;
    /** 计量单位 */
    private Long unitId;
    /** 采购金额 */
    private BigDecimal amount;
    /** 采购币种 */
    private Integer unitPriceCurrencyId;
    /** 备注 */
    private String memo;

    /** 产地 */
    private String placeOfOrigin;
    /** 单位毛量(KG) */
    private BigDecimal weight;
    /** 长(CM) */
    private BigDecimal productLength;
    /** 宽(CM) */
    private BigDecimal productWidth;
    /** 高(CM) */
    private BigDecimal productHeight;
    /** 单位体积(CBM) */
    private BigDecimal vol;

    /** 中包数量 */
    private Integer midBagQty;
    /** 中包毛重(KG) */
    private BigDecimal midBagWeight;
    /** 中包长(CM) */
    private BigDecimal midBagLength;
    /** 中包宽(CM) */
    private BigDecimal midBagWidth;
    /** 中包高(CM) */
    private BigDecimal midBagHeight;
    /** 中包体积(CBM) */
    private BigDecimal midBagVol;
    /** 中包条码 */
    private String midBagBarcode;

    /** 大包数量 */
    private Integer bigBagQty;
    /** 大包毛重(KG) */
    private BigDecimal bigBagWeight;
    /** 大包长(CM) */
    private BigDecimal bigBagLength;
    /** 大包宽(CM) */
    private BigDecimal bigBagWidth;
    /** 大包高(CM) */
    private BigDecimal bigBagHeight;
    /** 大包体积(CBM) */
    private BigDecimal bigBagVol;
    /** 大包条码 */
    private String bigBagBarcode;
}