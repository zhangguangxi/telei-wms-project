package com.telei.wms.project.api.endpoint.location.dto;

import com.nuochen.framework.component.validation.Check;
import lombok.Data;

/**
 * @author gongrp
 */
@Data
public class LocationListRequest {

    /**
     * 通道
     */
    private String lcAisle;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /** 仓库id */
    @Check
    private Long warehouseId;

}