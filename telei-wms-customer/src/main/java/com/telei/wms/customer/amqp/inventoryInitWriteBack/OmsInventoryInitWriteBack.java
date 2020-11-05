package com.telei.wms.customer.amqp.inventoryInitWriteBack;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/1 14:55
 */
@Data
public class OmsInventoryInitWriteBack {

    private List<OmsInventory> omsInventoryList;

    private String fromCode;

    @Data
    public static class OmsInventory {
        /** 公司编码 */
        private Long companyId;
        /** 仓库id */
        private Long warehouseId;
        /** 仓库code */
        private String warehouseCode;
        /** 产品id */
        private Long productId;
        /** 数量 */
        private BigDecimal qty;
    }

}
