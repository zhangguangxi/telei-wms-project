package com.telei.wms.project.api.endpoint.pullReplenishment.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 拉式补货分页查询Response
 */
@Data
public class PullReplenishmentPageQueryResponse {

    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}