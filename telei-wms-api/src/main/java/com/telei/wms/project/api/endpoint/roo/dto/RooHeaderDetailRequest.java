package com.telei.wms.project.api.endpoint.roo.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 详情request
 */
@Data
public class RooHeaderDetailRequest {

    /** 主键id */
    @ApiModelProperty(value = "主键id", example = "4675353938365515777", position = 1)
    @Check
    private Long id;
}