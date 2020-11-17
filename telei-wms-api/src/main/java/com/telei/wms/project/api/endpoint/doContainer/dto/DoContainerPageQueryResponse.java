package com.telei.wms.project.api.endpoint.doContainer.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 出库任务分页查询Response
 */
@Data
public class DoContainerPageQueryResponse {

    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}