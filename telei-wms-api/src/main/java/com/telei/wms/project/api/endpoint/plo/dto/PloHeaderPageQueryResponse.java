package com.telei.wms.project.api.endpoint.plo.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 拣货单分页查询Response
 */
@Data
public class PloHeaderPageQueryResponse {

    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}