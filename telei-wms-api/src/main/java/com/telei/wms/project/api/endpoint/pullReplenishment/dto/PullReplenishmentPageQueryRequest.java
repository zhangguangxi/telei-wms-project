package com.telei.wms.project.api.endpoint.pullReplenishment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 拉式补货分页查询Request
 */
@Data
public class PullReplenishmentPageQueryRequest {

    @ApiModelProperty(value = "开始时间", example = "2020-07-01 10:00:00")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-02 10:00:00")
    private String endTime;

    @ApiModelProperty(value = "产品编码", example = "2525345353")
    private String productNo;

    @ApiModelProperty(value = "产品名称", example = "锤子")
    private String productName;

    @ApiModelProperty(value = "条码", example = "B12345566")
    private String productBarcode;

    @ApiModelProperty(value = "供应商", example = "供应商")
    private String supplierName;

    @ApiModelProperty(value = "产品分类id", example = "1")
    private Long productCategoryId;

    @ApiModelProperty(value = "产品分类名称", example = "1")
    private String productCategoryName;

    @ApiModelProperty(value = "仓库id", example = "1")
    private Long warehouseId;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;

}