package com.telei.wms.project.api.endpoint.lcRecommend.dto;

import lombok.Data;

/**
 * 更新Request
 */
@Data
public class LcRecommendUpdateRequest {

    /** id */
    private Long id;
    /** 库位编码 */
    private String lcCode;
}