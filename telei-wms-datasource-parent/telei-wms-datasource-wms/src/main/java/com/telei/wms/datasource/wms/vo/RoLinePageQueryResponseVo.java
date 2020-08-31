package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsRoLine;
import lombok.Data;

/**
 * 入库任务详细
 */
@Data
public class RoLinePageQueryResponseVo extends WmsRoLine {

    /** 产地 */
    private String placeOfOrigin;
    /** 中包数量 */
    private String midBagQty;
    /** 大包数量 */
    private String bigBagQty;
}
