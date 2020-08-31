package com.telei.wms.project.api.endpoint.roo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 收货单分页查询Request
 */
@Data
public class RooHeaderPageQueryRequest {

    @ApiModelProperty(value = "开始时间", example = "2020-07-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-02 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @ApiModelProperty(value = "单据编号", example = "2525345353")
    private String rooCode;

    @ApiModelProperty(value = "入库单据编号", example = "2525345353")
    private String roCode;

    @ApiModelProperty(value = "收货状态 20-收货成功，99-关闭", example = "20")
    private String roStatus;

    @ApiModelProperty(value = "所属人", example = "2525345353")
    private String ownerUser;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;

}