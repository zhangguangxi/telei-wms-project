package com.telei.wms.project.api.endpoint.ro.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 入库任务分页查询Response
 */
@Data
public class RoHeaderPageQueryResponse {

    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}