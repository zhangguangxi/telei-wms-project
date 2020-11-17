package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsLcRecommend;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 推荐库位分页查询
 */
@Data
public class LcRecommendPageQueryResponseVo extends WmsLcRecommend {

    /** 仓库名称 */
    private String warehouseName;
    /** 产品编码-客户输入 */
    private String productNo;
    /** 产品名称 */
    private String productName;
    /** 中包数量 */
    private Integer midBagQty;
    /** 大包数量 */
    private Integer bigBagQty;
    /** 大包毛重(KG) */
    private BigDecimal bigBagWeight;
    /** 大包体积(CBM) */
    private BigDecimal bigBagVol;
    /** 产品分类名称 */
    private String categoryName;
    /** 产品分类外文名称 */
    private String categoryEname;
    /** 图片路径 */
    private String imagePath;
    /** 产品本地名称 */
    private String productNameLocal;
    /** 条码 */
    private String productBarcode;
}
