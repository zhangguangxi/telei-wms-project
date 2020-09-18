package com.telei.wms.project.api.endpoint.plo.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 拣货单分页查询Request
 */
@Data
public class PloHeaderPageQueryRequest {

    @ApiModelProperty(value = "开始时间", example = "2020-07-01 10:00:00")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-02 10:00:00")
    private String endTime;

    @ApiModelProperty(value = "业务单据编号", example = "2525345353")
    private String ploCode;

    @ApiModelProperty(value = "仓库id", example = "2525345353")
    private Long warehouseId;

    @ApiModelProperty(value = "拣货单状态  01-制单，20-部分拣货，30-已拣货，98-关闭", example = "10")
    private String orderStatus;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    @Check
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    @Check
    private Integer pageSize;
}