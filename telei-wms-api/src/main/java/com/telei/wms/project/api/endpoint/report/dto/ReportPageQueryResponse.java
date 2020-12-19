package com.telei.wms.project.api.endpoint.report.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/9 14:19
 */
@Data
public class ReportPageQueryResponse {
    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}
