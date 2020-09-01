package com.telei.wms.project.api.endpoint.init;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.init.dto.*;
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
 * @date 2020/8/25 15:03
 */
@Api(tags = "库存初始化接口")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class InitEndpoint {

    @Autowired
    private InitBussiness initBussiness;

    @ApiOperation("新增库存初始化")
    @PostMapping(ServiceId.WMS_INIT_ADD)
    public InitHeaderCudBaseResponse addInitHeader(@RequestBody @Valid InitHeaderAddRequest request) {
        return initBussiness.addInitHeader(request);
    }

    @ApiOperation("审核库存初始化")
    @PostMapping(ServiceId.WMS_INIT_AUDIT)
    public InitHeaderCudBaseResponse auditInitHeader(@RequestBody @Valid InitHeaderAuditRequest request) {
        return initBussiness.auditInitHeader(request);
    }

    @ApiOperation("库存初始化分页")
    @PostMapping(ServiceId.WMS_INIT_PAGE_QUERY)
    public InitHeaderPageQueryResponse initHeaderPageQuery(@RequestBody @Valid InitHeaderPageQueryRequest request) {
        InitHeaderBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(request, InitHeaderBusinessPageQueryRequest.class);
        InitHeaderBusinessPageQueryResponse businessResponse = initBussiness.initHeaderPageQuery(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, InitHeaderPageQueryResponse.class);
    }

    @ApiOperation("库存初始化详情")
    @PostMapping(ServiceId.WMS_INIT_DETAIL)
    public InitHeaderDetailResponse initHeaderDetail(@RequestBody @Valid InitHeaderDetailRequest request) {
        return initBussiness.initHeaderDetail(request);
    }

}
