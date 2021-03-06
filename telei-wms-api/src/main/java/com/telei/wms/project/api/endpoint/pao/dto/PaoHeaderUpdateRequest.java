package com.telei.wms.project.api.endpoint.pao.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaoHeaderUpdateRequest {
    /** id */
    private Long id;
    /** 业务单据编号-按单据编码规则生成 */
    private String paoCode;
    /** 入库类型 WMS_I_ORDER_TYPE 20 直接入库 21 其他入库 等 */
    private String orderType;
    /** 入库单单头id */
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
    /** 总数量 */
    private BigDecimal totalQty;
    /** 总重量(KG) */
    private BigDecimal totalWeight;
    /** 总净重(KG) */
    private BigDecimal totalNetWeight;
    /** 总体积(CBM) */
    private BigDecimal totalVol;
    /** 已上架数量 */
    private BigDecimal putawayQty;
    /** 状态  01-制单，10-部分上架，20-已上架，98-关闭，99-作废 */
    private String paoStatus;
    /** 创建用户 */
    private String createUser;
    /** 创建时间 */
    private Date createTime;
    /** 最后更新用户 */
    private Date lastupdateUser;
    /** 最后更新时间 */
    private String lastupdateTime;
    /** 上架用户 */
    private String putawayUser;
    /** 上架时间 */
    private Date putawayTime;
}