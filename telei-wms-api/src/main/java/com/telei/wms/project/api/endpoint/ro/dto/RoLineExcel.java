package com.telei.wms.project.api.endpoint.ro.dto;

import com.telei.wms.commons.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoLineExcel {

    /** 产品编码-客户输入 */
    @Excel(name = "商品码")
    private String productNo;
    /** 产品名称 */
    @Excel(name = "商品名称")
    private String productName;
    /** 条码 */
    @Excel(name = "条码")
    private String productBarcode;
    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;
    /** 计划入库数量 */
    @Excel(name = "总数", numberFormat = "#,##0.00")
    private BigDecimal planQty;

}