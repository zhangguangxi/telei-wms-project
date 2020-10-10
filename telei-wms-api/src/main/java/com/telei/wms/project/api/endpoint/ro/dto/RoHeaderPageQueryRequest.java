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

    @ApiModelProperty(value = "所属人名称", example = "2525345353")
    private String userName;

    @ApiModelProperty(value = "创建用户", example = "2525345353")
    private String createUser;

    @ApiModelProperty(value = "商家订单号（采购单编号）", example = "2525345353")
    private String custOrderNo;

    @ApiModelProperty(value = "订单状态 01-制单，10-审核，40-部分入库，50-已入库，98-关闭，99-作废", example = "10")
    private String orderStatus;

    @ApiModelProperty(value = "0 没有打印过入库任务,1 打印过入库任务", example = "0")
    private String hadPrintTo;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;
}