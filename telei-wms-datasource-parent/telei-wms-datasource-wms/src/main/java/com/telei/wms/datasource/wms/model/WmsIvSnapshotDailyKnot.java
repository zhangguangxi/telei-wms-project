package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * wms_iv_snapshot_daily_knot 库存快照日结算表
 */
@Data
public class WmsIvSnapshotDailyKnot implements Entity<Long> {
    /** id */
    private Long id;
    /** 公司id */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 客户id */
    private Long customerId;
    /** 产品id */
    private Long productId;
    /** 结算日期,当地时间 */
    private Date knotLcDate;
    /** 结算时间 */
    private Date knotDate;
    /** 快照日期,当地时间 */
    private Date snapshotLcDate;
    /** 快照时间 */
    private Date snapshotDate;
    /** 库存数量 */
    private BigDecimal ivQty;
    /** 总重量(KG) */
    private BigDecimal totalWeight;
    /** 总净重(KG) */
    private BigDecimal totalNetWeight;
    /** 总体积 */
    private BigDecimal totalVol;
    /** 库位数 */
    private BigDecimal lcCount;
    /** 大包数量 */
    private BigDecimal bigBagQty;
    /** 大包剩余数量 */
    private BigDecimal bigBagExtraQty;
    /** 中包数量 */
    private BigDecimal midBagQty;
    /** 中包剩余数量 */
    private BigDecimal midBagExtraQty;
    /** 创建时间 */
    private Date createTime;
}