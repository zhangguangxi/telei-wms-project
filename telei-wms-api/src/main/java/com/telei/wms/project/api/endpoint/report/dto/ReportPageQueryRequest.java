package com.telei.wms.project.api.endpoint.report.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/9 14:20
 */
@Data
public class ReportPageQueryRequest {

    @ApiModelProperty(value = "当前时区数 东八区：8", example = "8")
    private Integer hour;

    @ApiModelProperty(value = "开始时间", example = "2020-07-01 10:00:00")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-02 10:00:00")
    private String endTime;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;
}
