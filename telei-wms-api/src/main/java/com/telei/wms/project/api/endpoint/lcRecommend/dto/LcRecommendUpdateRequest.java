package com.telei.wms.project.api.endpoint.lcRecommend.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 更新Request
 */
@Data
public class LcRecommendUpdateRequest {

    /**  */
    @ApiModelProperty(value = "id", example = "2353535353")
    @Check
    private Long id;
    /** 库位编码 */
    @ApiModelProperty(value = "库位编码", example = "1-s")
    @Check
    private String lcCode;
}