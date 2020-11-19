package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * wms_do_line 出库任务单信息
 */
@Data
public class WmsDoLine implements Entity<Long> {
    /** id */
    private Long id;
    /** 订单id */
    private Long dohId;
    /** 出库计划明细id */
    private Long spdId;
    /** 出库计划id */
    private Long spId;
    /** 销售订单id */
    private Long soId;
    /** 销售订单明细id */
    private Long sodId;
    /** 产品id */
    private Long productId;
    /** 条码 */
    private String productBarcode;
    /** 规格型号-有颜色尺码拼接 */
    private String specType;
    /** 图片 */
    private String mediaId;
    /** 产品编码-客户输入 */
    private String productNo;
    /** 产品名称 */
    private String productName;
    /** 产品本地名称 */
    private String productNameLocal;
    /** UPC码 */
    private String productUpcCode;
    /** 品牌 */
    private String brand;
    /** 计量单位 */
    private Long stockUnit;
    /** 单位毛重(KG) */
    private BigDecimal unitGrossWeight;
    /** 单位体积(CBM) */
    private BigDecimal unitVol;
    /** 数量 */
    private BigDecimal qty;
    /** 行重量(KG) */
    private BigDecimal weight;
    /** 行体积 */
    private BigDecimal vol;
    /** 大包数量 */
    private BigDecimal bigBagQty;
    /** 大包转换数 */
    private BigDecimal bigBagRate;
    /** 中包数量 */
    private BigDecimal midBagQty;
    /** 中包转换数 */
    private BigDecimal midBagRate;
    /** 小包数量 */
    private BigDecimal smallBagQty;
    /** 出库数量 */
    private BigDecimal shipQty;
    /** 出库重量(KG) */
    private BigDecimal shipWeight;
    /** 出库体积(CBM) */
    private BigDecimal shipVol;
    /** 拣货数量 */
    private BigDecimal ploQty;
    /** 装柜数量 */
    private BigDecimal containerQty;
    /** 退库数量 */
    private BigDecimal backlcQty;
    /** 备注 */
    private String memo;
    /** 参考成本 */
    private BigDecimal costReference;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 金额 */
    private BigDecimal amount;
}