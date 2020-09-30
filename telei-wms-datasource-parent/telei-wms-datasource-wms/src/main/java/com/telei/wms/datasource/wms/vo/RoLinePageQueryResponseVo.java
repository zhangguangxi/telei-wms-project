package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsRoLine;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 入库任务详细
 */
@Data
public class RoLinePageQueryResponseVo extends WmsRoLine {

    /** 产地 */
    private String placeOfOrigin;
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
}
