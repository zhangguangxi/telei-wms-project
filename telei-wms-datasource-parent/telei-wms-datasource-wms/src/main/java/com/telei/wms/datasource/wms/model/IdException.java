package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

/**
 * id_exception 数据同步指令异常表
 */
@Data
public class IdException implements Entity<Integer> {
    /** 自增序列 */
    private Integer id;
    /** 指令id */
    private Long idId;
    /** 异常内容 */
    private String exceptionNote;
}