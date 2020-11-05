package com.telei.wms.datasource.wms.vo;

import lombok.Data;

/**
 * 推荐库位分页查询
 */
@Data
public class LcRecommendPageQueryRequestVo {

    /** 开始时间 */
    private String startTime;
    /** 结束时间 */
    private String endTime;
    /** 创建人 */
    private String createUser;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 公司id */
    private Long companyId;
}
