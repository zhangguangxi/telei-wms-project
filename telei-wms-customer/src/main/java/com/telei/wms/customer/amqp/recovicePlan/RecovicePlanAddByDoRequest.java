package com.telei.wms.customer.amqp.recovicePlan;

import com.nuochen.framework.component.validation.Check;
import lombok.Data;

/**
 * 出库任务创建计划Request
 */
@Data
public class RecovicePlanAddByDoRequest {

    /** 采购单id */
    @Check
    private Long poId;
}
