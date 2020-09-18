package com.telei.wms.project.api.endpoint.plo.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 新增Request
 */
@Data
public class PloHeaderAddRequest {

    /** 订单id */
    @ApiModelProperty(value = "订单id", example = "4675353938365515777", position = 1)
    @Check
    private Long dohId;
}