package com.telei.wms.project.api.endpoint.init.dto;

import com.telei.wms.commons.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitLineDetailExcel {

    @Excel(name = "商品码*(SKU编码)")
    private String productNo;
    /** 产品名称 */
    @Excel(name = "商品名称")
    private String productName;
    /** UPC码 */
    @Excel(name = "商品条码*")
    private String productBarcode;
    /** 采购金额 */
    @Excel(name = "货位*")
    private String amount;
    /** 采购单价 */
    @Excel(name = "货位类型*", combo="样品库位,高库位")
    private String unitPrice;
    /** 数量 */
    @Excel(name = "数量*")
    private BigDecimal palnQty;
    /** 供应商 */
    @Excel(name = "供应商")
    private String supplierName;
}