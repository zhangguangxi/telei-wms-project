package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class WmsInitLineVO {
    /** 库存初始化明细数据 */
    /** id */
    private Long id;
    /** 单头id */
    private Long ivihId;
    /** 产品序列码 */
    private Long productId;
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
    /** 产品数据 */
    /** 产品编码-客户输入 */
    private String productNo;
    /** 产品名称 */
    private String productName;
    /** 产品本地名称 */
    private String productNameLocal;
    /** 条码 */
    private String productBarcode;
    /** UPC码 */
    private String productUpcCode;
    /** 产品分类 */
    private Long productCategoryId;
    /** 规格型号 */
    private String specType;
    /** 品牌 */
    private String brand;
    /** 产地 */
    private String placeOfOrigin;
    /** 毛重(G) */
    private BigDecimal unitGrossWeight;
    /** 长(CM) */
    private BigDecimal productLength;
    /** 宽(CM) */
    private BigDecimal productWidth;
    /** 高(CM) */
    private BigDecimal productHeight;
    /** 单位体积(CBM) */
    private BigDecimal unitVol;
    /** 计量单位 */
    private Long stockUnit;
    /** 产品颜色 */
    private String productColor;
    /** 产品尺寸 */
    private String productSize;
    /** 参考成本 */
    private BigDecimal costReference;
    /** 成本币种 */
    private Integer currencyId;
    /** 建议售价 */
    private BigDecimal sellingPriceReference;
    /** 售价币种 */
    private Integer sellingCurrencyId;
    /** 材质 */
    private String texture;
    /** 备注 */
    private String memo;
    /** 状态 */
    private Integer status;
    /** 上架状态，1上架，0暂不上架 */
    private Integer saleStatus;
    /** 图片路径 */
    private String imagePath;
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

}
