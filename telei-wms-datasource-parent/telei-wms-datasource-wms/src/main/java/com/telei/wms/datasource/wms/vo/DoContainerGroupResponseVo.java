package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author gongrp
 */
@Data
public class DoContainerGroupResponseVo {
    private Long dolId;
    private Long productId;
    private String lcCode;
    private BigDecimal cQty;
    private BigDecimal cVol;
    private BigDecimal cWeight;
}
