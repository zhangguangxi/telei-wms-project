package com.telei.wms.project.api.endpoint.pao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 上架单分页查询Request
 */
@Data
public class PaoHeaderPageQueryRequest {

    @ApiModelProperty(value = "开始时间", example = "2020-07-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-02 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
    @Check
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    @Check
    private Integer pageSize;
}