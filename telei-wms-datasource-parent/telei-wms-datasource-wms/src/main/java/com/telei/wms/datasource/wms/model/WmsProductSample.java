package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * wms_product_sample 样品库位商品存放比例
 */
@Data
public class WmsProductSample implements Entity<Long> {
    /** id */
    private Long id;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 产品id */
    private Long productId;
    /** 样品库位货量上限倍数 */
    private BigDecimal ceilingMultiple;
    /** 样品库位货量下限倍数 */
    private BigDecimal limitMultiple;
}