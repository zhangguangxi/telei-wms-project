package com.telei.wms.project.api.endpoint.lcRecommend.dto;

import lombok.Data;

import java.util.List;

/**
 * 删除Request
 */
@Data
public class LcRecommendDeleteRequest {

    /** 公司id */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 订单编号 */
    private String orderCode;
    /** 产品id */
    private List<Long> productIds;
}