package com.telei.wms.project.api.endpoint.pao.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 增删改基础response
 */
@Data
public class PaoCudBaseResponse {

    /**是否成功*/
    @ApiModelProperty(value = "是", example = "true/false", position = 1)
    private Boolean isSuccess = true;
}
