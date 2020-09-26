package com.telei.wms.customer.amqp.inventoryChangeWriteBack;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/1 14:55
 */
@Data
public class OmsInventoryChangeWriteBack {
    private List<OmsInventoryChangeWriteBackCondition> list;

    @Data
    public static class OmsInventoryChangeWriteBackCondition {
        /**公司id*/
        private Long companyId;
        /** 仓库id */
        private Long warehouseId;
        /** 产品id */
        private Long productId;
        /**调整数量*/
        private BigDecimal qty;
        /**调整类型 1:调多 2:调少*/
        private Integer type;
    }
}
