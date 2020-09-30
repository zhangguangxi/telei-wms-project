package com.telei.wms.project.api.endpoint.liftTask.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author gongrp
 * @date 2020/6/10 16:52
 */
@Data
public class WmsProductSampleCommonRequest {

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
