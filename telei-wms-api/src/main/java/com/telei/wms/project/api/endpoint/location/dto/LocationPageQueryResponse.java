package com.telei.wms.project.api.endpoint.location.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LocationPageQueryResponse {

    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}