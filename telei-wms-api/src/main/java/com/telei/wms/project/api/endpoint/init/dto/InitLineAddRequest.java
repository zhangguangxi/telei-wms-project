package com.telei.wms.project.api.endpoint.init.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * wms_init_line 库存初始化明细
 */
@Data
public class InitLineAddRequest {
    /** id */
    private Long id;
    /** 单头id */
    private Long ivihId;
    /** 产品序列码 */
    private Long productId;
    /** 供应商id */
    private Long supplierId;
    /** 供应商id */
    private String productBarcode;
    /** 库位编码 */
    private String lcCode;
    /** 先进先出时间 */
    private Date ivFifoTime;
    /** 库存数量 */
    private BigDecimal ivQty;
    /** 生产日期 */
    private Date productDate;
    /** 有效日期 */
    private Date ivEffectiveDate;
    /** 客户指定批次号 */
    private String batchNo;
}