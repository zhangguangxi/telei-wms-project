package com.telei.wms.project.api.endpoint.pao.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 取消入上架Request
 */
@Data
public class PaoHeaderCancelRequest {

    /** id */
    @ApiModelProperty(value = "主键id", example = "4675353938365515777")
    @Check
    private Long id;
}