package com.telei.wms.project.api.endpoint.wmsIdInstantdirective.dto;

import lombok.Data;

/**
 * 指令异常Request
 */
@Data
public class WmsIdExceptionAddRequest {

    /** 指令id */
    private Long idId;
    /** 异常内容 */
    private String exceptionNote;
}