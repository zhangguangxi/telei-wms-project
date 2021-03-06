package com.telei.wms.project.api.endpoint.ro.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * wms_ro_line 入库计划明细
 */
@Data
public class RoLineAddRequest {
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
    private Long costReferenceCurrencyId;
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
    private Long unitPriceCurrencyId;
    /** 备注 */
    private String memo;
}