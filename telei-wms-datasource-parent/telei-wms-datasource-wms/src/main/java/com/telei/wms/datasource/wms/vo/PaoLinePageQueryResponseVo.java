package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsPaoLine;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 上架单详细
 */
@Data
public class PaoLinePageQueryResponseVo extends WmsPaoLine {

    /** 产品编码-客户输入 */
    private String productNo;
    /** 产品名称 */
    private String productName;
    /** 条码 */
    private String productBarcode;
    /** 品牌 */
    private String brand;
    /** 产地 */
    private String placeOfOrigin;
    /** 规格型号 */
    private String specType;
    /** 中包数量 */
    private BigDecimal midBagQty;
    /** 大包数量 */
    private BigDecimal bigBagQty;
}
