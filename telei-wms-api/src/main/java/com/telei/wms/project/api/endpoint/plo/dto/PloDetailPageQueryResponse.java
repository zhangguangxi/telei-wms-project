package com.telei.wms.project.api.endpoint.plo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 拣货明细查询Response
 */
@Data
public class PloDetailPageQueryResponse {

    /** id */
    private Long id;
    /** 单头id */
    private Long ploId;
    /** 业务单据编号 */
    private String ploCode;
    /** 明细id */
    private Long plolId;
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
    /** 拣货数量 */
    private BigDecimal pickQty;
    /** 总重量(KG) */
    private BigDecimal pickWeight;
    /** 总体积(CBM) */
    private BigDecimal pickVol;
    /** 创建用户 */
    private String createUser;
    /** 创建时间 */
    private Date createTime;
}