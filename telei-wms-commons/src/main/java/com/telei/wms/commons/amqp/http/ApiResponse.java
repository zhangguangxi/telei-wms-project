package com.telei.wms.commons.amqp.http;

import lombok.Data;

/**
 * 回调response
 */
@Data
public class ApiResponse {

    private String code;
    private String message;
    private DataResponse data;
}
