package com.telei.wms.project.api.endpoint.liftWork.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 增删改基础response
 */
@Data
public class LiftWorkInfoResponse extends LiftWorkCommonResponse {
    /** 公司编码 */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 升降类型 RISE-升库,DROP-降货 */
    private String liftType;
    /** 产品id */
    private Long productId;
    /** 产品编码 */
    private String productNo;
    /** 产品名称 */
    private String productName;
    /** 产品本地名称 */
    private String productNameLocal;
    /** 条码 */
    private String productBarcode;
    /** UPC码 */
    private String productUpcCode;
    /** 数量 */
    private BigDecimal liftQty;
    /** 大包数量 */
    private BigDecimal bigBagQty;
    /** 大包转换数 */
    private BigDecimal bigBagRate;
    /** 大包剩余数量 */
    private BigDecimal bigBagExtraQty;
    /** 样品库位(升,为原库位; 降，为目标库位) */
    private String sampleLcCode;
    /** 推荐库位 */
    private String prepLcCode;
    /** 推荐数量 */
    private BigDecimal qty;
}
