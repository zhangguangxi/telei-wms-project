package com.telei.wms.project.api.endpoint.doContainer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DoContainerRevokeRequest {

    @ApiModelProperty(value = "出库任务id", example = "4675353938365515777")
    @JsonProperty("dohId")
    @Check
    private Long dohId;

    @ApiModelProperty(value = "货柜id", example = "4675353938365515777")
    @JsonProperty("cId")
    @Check
    private Long cId;

}