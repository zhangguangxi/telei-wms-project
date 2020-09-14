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
        /**计量单位*/
        private Long stockUnit;
        /** 库存数量 */
        private BigDecimal ivQty;
        /** 产品序列码 */
        private Long productId;
        /** 库存批次id */
        private Long iabId;
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

        /**引起库存变动的单据明细id*/
        private Long ivDocumentlineId;
        public Long getIvDocumentlineId(){
            return this.getPaolId();
        }
        /**引起库存变动的单据id*/
        private Long ivDocumentId;
        public Long getIvDocumentId(){
            return this.getPaoId();
        }
    }
}
