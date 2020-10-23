package com.telei.wms.project.api.endpoint.location.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collection;

/**
 * 详情request
 */
@Data
public class LocationDeleteRequest {

    /** 主键id */
    @ApiModelProperty(value = "主键id", example = "[4675353938365515777,4675353938365515777]", position = 1)
    @Check
    private Collection<Long> ids;
}