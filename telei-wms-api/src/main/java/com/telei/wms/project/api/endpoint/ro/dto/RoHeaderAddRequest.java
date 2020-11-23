package com.telei.wms.project.api.endpoint.ro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * wms_ro_header 入库任务
 */
@Data
public class RoHeaderAddRequest {
    /** 入库任务id */
    private Long id;
    /** 业务单据编号-用oms入库计划单号 */
    private String roCode;
    /** 采购单id */
    private Long poId;
    /** 入库计划id */
    private Long rpId;
    /** 公司id */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 供应商id */
    private Long supplierId;
    /** 供应商/客户名称 */
    private String suppCustName;
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
    /** 币种 */
    private Long currencyId;
    /** 货品种类数量 */
    private Integer speciesQty;
    /** 收货数量 */
    private BigDecimal receQty;
    /** 收货货品种类数量 */
    private Integer receSpeciesQty;
    /** 上架数量 */
    private BigDecimal putawayQty;
    /** 强制入库标记 1-是 0-否 */
    private Integer enforcement;
    /** 收货完成时间 */
    private Date recvAllTime;
    /** 上架完成时间 */
    private Date putawayAllTime;
    /** 订单状态 01-制单，10-审核，40-部分入库，50-已入库，98-关闭，99-作废 */
    private String orderStatus;
    /** 0 没有打印过入库任务,1 打印过入库任务 */
    private String hadPrintTo;
    /** 所属人,存储account_id */
    private Long ownerUser;
    /** 备注 */
    private String memo;
    /** 物流公司 */
    private String logisticsCompany;
    /** 物流单号 */
    private String trackingNo;
    /** 供应商联系人id */
    private Long supplierContactId;
    /** 供应商联系人名称 */
    private String supplierContactName;
    /** 供应商联系人手机 */
    private String supplierContactMobile;
    /** 供应商联系人地址 */
    private String supplierContactAddress;
    /** 创建用户 */
    private String createUser;
    /** 创建时间 */
    private Date createTime;
    /** 最后更新用户 */
    private String lastUpdateUser;
    /** 最后更新时间 */
    private Date lastUpdateTime;

    private List<RoLineAddRequest> roLines;
}