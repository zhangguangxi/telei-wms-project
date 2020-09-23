package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsPloDetail;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 拣货详情
 */
@Data
public class PloDetailPageQueryResponseVo extends WmsPloDetail {

    /** 中包数量 */
    private BigDecimal midBagRate;
    /** 大包数量 */
    private BigDecimal bigBagRate;
}
