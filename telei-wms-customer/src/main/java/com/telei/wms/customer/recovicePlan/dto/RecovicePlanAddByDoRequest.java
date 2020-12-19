package com.telei.wms.customer.recovicePlan.dto;

import com.nuochen.framework.component.validation.Check;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 出库任务创建计划Request
 */
@Data
public class RecovicePlanAddByDoRequest {

    /** 采购单id */
    @Check
    private Long poId;

    @Check
    private List<RecovicePlanAddByDoRequest.Detail> details;

    @Data
    public static class Detail {

        /** 产品id */
        @Check
        private Long productId;

        /** 数量 */
        @Check
        private BigDecimal planQty;
    }
}
