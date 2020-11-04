package com.telei.wms.project.api.endpoint.liftWork;


import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.liftWork.dto.*;
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
public class LiftWorkEndpoint {

    @Autowired
    private LiftWorkBussiness liftWorkBussiness;

    @ApiOperation("新增升降任务")
    @PostMapping(ServiceId.WMS_LIFT_WORK_ADD)
    public LiftWorkAddResponse addLiftWork(@RequestBody @Valid LiftWorkAddRequest request) {
        LiftWorkBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LiftWorkBusinessRequest.class);
        LiftWorkBusinessResponse businessResponse = liftWorkBussiness.addLiftWork(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LiftWorkAddResponse.class);
    }

    @ApiOperation("撤销升降任务")
    @PostMapping(ServiceId.WMS_LIFT_WORK_REVOKE)
    public LiftWorkRevokeResponse revokeLiftWork(@RequestBody @Valid LiftWorkRevokeRequest request) {
        LiftWorkBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LiftWorkBusinessRequest.class);
        LiftWorkBusinessResponse businessResponse = liftWorkBussiness.revokeLiftWork(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LiftWorkRevokeResponse.class);
    }

    @ApiOperation("处理升降任务")
    @PostMapping(ServiceId.WMS_LIFT_WORK_UPDATE)
    public LiftWorkUpdateResponse updateLiftWork(@RequestBody @Valid LiftWorkUpdateRequest request) {
        LiftWorkBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LiftWorkBusinessRequest.class);
        LiftWorkBusinessResponse businessResponse = liftWorkBussiness.updateLiftWork(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LiftWorkUpdateResponse.class);
    }

    @ApiOperation("升降任务分页")
    @PostMapping(ServiceId.WMS_LIFT_WORK_PAGE_QUERY)
    public LiftWorkPageQueryResponse liftWorkPageQuery(@RequestBody @Valid LiftWorkPageQueryRequest request) {
        LiftWorkBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LiftWorkBusinessPageQueryRequest.class);
        LiftWorkBusinessPageQueryResponse businessResponse = liftWorkBussiness.liftWorkPageQuery(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LiftWorkPageQueryResponse.class);
    }

    @ApiOperation("查询产品对应库位信息")
    @PostMapping(ServiceId.WMS_LIFT_WORK_INFO)
    public LiftWorkInfoResponse getLiftWorkInfo(@RequestBody @Valid LiftWorkInfoRequest request) {
        LiftWorkBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LiftWorkBusinessRequest.class);
        LiftWorkBusinessResponse businessResponse = liftWorkBussiness.getLiftWorkInfo(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LiftWorkInfoResponse.class);
    }

}
