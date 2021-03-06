package com.telei.wms.project.api.endpoint.report.dto;

import com.nuochen.framework.autocoding.domain.Pagination;
import lombok.Data;

/**
 * @author gongrp
 * @date 2020/6/10 16:52
 */
@Data
public class ReportBusinessPageQueryResponse {
    /**分页信息*/
    private Pagination page;
}
