package com.telei.wms.project.api.endpoint.inventory.dto;

import lombok.Data;

/**
 * @author: leo
 * @date: 2020/9/10 17:12
 */
@Data
public class InventoryDetailBussinessRequest {
    /**公司id*/
    private Long companyId;
    /**仓库id*/
    private Long warehouseId;
    /**库位编码*/
    private String lcCode;
    /**产品id*/
    private Long productId;
}
