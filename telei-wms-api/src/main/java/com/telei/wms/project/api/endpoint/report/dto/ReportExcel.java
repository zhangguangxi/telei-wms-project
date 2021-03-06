package com.telei.wms.project.api.endpoint.report.dto;


import com.telei.wms.commons.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author gongrp
 * @date 2020/6/18 10:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportExcel {

    @Excel(name = "日期", dateFormat = "yyyy-MM-dd")
    private Date dateTime;

    @Excel(name = "供应商")
    private String suppCustName;

    @Excel(name = "合同号")
    private String contractNo;

    @Excel(name = "品名")
    private String productName;

    @Excel(name = "总数量", numberFormat = "#,##0")
    private Integer sumCount;

    @Excel(name = "件数", numberFormat = "#,##0")
    private Integer sumBigCount;

    @Excel(name = "金额", numberFormat = "¥#,##0.00")
    private BigDecimal sumAmount;

}