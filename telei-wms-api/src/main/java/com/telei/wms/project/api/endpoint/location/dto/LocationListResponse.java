package com.telei.wms.project.api.endpoint.location.dto;

import com.telei.wms.datasource.wms.model.WmsLocation;
import com.telei.wms.datasource.wms.vo.WmsLocationVo;
import lombok.Data;

import java.util.List;

/**
 * @author gongrp
 */
@Data
public class LocationListResponse {
    /**
     * 通道列表
     */
    private List<WmsLocation> lcAisleList;
    /**
     * 库位列表
     */
    private List<WmsLocationVo> lcCodeList;
}