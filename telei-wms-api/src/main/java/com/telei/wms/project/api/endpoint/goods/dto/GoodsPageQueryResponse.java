package com.telei.wms.project.api.endpoint.goods.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/9 14:19
 */
@Data
public class GoodsPageQueryResponse {
    /*分页信息**/
    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}
