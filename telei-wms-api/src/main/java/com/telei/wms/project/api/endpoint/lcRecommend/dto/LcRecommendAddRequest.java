package com.telei.wms.project.api.endpoint.lcRecommend.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 新增Request
 */
@Data
public class LcRecommendAddRequest {

    /** 公司id */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 预计入库时间（为空即无限大） */
    private Date estArriveTime;
    /** 产品id */
    private List<Long> productIds;
    /** 创建用户 */
    private String createUser;
}