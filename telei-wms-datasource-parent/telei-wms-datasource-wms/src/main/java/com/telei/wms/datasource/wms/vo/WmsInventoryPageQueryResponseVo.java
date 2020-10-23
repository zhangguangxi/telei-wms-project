package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: leo
 * @date: 2020/9/10 15:31
 */
@Data
public class WmsInventoryPageQueryResponseVo {
    /**公司id*/
    private Long companyId;
    /**仓库id*/
    private Long warehouseId;
    /**仓库编码*/
    private String warehouseCode;
    /**库位编码*/
    private String lcCode;
    /**商品id*/
    private Long productId;
    /**商品码*/
    private String productNo;
    /**商品名称*/
    private String productName;
    /**条码*/
    private String productBarcode;
    /**中包转换率*/
    private BigDecimal bigBagRate;
    /**大包转换率*/
    private BigDecimal midBagRate;
    /**装箱数*/
    private BigDecimal boxQty;
    /**大包体积*/
    private BigDecimal bigBagVol;
    /**大包重量*/
    private BigDecimal bigBagWeight;
    /**库位类型*/
    private String  lcType;
    /**数量*/
    private BigDecimal qty;
    /**成本单价*/
    private BigDecimal costReference;
}
