package com.telei.wms.customer.amqp.recovicePlan;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * oms_recovice_plan 入库计划
 */
@Data
public class OmsRecovicePlan {
    /** 入库计划id */
    private Long id;
    /** 业务单据编号-按单据编码规则生成 */
    private String rpCode;
    /** 采购单id */
    private Long poId;
    /** 公司id */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 供应商id */
    private Long supplierId;
    /** 客户id */
    private Long customerId;
    /** 商家订单号 */
    private String custOrderNo;
    /** 入库类型 01-补货入库,10-退货入库,20-送货退回,30-换货入库,40-直接入库,50-转仓入库,60-其他入库 */
    private String orderType;
    /** 预计入库时间 */
    private Date estArriveTime;
    /** 交货时间 */
    private Date deliveryTime;
    /** 总重量(KG) */
    private BigDecimal totalWeight;
    /** 总体积(CBM) */
    private BigDecimal totalVol;
    /** 总计划入库数量 */
    private BigDecimal totalQty;
    /** 总金额 */
    private BigDecimal totalAmount;
    /** 货品种类数量 */
    private Integer detailedSpeciesQty;
    /** 订单状态 01-制单，10-审核，40-部分入库，50-已入库，98-关闭，99-删除 */
    private String orderStatus;
    /** 实收数量 */
    private BigDecimal receiptsQty;
    /** 所属人,存储account_id */
    private Long ownerUser;
    /** 备注 */
    private String memo;
    /** 物流单号 */
    private String trackingNo;
    /** 物流公司 */
    private String logisticsCompany;
    /** 创建用户 */
    private String createUser;
    /** 创建时间 */
    private Date createTime;
    /** 最后更新用户 */
    private String lastUpdateUser;
    /** 最后更新时间 */
    private Date lastUpdateTime;
    /** 审核用户 */
    private String auditUser;
    /** 审核时间 */
    private Date auditTime;
}