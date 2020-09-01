package com.telei.wms.customer.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * @author gongrp
 * @date 2020/6/10 16:41
 */
@Data
public class ProductDetailRequest {
    /** 产品id列表 */
    private Collection<Long> ids;
    /**商品名称*/
    private String productName;
    /** 产品分类 */
    private Long productCategoryId;
    /** 公司id */
    private Long companyId;
    /**规格*/
    private String specType;
    /**体积*/
    private BigDecimal unitVol;
    /**品牌*/
    private String brand;
    /**重量*/
    private BigDecimal unitGrossWeight;
    /**条码*/
    private String productBarcode;
    /**状态*/
    private Integer status;
}
