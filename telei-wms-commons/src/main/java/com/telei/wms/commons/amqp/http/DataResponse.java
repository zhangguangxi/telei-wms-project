package com.telei.wms.commons.amqp.http;

import lombok.Data;

/**
 * 回调response
 */
@Data
public class DataResponse {

    /**是否成功*/
    private Boolean isSuccess = false;
}
