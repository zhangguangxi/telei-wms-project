package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

/**
 * wms_id_exception 指令异常信息表
 */
@Data
public class WmsIdException implements Entity<Long> {
    /** 自增序列 */
    private Long id;
    /** 指令id */
    private Long idId;
    /** 异常内容 */
    private String exceptionNote;
}