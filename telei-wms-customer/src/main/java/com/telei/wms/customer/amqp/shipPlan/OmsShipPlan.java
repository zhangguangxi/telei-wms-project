package com.telei.wms.customer.amqp.shipPlan;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * oms_ship_plan 出库计划
 */
@Data
public class OmsShipPlan {
    /** 出库计划id */
    private Long id;
    /** 出库计划编号 */
    private String spCode;
    /** 销售订单id */
    private Long soId;
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
    /** 单据类型（01-线下订单 02-内部订单） */
    private String orderType;
    /** 进出口类型 01-进口,02-出口, 03-本地销售 */
    private String ieType;
    /** 总净重 */
    private BigDecimal totalNetWeight;
    /** 销售日期 */
    private Date saleDate;
    /** 付款日期 */
    private Date paymentDate;
    /** 要求到货日期 */
    private Date arrivalDate;
    /** 结算方式 */
    private String settlementMethod;
    /** 结算期限 */
    private String settlementPeriod;
    /** 总重量(KG) */
    private BigDecimal totalWeight;
    /** 总体积(CBM) */
    private BigDecimal totalVol;
    /** 总计划出库数量 */
    private BigDecimal totalQty;
    /** 货品种类数量 */
    private Integer detailedSpeciesQty;
    /** 总金额 */
    private BigDecimal totalAmount;
    /** 实际出库数量 */
    private BigDecimal shipQty;
    /** 币种 */
    private String currency;
    /** 订单状态 01-制单，10-审核，40-部分出库，50-已出库，98-关闭，99-作废 */
    private String orderStatus;
    /** 商家备注 */
    private String memo;
    /** 所属人,存储account_id */
    private Long ownerUser;
    /** 创建用户 */
    private String createUser;
    /** 创建时间 */
    private Date createTime;
    /** 最后更新用户 */
    private String lastUpdateUser;
    /** 最后更新时间 */
    private Date lastUpdateTime;
}