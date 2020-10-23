package com.telei.wms.project.api.endpoint.plo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 新增Response
 */
@Data
public class PloAddResponse {

    /**是否成功*/
    @ApiModelProperty(value = "是", example = "true/false")
    private Boolean isSuccess = true;

    /** 主键id */
    @ApiModelProperty(value = "主键id", example = "4675353938365515777")
    private Long id;
}
