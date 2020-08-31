package com.telei.wms.project.api.endpoint.goods.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/7/15 16:41
 */
@Data
public class GoodsDetailRequest {
    @ApiModelProperty(value = "商品id")
    @Check
    private Long id;
}
