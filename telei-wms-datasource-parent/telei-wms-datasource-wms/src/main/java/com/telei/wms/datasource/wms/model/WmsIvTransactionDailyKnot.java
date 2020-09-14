package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * wms_iv_transaction_daily_knot 库存变动记录日结表
 */
@Data
public class WmsIvTransactionDailyKnot implements Entity<Long> {
    /** id */
    private Long id;
    /** 公司id */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 商家编码 */
    private Long customerId;
    /** 产品序列码 */
    private Long productId;
    /** 结算日期,当地时间 */
    private Date knotDate;
    /** 结算时间 */
    private Date knotLcDate;
    /** 快照日期,当地时间 */
    private Date snapshotDate;
    /** 快照时间 */
    private Date snapshotLcDate;
    /** 开始id(含) */
    private Long ivtIdFrom;
    /** 结束id(含) */
    private Long ivtIdEnd;
    /** 期初数量(取库存快照前一天结算数量) */
    private BigDecimal knotBeginQty;
    /** 出库数量(合计所有为负数的变化数量) */
    private BigDecimal ivtQtyOut;
    /** 出库大包数量 */
    private BigDecimal bigBagQtyOut;
    /** 出库毛重 */
    private BigDecimal weightOut;
    /** 出库净重 */
    private BigDecimal netWeightOut;
    /** 出库体积 */
    private BigDecimal volOut;
    /** 入库数量(合计所有为正数的变化数量) */
    private BigDecimal ivtQtyIn;
    /** 入库大包数量 */
    private BigDecimal bigBagQtyIn;
    /** 入库毛重 */
    private BigDecimal weightIn;
    /** 入库净重 */
    private BigDecimal netWeightIn;
    /** 入库体积 */
    private BigDecimal volIn;
    /** 发生数量(合计所有变化数量) */
    private BigDecimal ivtQty;
    /** 变动盘数【待定】 */
    private BigDecimal trayCountChange;
    /** 期末数量(取库存快照当天结算数量) */
    private BigDecimal knotEndQty;
    /** 创建时间，本地时间 */
    private Date createTimeLc;
    /** 创建时间 */
    private Date createTime;
}