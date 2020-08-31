package com.telei.wms.project.api.endpoint.wmsIdInstantdirective.dto;

import lombok.Data;

/**
 * 回调response
 */
@Data
public class WmsIdInstantdirectiveCallbackResponse {

    private String code;
    private String message;
    private WmsIdInstantdirectiveCallbackDataResponse data;
}
