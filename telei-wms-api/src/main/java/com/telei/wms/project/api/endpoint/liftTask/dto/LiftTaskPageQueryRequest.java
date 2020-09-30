package com.telei.wms.project.api.endpoint.liftTask.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 升降任务分页查询Request
 */
@Data
public class LiftTaskPageQueryRequest {

    @ApiModelProperty(value = "产品编码", example = "2525345353")
    private String productNo;

    @ApiModelProperty(value = "产品名称", example = "锤子")
    private String productName;

    @ApiModelProperty(value = "条码", example = "B12345566")
    private String productBarcode;

    @ApiModelProperty(value = "库位", example = "S1-25-15")
    private String lcCode;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;

}