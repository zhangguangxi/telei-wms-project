package com.telei.wms.customer.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author gongrp
 * @date 2020/6/10 16:41
 */
@Data
public class ProductDetailResponse {
    /** 主键id */
    private Long id;
    /** 来源主键id */
    private Long fromId;
    /** 公司id */
    private Long companyId;
    /** 产品id */
    private Long productId;
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
    /** 产品分类 */
    private Long productCategoryId;
    /** 产品分类名称 */
    private String productCategoryName;
    /** 规格型号 */
    private String specType;
    /** 品牌 */
    private String brand;
    /** 产地 */
    private String placeOfOrigin;
    /** 毛重(G) */
    private BigDecimal unitGrossWeight;
    /** 长(CM) */
    private BigDecimal productLength;
    /** 宽(CM) */
    private BigDecimal productWidth;
    /** 高(CM) */
    private BigDecimal productHeight;
    /** 单位体积(CBM) */
    private BigDecimal unitVol;
    /** 计量单位 */
    private Long stockUnit;
    /** 产品颜色 */
    private String productColor;
    /** 产品尺寸 */
    private String productSize;
    /** 参考成本 */
    private BigDecimal costReference;
    /** 成本币种 */
    private Integer currencyId;
    /** 建议售价 */
    private BigDecimal sellingPriceReference;
    /** 售价币种 */
    private Integer sellingCurrencyId;
    /** 材质 */
    private String texture;
    /** 备注 */
    private String memo;
    /** 状态 */
    private Integer status;
    /** 上架状态，1上架，0暂不上架 */
    private Integer saleStatus;
    /** 图片路径 */
    private String imagePath;
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
    /** 原记录创建公司id */
    private Long createCompanyId;
    /** 创建时间 */
    private Date createTime;
    /** 创建人 */
    private String createUser;
    /** 初次启用时间 */
    private Date initialActivationTime;
    /** 最后更新时间 */
    private Date lastUpdateTime;
    /** 最后更新人 */
    private String lastUpdateUser;

}
