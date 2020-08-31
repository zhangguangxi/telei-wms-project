package com.telei.wms.project.api.endpoint.ro.dto;

import lombok.Data;

/**
 * 取消入库计划Request
 */
@Data
public class RoHeaderCancelRequest {

    /** 入库计划id */
    private Long rpId;
}