package com.telei.wms.project.api.endpoint.doHeader.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 出库任务分页查询Response
 */
@Data
public class DoHeaderPageQueryResponse {

    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}