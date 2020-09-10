package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: leo
 * @date: 2020/9/10 15:31
 */
@Data
public class WmsInventoryPageQueryResponseVo {
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
    private BigDecimal bigBagBol;
    /**大包重量*/
    private BigDecimal bigBagWeight;
    /**库位*/
    private String lcCode;
    /**库位类型*/
    private String  lcType;
    /**数量*/
    private BigDecimal qty;
    /**成本单价*/
    private BigDecimal costReference;
    /**库存金额(成本单价 * 数量)*/
    private BigDecimal inventoryAmount;
}
