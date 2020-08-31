package com.telei.wms.project.api.endpoint.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/9 09:38
 */
@Data
public class GoodsAddResponse {
    /**是否成功*/
    @ApiModelProperty(value = "是",example = "true/false",position = 1)
    private Boolean isSuccess;
}
