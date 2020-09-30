package com.telei.wms.project.api.endpoint.liftTask.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 升降任务分页查询Response
 */
@Data
public class LiftTaskPageQueryResponse {

    @ApiModelProperty(value = "分页信息")
    private Pagination page;
}