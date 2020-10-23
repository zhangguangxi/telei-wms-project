package com.telei.wms.project.api.endpoint.init;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.init.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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

    @ApiOperation("保存库存初始化")
    @PostMapping(ServiceId.WMS_INIT_ADD)
    public InitHeaderCudBaseResponse addInitHeader(@RequestBody @Valid InitHeaderAddRequest request) {
        return initBussiness.addInitHeader(request);
    }

    @ApiOperation("提交库存初始化")
    @PostMapping(ServiceId.WMS_INIT_AUDIT)
    public InitHeaderCudBaseResponse auditInitHeader(@RequestBody @Valid InitHeaderAuditRequest request) {
        return initBussiness.auditInitHeader(request);
    }

    @ApiOperation("下载库存初始化商品模板")
    @GetMapping(ServiceId.WMS_INIT_TEMPLATE)
    public void exportTemplate(HttpServletRequest request) {
        initBussiness.exportTemplate(request);
    }

    @ApiOperation("检查产品库存供应商是否存在")
    @PostMapping(ServiceId.WMS_INIT_CHECK)
    public List<InitLineCheckResponse> initLineCheck(@RequestBody @Valid InitLineCheckRequest request) {
        return initBussiness.initLineCheck(request);
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

    @ApiOperation("删除库存初始化单")
    @PostMapping(ServiceId.WMS_INIT_DELETE)
    public InitHeaderDeleteResponse deleteInitHeader(@RequestBody @Valid InitHeaderDeleteRequest request) {
        return initBussiness.deleteInitHeader(request);
    }

}
