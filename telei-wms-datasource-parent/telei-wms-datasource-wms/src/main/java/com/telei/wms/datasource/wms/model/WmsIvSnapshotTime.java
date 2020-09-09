package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.util.Date;
import lombok.Data;

/**
 * wms_iv_snapshot_time 库存快照时间表
 */
@Data
public class WmsIvSnapshotTime implements Entity<Long> {
    /** ivst_id */
    private Long id;
    /** 节点 */
    private String serverNo;
    /** 快照时间日期 */
    private Date snapshotTime;
    /** 当地时间 yyyy-mm-dd hh24:mi:ss */
    private String snapshotLcTime;
    /** end_id */
    private Long ivstEndId;
    /** 发送完成  0 未发送， 1 发送中， 2 发送完成 */
    private Integer sendOver;
}