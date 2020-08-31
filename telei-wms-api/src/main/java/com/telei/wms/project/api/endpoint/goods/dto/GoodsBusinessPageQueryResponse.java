package com.telei.wms.project.api.endpoint.goods.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import lombok.Data;

/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/9 14:23
 */
@Data
public class GoodsBusinessPageQueryResponse {
    /*分页信息**/
    private Pagination page;
}
