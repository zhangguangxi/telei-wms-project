package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.util.Date;
import lombok.Data;

/**
 * id_instantdirective 数据同步指令表
 */
@Data
public class IdInstantdirective implements Entity<Long> {
    /** id */
    private Long id;
    /** 来源节点，CENTER、HANGHZHOU、FRANKFURT */
    private String fromNode;
    /** 目标节点，CENTER、HANGHZHOU、FRANKFURT */
    private String targetNode;
    /** 同步数据子系统，CDM WMS OMS 等 */
    private String idSystem;
    /** 同步数据表 */
    private String idTable;
    /** 同步记录id，多记录可为空 */
    private Long idRowId;
    /** 操作类型 ADD,DEL,MOD */
    private String operationCode;
    /** 单据作业提交数据 */
    private String orderContext;
    /** 指令状态  O-Open，Y-Yes，F-	Fail */
    private String isCode;
    /** 指令创建时间 */
    private Date createTime;
    /** 指令完成时间 */
    private Date finishTime;
    /** 指令备注 */
    private String idNote;
    /** 指令结果 */
    private String idResult;
    /** 发送MQ次数，默认0次 */
    private Integer sendCount;
    /** 指令执行次数，默认0次 */
    private Integer processCount;
}