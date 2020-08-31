package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.util.Date;
import lombok.Data;

/**
 * wms_id_instantdirective 指令表，根据不同指令进行不同操作
 */
@Data
public class WmsIdInstantdirective implements Entity<Long> {
    /** id */
    private Long id;
    /** 指令类型，PUTON 上架 ，SHIP  出库签出 */
    private String idtCode;
    /** 指令操作的单据编号 */
    private String idRefCode;
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
    /** 指令执行次数，默认0次 */
    private Integer processCount;
}