package com.telei.wms.project.api.endpoint.liftWork.dto;

import lombok.Data;

import java.util.List;


/**
 * wms_lift_work 升降任务
 * @author gongrp
 */
@Data
public class LiftWorkUpdateRequest {

    private List<LiftWorkUpdateCommonRequest> updateRequestList;

}