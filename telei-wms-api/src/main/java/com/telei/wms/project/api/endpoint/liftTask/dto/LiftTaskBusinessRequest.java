package com.telei.wms.project.api.endpoint.liftTask.dto;

import com.telei.wms.datasource.wms.vo.LiftTaskPageQueryResponseVo;
import lombok.Data;

import java.util.List;

/**
 * @author gongrp
 * @date 2020/6/10 16:52
 */
@Data
public class LiftTaskBusinessRequest {

    /**
     * 产品编码
     */
    private String productNo;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 条码
     */
    private String productBarcode;

    /**
     * 样品库位(升,为原库位; 降，为目标库位)
     */
    private String lcCode;

    private List<WmsProductSampleCommonRequest> updateRequestList;

    public List<LiftTaskPageQueryResponseVo> addLiftWorkList;

    private String liftTaskType;

}
