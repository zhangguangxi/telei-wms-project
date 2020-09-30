package com.telei.wms.project.api.endpoint.liftTask;


import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.liftTask.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author gongrp
 */
@Api(tags = "升降任务接口")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class LiftWTaskEndpoint {

    @Autowired
    private LiftTaskBussiness liftTaskBussiness;

    @ApiOperation("发布升降任务")
    @PostMapping(ServiceId.WMS_LIFT_TASK_ADD)
    public LiftTaskAddResponse addLiftTask(@RequestBody @Valid LiftTaskAddRequest request) {
        LiftTaskBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LiftTaskBusinessRequest.class);
        LiftTaskBusinessResponse businessResponse = liftTaskBussiness.addLiftTask(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LiftTaskAddResponse.class);
    }

    @ApiOperation("批量设置上限/下限")
    @PostMapping(ServiceId.WMS_LIFT_TASK_UPDATE)
    public LiftTaskUpdateResponse updateLiftTask(@RequestBody @Valid LiftTaskUpdateRequest request) {
        LiftTaskBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LiftTaskBusinessRequest.class);
        LiftTaskBusinessResponse businessResponse = liftTaskBussiness.updateLiftTask(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LiftTaskUpdateResponse.class);
    }

    @ApiOperation("智能升降任务分页")
    @PostMapping(ServiceId.WMS_LIFT_TASK_PAGE_QUERY)
    public LiftTaskPageQueryResponse liftTaskPageQuery(@RequestBody @Valid LiftTaskPageQueryRequest request) {
        LiftTaskBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LiftTaskBusinessPageQueryRequest.class);
        LiftTaskBusinessPageQueryResponse businessResponse = liftTaskBussiness.liftTaskPageQuery(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LiftTaskPageQueryResponse.class);
    }

}
