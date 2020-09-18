package com.telei.wms.project.api.endpoint.ro.dto;

import lombok.Data;

/**
 * 获取是否可以强制关闭Request
 */
@Data
public class RoHeaderUpdateEnforcementRequest {

    /** 入库计划id */
    private Long rpId;
}