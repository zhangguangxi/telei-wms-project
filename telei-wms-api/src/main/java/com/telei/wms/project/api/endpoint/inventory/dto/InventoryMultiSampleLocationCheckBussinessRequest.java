package com.telei.wms.project.api.endpoint.inventory.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/11/13 16:48
 */
@Data
public class InventoryMultiSampleLocationCheckBussinessRequest {
    private List<InventoryMultiSampleLocationCheckCondition> list;
    @Data
    public static  class InventoryMultiSampleLocationCheckCondition{
        /**公司id*/
        private Long companyId;
        /**仓库id*/
        private Long warehouseId;
        /**库位编码*/
        private String lcCode;
        /**产品序列码*/
        private Long productId;
    }
}
