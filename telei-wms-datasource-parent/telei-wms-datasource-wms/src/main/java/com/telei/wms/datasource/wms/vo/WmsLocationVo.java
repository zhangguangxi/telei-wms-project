package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsLocation;
import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/22 10:56
 */
@Data
public class WmsLocationVo extends WmsLocation {

    private String warehouseName;

    private Integer productCount;

    private Integer qty;

    private List<WmsLocationAisleVo> aisleVoList;

}
