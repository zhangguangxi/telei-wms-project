package com.telei.wms.project.api.endpoint.liftTask.dto;

import lombok.Data;

import java.util.List;


/**
 * wms_lift_work 升降任务
 * @author gongrp
 */
@Data
public class LiftTaskUpdateRequest {

    private List<WmsProductSampleCommonRequest> updateRequestList;

    /**
     * 设置上下限类型
     * limit 下限
     * ceiling 上限
     */
    private String liftTaskType;

}