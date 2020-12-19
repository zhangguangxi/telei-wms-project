package com.telei.wms.customer.amqp.inventoryAddWriteBack;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/1 14:55
 */
@Data
public class OmsInventoryAddWriteBack {
    /**入库类型 01-补货入库 02-内部订单 08-销售退货*/
    private String orderType;

    private List<OmsInventoryAddWriteBackCondition> list;

    @Data
    public static class OmsInventoryAddWriteBackCondition {
        /**公司id */
        private Long companyId;
        /**仓库id*/
        private Long warehouseId;
        /** 入库计划id */
        private Long rpId;
        /** 入库计划单明细id */
        private Long rpdId;
        /** 采购单id */
        private Long poId;
        /** 采购单明细id */
        private Long podId;
        /**产品ID*/
        private Long productId;
        /**上架(入库)数量*/
        private BigDecimal ivQty;
    }
}
