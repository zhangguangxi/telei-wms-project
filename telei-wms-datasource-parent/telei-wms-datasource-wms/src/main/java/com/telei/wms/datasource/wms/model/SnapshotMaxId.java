package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

/**
 * snapshot_max_id 库存快照指令执行的最大id表
 */
@Data
public class SnapshotMaxId implements Entity<Long> {
    private Long id;
    private Long spMaxId;
}