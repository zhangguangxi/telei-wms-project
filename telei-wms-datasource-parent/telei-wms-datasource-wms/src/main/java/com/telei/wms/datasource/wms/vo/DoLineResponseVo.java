package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsDoLine;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 出库任务详情
 */
@Data
public class DoLineResponseVo extends WmsDoLine {

    /** 产地 */
    private String placeOfOrigin;
    /** 大包毛重(KG) */
    private BigDecimal bigBagWeight;
    /** 大包体积(CBM) */
    private BigDecimal bigBagVol;

}
