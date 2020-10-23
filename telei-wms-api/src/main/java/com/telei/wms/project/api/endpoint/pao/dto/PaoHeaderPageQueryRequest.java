package com.telei.wms.project.api.endpoint.pao.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 上架单分页查询Request
 */
@Data
public class PaoHeaderPageQueryRequest {

    @ApiModelProperty(value = "开始时间", example = "2020-07-01 10:00:00")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-02 10:00:00")
    private String endTime;

    @ApiModelProperty(value = "收货单号", example = "2525345353")
    private String rooCode;

    @ApiModelProperty(value = "上架单编号", example = "2525345353")
    private String paoCode;

    @ApiModelProperty(value = "状态  01-制单，10-部分上架，20-已上架，98-关闭，99-作废", example = "10")
    private String paoStatus;

    @ApiModelProperty(value = "上架用户", example = "Dean")
    private String putawayUser;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    @Check
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    @Check
    private Integer pageSize;
}