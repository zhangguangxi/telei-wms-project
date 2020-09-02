package com.telei.wms.datasource.wms.vo;

import lombok.Data;

/**
 * 上架单分页查询
 */
@Data
public class PaoHeaderPageQueryRequestVo {

    /** 开始时间 */
    private String startTime;
    /** 结束时间 */
    private String endTime;
    /** 收货单号 */
    private String rooCode;
    /** 业务单据编号-按单据编码规则生成 */
    private String paoCode;
    /** 上架用户 */
    private String putawayUser;
    /** 状态  01-制单，10-部分上架，20-已上架，98-关闭，99-作废 */
    private String paoStatus;
}
