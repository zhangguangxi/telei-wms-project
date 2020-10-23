package com.telei.wms.datasource.wms.vo;

import lombok.Data;

/**
 * 拣货单分页查询
 */
@Data
public class PloHeaderPageQueryRequestVo {

    /** 业务单据编号 */
    private String ploCode;
    /** 任务单据编号 */
    private String dohCode;
    /** 拣货员 */
    private String plUser;
    /** 开始时间 */
    private String startTime;
    /** 结束时间 */
    private String endTime;
    /** 仓库id */
    private Long warehouseId;
    /** 拣货单状态  01-制单，20-部分拣货，30-已拣货，98-关闭 */
    private String orderStatus;
    /** 公司id */
    private Long companyId;
}
