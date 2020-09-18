package com.telei.wms.datasource.wms.vo;

import lombok.Data;

/**
 * 入库任务分页查询
 */
@Data
public class DoHeaderPageQueryRequestVo {

    /** 业务单据编号-按单据编码规则生成 */
    private String roCode;
    /** 开始时间 */
    private String startTime;
    /** 结束时间 */
    private String endTime;
    /** 仓库id */
    private Long warehouseId;
    /** 所属人,存储account_id */
    private Long ownerUser;
    /** 订单状态 01-制单，10-审核，40-部分入库，50-已入库，98-关闭，99-作废 */
    private String orderStatus;
}
