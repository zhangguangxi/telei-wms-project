package com.telei.wms.project.api.endpoint.doHeader.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 详情Request
 */
@Data
public class DoDetailRequest {

    /** 主键id */
    @ApiModelProperty(value = "主键id", example = "4675353938365515777", position = 1)
    @Check
    private Long id;
}