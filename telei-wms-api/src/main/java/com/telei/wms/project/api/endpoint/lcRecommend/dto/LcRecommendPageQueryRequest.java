package com.telei.wms.project.api.endpoint.lcRecommend.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 推荐库位分页查询Request
 */
@Data
public class LcRecommendPageQueryRequest {

    @ApiModelProperty(value = "开始时间", example = "2020-07-01 10:00:00")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-02 10:00:00")
    private String endTime;

    @ApiModelProperty(value = "创建人", example = "Dean")
    private String createUser;

    @ApiModelProperty(value = "仓库id", example = "2525345353")
    private Long warehouseId;

    @ApiModelProperty(value = "仓库code", example = "2525345353")
    private String warehouseCode;

    @ApiModelProperty(value = "类型：0全部，1有库位，2没有库位", example = "0")
    private Integer type;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    @Check
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    @Check
    private Integer pageSize;
}