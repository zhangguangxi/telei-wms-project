package com.telei.wms.project.api.endpoint.doContainer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 详情Request
 */
@Data
public class DoContainerDetailRequest {

    @ApiModelProperty(value = "柜id", example = "4675353938365515777", position = 1)
    @Check
    @JsonProperty("cId")
    private Long cId;

}