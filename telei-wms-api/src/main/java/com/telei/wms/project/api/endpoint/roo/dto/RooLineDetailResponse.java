package com.telei.wms.project.api.endpoint.roo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * wms_roo_line 收货操作单
 */
@Data
public class RooLineDetailResponse {
    /** id */
    private Long id;
    /** 收货作单头id */
    private Long rooId;
    /** 入库单单头id */
    private Long roId;
    /** 对应计划明细的id */
    private Long rolId;
    /** 收货状态 01-制单，20-收货成功，98-关闭 */
    private String roolStatus;
    /** 商品id */
    private Long productId;
    /** 条码 */
    private String productBarcode;
    /** UPC码 */
    private String productUpcCode;
    /** 计划数量 */
    private BigDecimal palnQty;
    /** 收货数量 */
    private BigDecimal receQty;
    /** 计量单位 */
    private Long stockUnit;
    /** 规格型号 */
    private String specType;
    /** 单位毛量(KG) */
    private BigDecimal weight;
    /** 长(CM) */
    private BigDecimal productLength;
    /** 宽(CM) */
    private BigDecimal productWidth;
    /** 高(CM) */
    private BigDecimal productHeight;
    /** 单位体积(CBM) */
    private BigDecimal vol;
    /** 中包数量 */
    private Integer midBagQty;
    /** 中包毛重(KG) */
    private BigDecimal midBagWeight;
    /** 中包长(CM) */
    private BigDecimal midBagLength;
    /** 中包宽(CM) */
    private BigDecimal midBagWidth;
    /** 中包高(CM) */
    private BigDecimal midBagHeight;
    /** 中包体积(CBM) */
    private BigDecimal midBagVol;
    /** 中包条码 */
    private String midBagBarcode;
    /** 大包数量 */
    private Integer bigBagQty;
    /** 大包毛重(KG) */
    private BigDecimal bigBagWeight;
    /** 大包长(CM) */
    private BigDecimal bigBagLength;
    /** 大包宽(CM) */
    private BigDecimal bigBagWidth;
    /** 大包高(CM) */
    private BigDecimal bigBagHeight;
    /** 大包体积(CBM) */
    private BigDecimal bigBagVol;
    /** 大包条码 */
    private String bigBagBarcode;
    /** 备注 */
    private String memo;
    /** 生产日期 */
    private Date productDate;
    /** 有效日期 */
    private Date effectiveDate;
    /** 客户自定义批次号 */
    private String batchNo;
    /** 实物类别，GD合格 BK破损 MD霉变 WE淋湿 DF残次，数据字典COMMON_QS_CODE */
    private String qsCode;
    /** 收货时间 */
    private Date recvTime;
    /** 收货用户 */
    private String recvUser;
    /** 创建用户 */
    private String createUser;
    /** 创建时间 */
    private Date createTime;
    /** 库存批次id */
    private Long iabId;
}