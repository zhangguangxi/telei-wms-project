package com.telei.wms.commons.utils;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: leo
 * @Date: 2020/6/23 17:53
 */
@Data
public class PageCommonRequest {
    /**当前页*/
    @ApiModelProperty(value = "页码",example = "1",position = 1)
    @Check
    private Integer pageNumber;
    /**每页记录数*/
    @ApiModelProperty(value = "每页记录数",example = "10",position = 2)
    @Check
    private Integer pageSize;
}
