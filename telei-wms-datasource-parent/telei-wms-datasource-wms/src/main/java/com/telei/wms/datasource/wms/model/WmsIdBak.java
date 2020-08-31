package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.util.Date;
import lombok.Data;

/**
 * wms_id_bak 指令备份表，指令移除至此表
 */
@Data
public class WmsIdBak implements Entity<Long> {
    /** 自增序列 */
    private Long id;
    /** 指令id */
    private Long idId;
    /** 指令类型 */
    private String idtCode;
    /** 指令要操作的单据编号 */
    private String idRefCode;
    /** 单据作业数据 */
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
    /** 指令执行次数，默认0次 */
    private Integer processCount;
    /** 指令备份时间 */
    private Date bakTime;
}