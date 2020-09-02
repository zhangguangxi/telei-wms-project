package com.telei.wms.project.api.endpoint.pao.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 上架单分页查询Response
 */
@Data
public class PaoHeaderPageQueryResponse {

    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}