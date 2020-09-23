package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * wms_iv_out_history wms待出库存表
 */
@Data
public class WmsIvOutHistory implements Entity<Long> {
    /** 待出库存id */
    private Long id;
    /** 公司id */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 计划出库时间 */
    private Date planOutDate;
    /** 订单id */
    private Long orderId;
    /** 订单编号 */
    private String orderCode;
    /** 明细id */
    private Long lineId;
    /** 产品id */
    private Long productId;
    /** 数量 */
    private BigDecimal qty;
}