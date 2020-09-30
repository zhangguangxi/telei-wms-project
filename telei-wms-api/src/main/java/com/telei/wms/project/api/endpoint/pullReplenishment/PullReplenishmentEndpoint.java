package com.telei.wms.project.api.endpoint.pullReplenishment;


import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.commons.utils.CommonResponse;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.pullReplenishment.dto.PullReplenishmentBusinessPageQueryRequest;
import com.telei.wms.project.api.endpoint.pullReplenishment.dto.PullReplenishmentBusinessPageQueryResponse;
import com.telei.wms.project.api.endpoint.pullReplenishment.dto.PullReplenishmentPageQueryRequest;
import com.telei.wms.project.api.endpoint.pullReplenishment.dto.PullReplenishmentPageQueryResponse;
import com.telei.wms.project.api.utils.DataConvertUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author gongrp
 */
@Api(tags = "拉式补货接口")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class PullReplenishmentEndpoint {

    @Autowired
    private PullReplenishmentBussiness pullReplenishmentBussiness;

    @ApiOperation("拉式补货分页")
    @PostMapping(ServiceId.WMS_PULL_REPLENISHMENT_PAGE_QUERY)
    public PullReplenishmentPageQueryResponse PullReplenishmentPageQuery(@RequestBody @Valid PullReplenishmentPageQueryRequest request) {
        PullReplenishmentBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(request, PullReplenishmentBusinessPageQueryRequest.class);
        PullReplenishmentBusinessPageQueryResponse businessResponse = pullReplenishmentBussiness.pullReplenishmentPageQuery(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, PullReplenishmentPageQueryResponse.class);
    }

    @ApiOperation("拉式补货导出")
    @PostMapping(ServiceId.WMS_PULL_REPLENISHMENT_EXPORT)
    public CommonResponse exportByQuery(@RequestBody PullReplenishmentPageQueryRequest pageQueryRequest, HttpServletRequest request) {
        PullReplenishmentBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(pageQueryRequest, PullReplenishmentBusinessPageQueryRequest.class);
        return pullReplenishmentBussiness.pullReplenishmentList(businessRequest, request);
    }

}
