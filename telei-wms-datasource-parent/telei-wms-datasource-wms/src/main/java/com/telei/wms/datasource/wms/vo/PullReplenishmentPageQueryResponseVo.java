package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: leo
 * @date: 2020/9/10 15:31
 */
@Data
public class PullReplenishmentPageQueryResponseVo {
    /**
     * 商品图片
     */
    private String imagePath;
    /**
     * 商品码
     */
    private String productNo;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 条码
     */
    private String productBarcode;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 商品id
     */
    private Long productId;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 大包体积
     */
    private BigDecimal bigBagVol;
    /**
     * 大包重量
     */
    private BigDecimal bigBagWeight;
    /**
     * 大包转换率
     */
    private BigDecimal bigBagQty;
    /**
     * 中包转换率
     */
    private BigDecimal midBagQty;

    /**
     * 参考成本
     */
    private BigDecimal costReference;
    /**
     * 出库数量
     */
    private BigDecimal shipQty;
    /**
     * 出库体积
     */
    private BigDecimal shipVol;
    /**
     * 出库重量
     */
    private BigDecimal shipWeight;
    /**
     * 出库大包
     */
    private BigDecimal shipBigQty;
    /**
     * 总体积
     */
    private BigDecimal sumVol;
    /**
     * 总重量
     */
    private BigDecimal sumWeight;
    /**
     * 供应商
     */
    private String supplierName;
    /**
     * 供应商id
     */
    private Long supplierId;
    /**
     * 内部供应商
     */
    private String internalSupplier;
    /**
     * 分类名称
     */
    private String categoryName;
}
