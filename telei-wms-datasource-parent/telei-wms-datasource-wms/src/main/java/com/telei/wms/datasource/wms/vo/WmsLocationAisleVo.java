package com.telei.wms.datasource.wms.vo;

import lombok.Data;

/**
 * @author: leo
 * @date: 2020/9/22 10:56
 */
@Data
public class WmsLocationAisleVo {

    private String lcX1;
    private String lcY1;
    private String lcZ1;
    /**产品分类数量*/
    private Integer productCount1;
    /**产品库存*/
    private Integer qty1;
    /**库存类型 S：样品库位 Z：高库位*/
    private String lcType1;
    private String lcCode1;
    private String lcExist1;

    private String lcX2;
    private String lcY2;
    private String lcZ2;
    /**产品分类数量*/
    private Integer productCount2;
    /**产品库存*/
    private Integer qty2;
    /**库存类型 S：样品库位 Z：高库位*/
    private String lcType2;
    private String lcCode2;
    private String lcExist2;

    private String lcX3;
    private String lcY3;
    private String lcZ3;
    /**产品分类数量*/
    private Integer productCount3;
    /**产品库存*/
    private Integer qty3;
    /**库存类型 S：样品库位 Z：高库位*/
    private String lcType3;
    private String lcCode3;
    private String lcExist3;
}
