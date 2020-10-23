package com.telei.wms.project.api.endpoint.doHeader.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 出库任务分页查询Request
 */
@Data
public class DoHeaderPageQueryRequest {

    @ApiModelProperty(value = "开始时间", example = "2020-07-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-02 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @ApiModelProperty(value = "业务单据编号", example = "2525345353")
    private String dohCode;

    @ApiModelProperty(value = "仓库id", example = "2525345353")
    private Long warehouseId;

    @ApiModelProperty(value = "出库计划id", example = "2525345353")
    private Long spId;

    @ApiModelProperty(value = "公司id[默认不传]", example = "2525345353")
    private Long companyId;

    @ApiModelProperty(value = "订单状态 01-待处理，02-已核验， 30-已拣货， 40-已出库", example = "01")
    private String orderStatus;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;
}