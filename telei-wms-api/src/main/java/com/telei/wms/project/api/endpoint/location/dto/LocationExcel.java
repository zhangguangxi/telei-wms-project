package com.telei.wms.project.api.endpoint.location.dto;


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
public class LocationExcel {

    @Excel(name = "库位编码")
    private String lcCode;
    @Excel(name = "通道")
    private String lcAisle;
    @Excel(name = "货架")
    private String lcX;
    @Excel(name = "层")
    private String lcY;
    @Excel(name = "列")
    private String lcZ;
    @Excel(name = "库位类型", readConverterExp = "S=样品库位,Z=高架库位", combo = "样品库位,高架库位")
    private String lcType;
    @Excel(name = "长")
    private BigDecimal lcLength;
    @Excel(name = "宽")
    private BigDecimal lcWidth;
    @Excel(name = "高")
    private BigDecimal lcHeight;
    @Excel(name = "体积")
    private BigDecimal lcVol;
    @Excel(name = "承重")
    private Integer lcSustainweight;
    @Excel(name = "上架锁", readConverterExp = "0=无锁,1=有锁", combo = "无锁,有锁")
    private String lcPutawaylock;
    @Excel(name = "下架锁", readConverterExp = "0=无锁,1=有锁", combo = "无锁,有锁")
    private String lcPickinglock;
    @Excel(name = "备注")
    private String memo;

}
