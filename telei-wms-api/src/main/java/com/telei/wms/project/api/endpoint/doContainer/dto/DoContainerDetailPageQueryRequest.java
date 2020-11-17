package com.telei.wms.project.api.endpoint.doContainer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 出库任务装箱信息分页查询Request
 */
@Data
public class DoContainerDetailPageQueryRequest {

    @ApiModelProperty(value = "出库计划id", example = "2525345353")
    @JsonProperty("dohId")
    @Check
    private Long dohId;

    @ApiModelProperty(value = "货柜id", example = "2525345353")
    @JsonProperty("cId")
    @Check
    private Long cId;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;
}