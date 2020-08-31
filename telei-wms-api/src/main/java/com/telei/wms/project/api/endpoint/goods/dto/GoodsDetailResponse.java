package com.telei.wms.project.api.endpoint.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/7/15 16:40
 */
@Data
public class GoodsDetailResponse {
    @ApiModelProperty(value = "商品id")
    private Long id;
    @ApiModelProperty(value = "商品名称")
    private String name;
}
