package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: leo
 * @date: 2020/9/22 10:56
 */
@Data
public class WmsDeductIvOutConfirmResponseVo {
    /**公司id*/
    private Long companyId;
    /**仓库id*/
    private Long warehouseId;
    /**产品id*/
    private Long productId;
    /**库位*/
    private String lcCode;
    /**待出库存id*/
    private Long ivoId;
    /** 明细id */
    private Long lineId;
    /**数量*/
    private BigDecimal qty;
}
