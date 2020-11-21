package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ReportVo {

    /**
     * 日期
     */
    private Date dateTime;
    /**
     * 供应商
     */
    private String suppCustName;
    /**
     * 合同号
     */
    private String contractNo;
    /**
     * 总件数
     */
    private Integer sumCount;
    /**
     * 总金额
     */
    private BigDecimal sumAmount;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 币种名称
     */
    private String currencyName;
    /**
     * 币种名称
     */
    private String currencyEname;

}
