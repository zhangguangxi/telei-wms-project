package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * wms_roo_header 收货单
 */
@Data
public class WmsRooHeader implements Entity<Long> {
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
    /** 收货状态 01-制单，10-部分收货，20-收货成功，30-待上架 40-已上架 98-关闭 */
    private String roStatus;
    /** 备注 */
    private String memo;
    /** 创建用户 */
    private String createUser;
    /** 创建时间 */
    private Date createTime;
    /** 收货用户 */
    private String recvUser;
    /** 收货时间 */
    private Date recvTime;
}