package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsLcRecommend;
import lombok.Data;

/**
 * 推荐库位分页查询
 */
@Data
public class LcRecommendPageQueryResponseVo extends WmsLcRecommend {

    /** 仓库名称 */
    private String warehouseName;
}
