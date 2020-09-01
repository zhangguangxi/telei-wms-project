package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * wms_iv_attributebatch 库存批次表由收货生成
 */
@Data
public class WmsIvAttributebatch implements Entity<Long> {
    /** 库存批次id */
    private Long id;
    /** 产品id */
    private Long productId;
    /** 公司id */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 产品名称 */
    private String productName;
    /** 产品本地名称 */
    private String productNameLocal;
    /** 条码 */
    private String productBarcode;
    /** UPC码 */
    private String productUpcCode;
    /** 客户自定义批次号 */
    private String batchNo;
    /** 产品分类 */
    private Long productCategoryId;
    /** 规格型号-有颜色尺码拼接 */
    private String specType;
    /** 重量(毛重) */
    private BigDecimal weight;
    /** 长(CM) */
    private BigDecimal productLength;
    /** 宽(CM) */
    private BigDecimal productWidth;
    /** 高(CM) */
    private BigDecimal productHeight;
    /** 体积 */
    private BigDecimal vol;
    /** 计量单位 */
    private Long stockUnit;
    /** 产品颜色 */
    private String productColor;
    /** 产品尺寸 */
    private String productSize;
    /** 单价，取sku建议售价 */
    private BigDecimal price;
    /** 材质 */
    private String texture;
    /** 图片路径 */
    private String imagePath;
    /** 中包转换数 = 产品表中包转换数 */
    private Integer midBagRate;
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
    /** 大包转换数 = 产品表大包转换数 */
    private Integer bigBagRate;
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
    /** 生产日期 */
    private Date productDate;
    /** 有效日期 */
    private Date effectiveDate;
    /** 实物类别，GD合格 BK破损 MD霉变 WE淋湿 DF残次，数据字典COMMON_QS_CODE */
    private String qsCode;
    /** 应用代码  RECV 入库单收货, 其它,待定义   ,类型不同iab_document两字段对应的单据不同 */
    private String apCode;
    /** 生成属性业务单据编号 */
    private String documentCode;
    /** 生成属性单据id（入库任务等） */
    private Long iabDocumentId;
    /** 生成属性单据明细id */
    private Long iabDocumentlineId;
    /** 创建用户 */
    private Long createUser;
    /** 创建时间 */
    private Date createTime;
}