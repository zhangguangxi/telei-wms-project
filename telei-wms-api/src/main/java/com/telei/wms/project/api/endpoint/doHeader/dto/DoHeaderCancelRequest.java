package com.telei.wms.project.api.endpoint.doHeader.dto;

import lombok.Data;

/**
 * 取消出库计划Request
 */
@Data
public class DoHeaderCancelRequest {

    /** 出库计划id */
    private Long spId;
}