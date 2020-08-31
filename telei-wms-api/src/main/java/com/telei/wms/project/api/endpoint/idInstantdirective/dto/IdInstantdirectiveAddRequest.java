package com.telei.wms.project.api.endpoint.idInstantdirective.dto;

import lombok.Data;

@Data
public class IdInstantdirectiveAddRequest {

    /** 同步数据表 */
    private String idTable;
    /** 操作类型 ADD,DEL,MOD */
    private String operationCode;
    /** 同步记录id，多记录可为空 */
    private Long idRowId;
    /** 单据作业提交数据 */
    private Object orderContext;
    /** 指令备注 */
    private String idNote;
}
