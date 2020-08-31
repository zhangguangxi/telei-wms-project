package com.telei.wms.project.api.endpoint.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/9 14:20
 */
@Data
public class GoodsPageQueryRequest {
    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;
}
