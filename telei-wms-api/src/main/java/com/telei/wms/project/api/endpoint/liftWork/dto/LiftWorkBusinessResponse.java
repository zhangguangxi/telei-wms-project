package com.telei.wms.project.api.endpoint.liftWork.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author gongrp
 * @date 2020/6/10 16:41
 */
@Data
public class LiftWorkBusinessResponse extends LiftWorkCommonResponse {

    private String sampleLcCode;

    private String prepLcCode;

    private BigDecimal qty;

}
