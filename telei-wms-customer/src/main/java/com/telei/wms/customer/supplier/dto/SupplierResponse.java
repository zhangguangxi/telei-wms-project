package com.telei.wms.customer.supplier.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author gongrp
 * @date 2020/6/10 16:41
 */
@Data
public class SupplierResponse {
    /**
     * 客户id
     */
    private Long id;
    /**
     * 客户编码
     */
    private String customerCode;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 客户地址
     */
    private String customerAddress;
    /**
     * 内部客户
     */
    private String internalCustomer;
    /**
     * 对应公司
     */
    private Long mappingCompanyId;
    /**
     * 客户类别
     */
    private String customerGroup;
    /**
     * 所属地区
     */
    private String belongArea;
    /**
     * 账期天数
     */
    private Integer periodDays;
    /**
     * 税号
     */
    private String taxNo;
    /**
     * 开户银行
     */
    private String bank;
    /**
     * 银行账号
     */
    private String bankAccount;
    /**
     * 联系人名称
     */
    private String contactName;
    /**
     * 联系人职位
     */
    private String contactPosition;
    /**
     * 联系人电话
     */
    private String contactPhone;
    /**
     * 联系人手机
     */
    private String contactMobile;
    /**
     * 联系人邮箱
     */
    private String contactEmail;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String memo;
    /**
     * 所属人
     */
    private Long ownerUser;
    /**
     * 创建用户
     */
    private String createUser;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后更新用户
     */
    private String lastUpdateUser;
    /**
     * 最后更新时间
     */
    private Date lastUpdateTime;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;

}
