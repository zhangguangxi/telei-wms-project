package com.telei.wms.project.api.endpoint.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/8/26 10:04
 */
@Data
public class InventoryAddBussinessRequest {
    /**上架记录*/
    private List<InventoryAddRequestCondition> list;

    @Data
    public static  class InventoryAddRequestCondition {
        /**公司id*/
        private Long companyId;
        /** 仓库id */
        private Long warehouseId;
        /** 仓库code */
        private String warehouseCode;
        /**库位编码*/
        private String lcCode;
        /** 库存数量 */
        private BigDecimal ivQty;
        /** 商家编码 */
        private String customerId;
        /** 产品序列码 */
        private Long productId;
        /** 库存批次id */
        private Long iabId;
        /** 中包数量 */
        private BigDecimal midBagQty;
        /** 大包数量 */
        private BigDecimal bigBagQty;
        /**上架单单头id*/
        private Long paoId;
        /**上架单明细id*/
        private Long paolId;
        /**收货单id*/
        private Long rooId;
        /**收货单明细id*/
        private Long roolId;
        /**入库任务id*/
        private Long roId;
        /** 引起库存变动业务单据编号 */
        private String ivDocumentCode;
        /** 引起库存变动单据id */
        private Long ivDocumentId;
        /**引起库存变动单据明细id  */
        private Long ivDocumentlineId;

    }
}
