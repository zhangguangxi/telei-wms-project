package com.telei.wms.project.api.endpoint.roo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * wms_roo_header 收货单
 */
@Data
public class RooHeaderBusinessRequest {
    /** id */
    private Long id;
    /** 业务单据编号-按单据编码规则生成 */
    private String rooCode;
    /** 入库类型 WMS_I_ORDER_TYPE 20 直接入库 21 其他入库 等 */
    private String orderType;
    /** 入库任务id */
    private Long roId;
    /** 入库任务单号 */
    private String roCode;
    /** 商家订单号 */
    private String custOrderNo;
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
    /** 总数量 */
    private BigDecimal totalQty;
    /** 生成上架单数量 */
    private BigDecimal tmpPutawayQty;
    /** 上架数量 */
    private BigDecimal putawayQty;
    /** 收货状态 01-制单，10-部分收货，20-收货成功，98-关闭 */
    private String roStatus;
    /** 备注 */
    private String memo;

    private Long rooId;

    private Collection<Long> roolIds;

    private List<RooLineAddRequest> rooLines;
}