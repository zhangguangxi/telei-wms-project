package com.telei.wms.project.api.endpoint.wmsIdInstantdirective.dto;

import lombok.Data;

@Data
public class WmsIdInstantdirectiveCallbackRequest {

    /** id */
    private Long id;
    /** 异常信息Request */
    private WmsIdExceptionAddRequest idExceptionAddRequest;
}
