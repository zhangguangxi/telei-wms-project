package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: leo
 * @date: 2020/9/10 15:31
 */
@Data
public class LiftTaskPageQueryResponseVo {
    /**
     * 样品库位商品id
     */
    private Long id;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 仓库id
     */
    private Long warehouseId;
    /**
     * 仓库编码
     */
    private String warehouseCode;
    /**
     * 库位编码
     */
    private String lcCode;
    /**
     * 商品id
     */
    private Long productId;
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
     * UPC码
     */
    private String productUpcCode;
    /**
     * 装箱数
     */
    private BigDecimal bigBagRate;
    /**
     * 大包规格
     */
    private BigDecimal bigBagSpecification;
    /**
     * 中包规格
     */
    private BigDecimal midBagRate;
    /**
     * 大包体积
     */
    private BigDecimal bigBagVol;
    /**
     * 大包重量
     */
    private BigDecimal bigBagWeight;
    /**
     * 库位体积
     */
    private BigDecimal locationVolume;
    /**
     * 库位实际使用体积
     */
    private BigDecimal storageVolume;
    /**
     * 库位理论存放大包数
     */
    private BigDecimal numberLargePackage;
    /**
     * 当前库存
     */
    private BigDecimal ivQty;
    /**
     * 大包数
     */
    private BigDecimal bigCount;
    /**
     * 中包数
     */
    private BigDecimal middleCount;
    /**
     * 小包数
     */
    private BigDecimal smallCount;
    /**
     * 库位上限百分比
     */
    private BigDecimal ceilingMultiple;
    /**
     * 库位下限百分比
     */
    private BigDecimal limitMultiple;
    /**
     * 库位上限大包数
     */
    private BigDecimal inventoryCeilingBigCount;
    /**
     * 库位下限大包数
     */
    private BigDecimal inventoryLimitBigCount;
    /**
     * 升降货类型
     */
    private String lcType;
}
