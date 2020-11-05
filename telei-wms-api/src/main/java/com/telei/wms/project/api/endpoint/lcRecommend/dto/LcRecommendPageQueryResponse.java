package com.telei.wms.project.api.endpoint.lcRecommend.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 推荐库位分页查询Response
 */
@Data
public class LcRecommendPageQueryResponse {

    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}