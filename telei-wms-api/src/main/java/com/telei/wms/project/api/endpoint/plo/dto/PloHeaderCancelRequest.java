package com.telei.wms.project.api.endpoint.plo.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 取消拣货单Request
 */
@Data
public class PloHeaderCancelRequest {

    /** 主键id */
    @ApiModelProperty(value = "主键id", example = "4675353938365515777")
    @Check
    private Long id;
}