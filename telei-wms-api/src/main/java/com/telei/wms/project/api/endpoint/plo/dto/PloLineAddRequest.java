package com.telei.wms.project.api.endpoint.plo.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 新增Request
 */
@Data
public class PloLineAddRequest {
    /** id */
    private Long id;
    /** 单头id */
    private Long ploId;
    /** 业务单据编号 */
    private String ploCode;
    /** id */
    private Long dolId;
    /** 产品id */
    private Long productId;
    /** 条码 */
    private String productBarcode;
    /** 规格型号-有颜色尺码拼接 */
    private String specType;
    /** 图片 */
    private String mediaId;
    /** 产品编码-客户输入 */
    private String productNo;
    /** 产品名称 */
    private String productName;
    /** 产品本地名称 */
    private String productNameLocal;
    /** UPC码 */
    private String productUpcCode;
    /** 品牌 */
    private String brand;
    /** 单位毛重(KG) */
    private BigDecimal unitGrossWeight;
    /** 单位体积(CBM) */
    private BigDecimal unitVol;
    /** 数量 */
    private BigDecimal qty;
    /** 行重量(KG) */
    private BigDecimal weight;
    /** 行体积 */
    private BigDecimal vol;
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
    /** 拣货数量 */
    private BigDecimal pickedQty;
    /** 拣货重量(KG) */
    private BigDecimal pickedWeight;
    /** 拣货体积(CBM) */
    private BigDecimal pickedVol;
    /** 库位编码 */
    private String lcCode;
    /** 通道 */
    private String lcAisle;
    /** 货架 */
    private String lcX;
    private String lcY;
    private String lcZ;
    /** 备注 */
    private String memo;
}