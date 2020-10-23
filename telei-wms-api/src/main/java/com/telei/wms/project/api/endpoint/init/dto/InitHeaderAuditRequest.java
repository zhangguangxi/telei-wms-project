package com.telei.wms.project.api.endpoint.init.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 库存初始化审核 Request
 */
@Data
public class InitHeaderAuditRequest {

    @ApiModelProperty(value = "库存初始化id", example = "4675353938365515777")
    @Check
    private Long id;

    private String memo;

}