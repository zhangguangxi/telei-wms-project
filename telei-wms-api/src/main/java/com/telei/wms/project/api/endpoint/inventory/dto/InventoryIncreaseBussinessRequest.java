package com.telei.wms.project.api.endpoint.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: leo
 * @date: 2020/8/26 10:11
 */
@Data
public class InventoryIncreaseBussinessRequest {
    /** 公司编码 */
    private String companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /**原因*/
    private String reason;
    /**商品id*/
    private Long productId;
    /**库位编码*/
    private String lcCode;
    /** 库存数量 */
    private BigDecimal ivQty;
    /** 调整库位 */
    private String lcCodeAdjt;
    /** 调整数量 */
    private BigDecimal ivQtyAd;
}
