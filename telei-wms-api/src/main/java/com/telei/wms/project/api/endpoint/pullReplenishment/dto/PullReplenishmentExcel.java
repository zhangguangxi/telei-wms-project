package com.telei.wms.project.api.endpoint.pullReplenishment.dto;


import com.telei.wms.commons.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * @author gongrp
 * @date 2020/6/18 10:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PullReplenishmentExcel {

    @Excel(name = "商品码")
    private String productNo;

    @Excel(name = "商品名称")
    private String productName;

    @Excel(name = "商品条码")
    private String productBarcode;

    @Excel(name = "品牌")
    private String brand;

    @Excel(name = "大包体积")
    private BigDecimal bigBagVol;

    @Excel(name = "大包重量")
    private BigDecimal bigBagWeight;

    @Excel(name = "大包转换率")
    private BigDecimal bigBagQty;

    @Excel(name = "中包转换率")
    private BigDecimal midBagQty;

    @Excel(name = "参考成本")
    private BigDecimal costReference;

    @Excel(name = "出库数量")
    private BigDecimal shipQty;

    @Excel(name = "出库体积")
    private BigDecimal shipVol;

    @Excel(name = "出库重量")
    private BigDecimal shipWeight;

    @Excel(name = "出库大包")
    private BigDecimal shipBigQty;

    @Excel(name = "总体积")
    private BigDecimal sumVol;

    @Excel(name = "总重量")
    private BigDecimal sumWeight;

    @Excel(name = "供应商")
    private String supplierName;

    @Excel(name = "内部供应商", readConverterExp = "0=否,1=是")
    private String internalSupplier;

    @Excel(name = "分类名称")
    private String categoryName;

}
