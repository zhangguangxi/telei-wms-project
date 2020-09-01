package com.telei.wms.project.api.endpoint.init.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 库存初始化分页查询Request
 */
@Data
public class InitHeaderPageQueryRequest {

    @ApiModelProperty(value = "开始时间", example = "2020-07-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-02 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @ApiModelProperty(value = "单据编号", example = "2525345353")
    private String ivihCode;

    @ApiModelProperty(value = "仓库id", example = "2525345353")
    private Long warehouseId;

    @ApiModelProperty(value = "仓库编码", example = "SZ")
    private String warehouseCode;

    @ApiModelProperty(value = "库存初始化状态 01-制单，20-审核，98-关闭", example = "20")
    private String ivihStatus;

    @ApiModelProperty(value = "所属人", example = "admin")
    private String createUser;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;

}