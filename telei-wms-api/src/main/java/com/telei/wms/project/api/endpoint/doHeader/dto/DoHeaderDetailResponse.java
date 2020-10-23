package com.telei.wms.project.api.endpoint.doHeader.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * wms_ro_header 出库任务
 */
@Data
public class DoHeaderDetailResponse {
    /** 订单id */
    private Long id;
    /** 业务单据编号，从OMS拉取 */
    private String dohCode;
    /** 出库计划id */
    private Long spId;
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
    /** 客户联系人名称 */
    private String customerContactName;
    /** 客户联系人手机 */
    private String customerContactMobile;
    /** 客户联系人地址 */
    private String customerContactAddress;
    /** 备注 */
    private String memo;
    /** 总数量 */
    private BigDecimal totalQty;
    /** 总重量(KG) */
    private BigDecimal totalWeight;
    /** 总体积(CBM) */
    private BigDecimal totalVol;
    /** 货品种类数量 */
    private Integer detailedSpeciesQty;
    /** 出库数量 */
    private BigDecimal shipQty;
    /** 出库重量(KG) */
    private BigDecimal shipWeight;
    /** 出库体积(CBM) */
    private BigDecimal shipVol;
    /** 出库货品种类数量 */
    private Integer shipDetailedSpeciesQty;
    /** 单据类型（01-线下订单 02-内部订单） */
    private String orderType;
    /** 进出口类型 01-进口,02-出口, 03-本地销售 */
    private String ieType;
    /** 订单状态：10-未作业，20-已配货， 25-部分拣货， 30-已拣货， 40-已出库，  98-已取消 */
    private String orderStatus;
    /** 核验时间 */
    private Date checkTime;
    /** 核验人 */
    private String checkUser;
    /** 0 没有拣货单,1 已经生成拣货单 */
    private String hasPlo;
    /** 0 没有打印过核验单,1 打印过核验单 */
    private String hadPrintCheck;
    /** 0 未核验,1 已核验 */
    private String hadCheck;
    /** 0 没有打印装箱单,1 打印过装箱单 */
    private String hadPrintLoadList;
    /** 0 有效数据,1 历史版本数据 */
    private String oldDate;
    /** 创建时间 */
    private Date createTime;
    /** 最后更新时间 */
    private Date lastupdateTime;
    /** 发货时间 */
    private Date shippingTime;

    private List<DoLineDetailResponse> doLines;

    private Long ploId;
}