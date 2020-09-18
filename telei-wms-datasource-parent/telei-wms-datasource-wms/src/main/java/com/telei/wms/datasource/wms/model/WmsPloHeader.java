package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * wms_plo_header 拣货单头
 */
@Data
public class WmsPloHeader implements Entity<Long> {
    /** 单据id */
    private Long id;
    /** 业务单据编号 */
    private String ploCode;
    /** 订单id */
    private Long dohId;
    /** 出库任务 */
    private String dohCode;
    /** 公司id */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 商家订单号 */
    private String custOrderNo;
    /** 供应商id */
    private Long supplierId;
    /** 客户id */
    private Long customerId;
    /** 拣货单状态  01-制单，20-部分拣货，30-已拣货，98-关闭 */
    private String orderStatus;
    /** 冻结标记 1-是 0-否 */
    private Integer isFreezing;
    /** 数量 */
    private BigDecimal totalQty;
    /** 总重量(KG) */
    private BigDecimal totalWeight;
    /** 总体积(CBM) */
    private BigDecimal totalVol;
    /** 货品种类数量 */
    private Integer detailedSpeciesQty;
    /** 已拣货数量 */
    private BigDecimal pickedQty;
    /** 拣货重量(KG) */
    private BigDecimal pickingWeight;
    /** 拣货体积(CBM) */
    private BigDecimal pickingVol;
    /** 移动拣货      0否   1 是  */
    private String mobilePicking;
    /** 备注 */
    private String memo;
    /** 创建用户 */
    private String createUser;
    /** 创建时间 */
    private Date createTime;
    /** 最后更新用户 */
    private String lastUpdateUser;
    /** 最后更新时间 */
    private Date lastUpdateTime;
}