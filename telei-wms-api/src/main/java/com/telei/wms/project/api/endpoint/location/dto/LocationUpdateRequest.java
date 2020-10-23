package com.telei.wms.project.api.endpoint.location.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author gongrp
 */
@Data
public class LocationUpdateRequest extends LocationCommonRequest {

    @ApiModelProperty(value = "库位id",example = "2",position = 1)
    @Check
    private Long locationId;

}