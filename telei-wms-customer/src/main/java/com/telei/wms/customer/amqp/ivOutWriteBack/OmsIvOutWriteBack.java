package com.telei.wms.customer.amqp.ivOutWriteBack;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/23 09:22
 */
@Data
public class OmsIvOutWriteBack {
    private List<OmsIvOutWriteBackCondition> list;

    @Data
    public static  class OmsIvOutWriteBackCondition{
        /**公司id*/
        private Long companyId;
        /**仓库id*/
        private Long warehouseId;
        /**产品id*/
        private Long productId;
        /**库位*/
        private String lcCode;
        /**出库计划id*/
        private Long spId;
        /**出库计划明细id*/
        private Long spdId;
        /**销售单id*/
        private Long soId;
        /**销售单明细id*/
        private Long sodId;
        /**待出库存id*/
        private Long ivoId;
        /** 明细id(出库任务) */
        private Long lineId;
        /**数量*/
        private BigDecimal qty;
    }
}
