package com.telei.wms.project.api.endpoint.init.dto;

import lombok.Data;

/**
 * 产品检查Response
 */
@Data
public class InitLineCheckResponse extends InitLineDetailCheckRequest {

    /** 状态：0正常，1缺少必要参数，2产品不存在，3库存不足 */
    private String reason;
}