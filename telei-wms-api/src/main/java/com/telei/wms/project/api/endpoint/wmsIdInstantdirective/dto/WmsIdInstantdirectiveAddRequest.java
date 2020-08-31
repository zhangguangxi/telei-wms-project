package com.telei.wms.project.api.endpoint.wmsIdInstantdirective.dto;

import lombok.Data;

@Data
public class WmsIdInstantdirectiveAddRequest {

    /** 指令类型，PUTON 上架 ，SHIP  出库签出 */
    private String idtCode;
    /** 指令操作的单据编号 */
    private String idRefCode;
    /** 单据作业提交数据 */
    private Object orderContext;
    /** 指令备注 */
    private String idNote;
}
