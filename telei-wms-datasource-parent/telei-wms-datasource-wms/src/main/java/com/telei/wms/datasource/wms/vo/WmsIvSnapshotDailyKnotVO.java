package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存快照日结统计数据
 */
@Data
public class WmsIvSnapshotDailyKnotVO {
    /** 公司id */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 商家编码 */
    private Long customerId;
    /** 产品id */
    private Long productId;
    /** 库存数量 */
    private BigDecimal ivQty;
    /** 大包数量 */
    private BigDecimal midBagQty;
    /** 大包剩余数量 */
    private BigDecimal midBagExtraQty;
    /** 中包数量 */
    private BigDecimal bigBagQty;
    /** 中包剩余数量 */
    private BigDecimal bigBagExtraQty;
    /** 总体积 */
    private BigDecimal totalVol;
    /** 总重量(KG) */
    private BigDecimal totalWeight;
    /** 总净重(KG) */
    private BigDecimal totalNetWeight;
    /** 库位数 */
    private BigDecimal lcCount;
    /** 唯一Key */
    private String uniqueKey;

}
