package com.telei.wms.project.api.endpoint.goods.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/9 09:55
 */

@Data
public class GoodsAddRequest {
    /**商品名称*/
    @ApiModelProperty(value = "商品名称",example = "篮球aaaaa",position = 1)
    @Check
    private String name;
}
