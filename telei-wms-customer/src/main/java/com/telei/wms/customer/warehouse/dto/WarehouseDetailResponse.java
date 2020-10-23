package com.telei.wms.customer.warehouse.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author gongrp
 * @date 2020/6/10 16:41
 */
@Data
public class WarehouseDetailResponse {
    /** 仓库id */
    private Long id;
    /** 仓库code */
    private String warehouseCode;
    /** 仓库名称 */
    private String warehouseName;
    /** 公司id */
    private Long companyId;
    /** 仓库类型（01-主仓库，04-中转仓，06-中转仓） */
    private String categoryType;
    /** 默认收货仓库：0否，1是 */
    private String defaultReceive;
    /** 所在地区 */
    private String belongArea;
    /** 仓库地址 */
    private String addressTxt;
    /** 样品库位货量上限倍数 */
    private BigDecimal ceilingMultiple;
    /** 样品库位货量下限倍数 */
    private BigDecimal limitMultiple;
    /** 备注 */
    private String memo;
    /** 创建时间 */
    private Date createTime;
    /** 创建用户 */
    private String createUser;

}
