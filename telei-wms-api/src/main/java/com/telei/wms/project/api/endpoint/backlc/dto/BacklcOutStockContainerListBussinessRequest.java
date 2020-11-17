package com.telei.wms.project.api.endpoint.backlc.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: leo
 * @date: 2020/8/26 10:15
 */
@Data
public class BacklcOutStockContainerListBussinessRequest {
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
    private BigDecimal ivQtyAdjt;
    /**调减、盘亏、销毁、报损*/
    private String adjhDetailType;
}
