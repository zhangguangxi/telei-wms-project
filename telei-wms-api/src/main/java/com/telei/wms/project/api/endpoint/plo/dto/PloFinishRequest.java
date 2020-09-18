package com.telei.wms.project.api.endpoint.plo.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 拣货完毕Request
 */
@Data
public class PloFinishRequest {

    /** 单据id */
    @ApiModelProperty(value = "单据id", example = "4675353938365515777")
    @Check
    private Long id;
}