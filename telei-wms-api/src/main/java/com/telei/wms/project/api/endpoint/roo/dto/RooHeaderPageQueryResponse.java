package com.telei.wms.project.api.endpoint.roo.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 入库任务分页查询Response
 */
@Data
public class RooHeaderPageQueryResponse {

    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}