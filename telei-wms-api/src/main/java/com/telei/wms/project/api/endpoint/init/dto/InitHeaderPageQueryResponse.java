package com.telei.wms.project.api.endpoint.init.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 库存初始化分页查询Response
 */
@Data
public class InitHeaderPageQueryResponse {

    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}