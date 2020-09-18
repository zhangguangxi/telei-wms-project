package com.telei.wms.project.api.endpoint.plo.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 拣货明细分页查询Request
 */
@Data
public class PloDetailPageQueryRequest {

    /** 单头id */
    @ApiModelProperty(value = "单头id", example = "4675353938365515777")
    @Check
    private Long ploId;
}