package com.telei.wms.project.api.endpoint.ro.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 入库任务分页查询Request
 */
@Data
public class RoHeaderPageQueryRequest {

    @ApiModelProperty(value = "开始时间", example = "2020-07-01 10:00:00")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-02 10:00:00")
    private String endTime;

    @ApiModelProperty(value = "业务单据编号", example = "2525345353")
    private String roCode;

    @ApiModelProperty(value = "仓库id", example = "2525345353")
    private Long warehouseId;

    @ApiModelProperty(value = "所属人", example = "2525345353")
    private Long ownerUser;

    @ApiModelProperty(value = "订单状态 01-制单，10-审核，40-部分入库，50-已入库，98-关闭，99-作废", example = "10")
    private String orderStatus;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;
}