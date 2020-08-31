package com.telei.wms.project.api.endpoint.goods.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/7/15 16:32
 */
@Data
public class GoodsUpdateRequest {
    @ApiModelProperty(name = "商品id")
    @Check
    private Long id;

    @ApiModelProperty(name = "商品名称")
    private String name;
}
