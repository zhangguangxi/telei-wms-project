package com.telei.wms.customer.amqp.inventoryWriteBack;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/1 14:55
 */
@Data
public class OmsInventoryWriteBack {
    private List<OmsInventoryWriteBackCondition> list;

    @Data
    public static class OmsInventoryWriteBackCondition{
        /** 入库计划id */
        private Long rpId;
        /** 入库计划单明细id */
        private Long rpdId;
        /** 采购单id */
        private Long poId;
        /** 采购单明细id */
        private Long podId;
        /**上架(入库)数量*/
        private BigDecimal ivQty;
    }
}
