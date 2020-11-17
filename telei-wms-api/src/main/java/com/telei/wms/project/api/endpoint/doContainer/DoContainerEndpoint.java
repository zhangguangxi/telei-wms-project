package com.telei.wms.project.api.endpoint.doContainer;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.doContainer.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Auther: gongrp
 * @Date: 2020/9/16 18:10
 */
@Api(tags = "装柜管理")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class DoContainerEndpoint {

    @Autowired
    private DoContainerBussiness doContainerBussiness;

    @ApiOperation("OMS货柜管理-装柜信息")
    @PostMapping(ServiceId.WMS_DO_CONTAINER_DETAIL_BY_OMS)
    public DoContainerDetailResponse queryOMSContainerDetail(@RequestBody @Valid DoContainerDetailRequest request) {
        return doContainerBussiness.queryOMSContainerDetail(request);
    }

    @ApiOperation("查询货柜列表")
    @PostMapping(ServiceId.WMS_DO_CONTAINER_PAGE_QUERY)
    public DoContainerPageQueryResponse pageQueryDoContainer(@RequestBody @Valid DoContainerPageQueryRequest request) {
        return doContainerBussiness.pageQueryDoContainer(request);
    }

    @ApiOperation("确认装柜")
    @PostMapping(ServiceId.WMS_DO_CONTAINER_ADD)
    public DoContainerCudBaseResponse addDoContainer(@RequestBody @Valid DoContainerAddRequest request) {
        return doContainerBussiness.addDoContainer(request);
    }

    @ApiOperation("WMS装柜信息")
    @PostMapping(ServiceId.WMS_DO_CONTAINER_DETAIL)
    public DoContainerDetailResponse queryWMSContainerDetail(@RequestBody @Valid DoContainerPageQueryRequest request) {
        return doContainerBussiness.queryWMSContainerDetail(request);
    }

    @ApiOperation("装柜明细")
    @PostMapping(ServiceId.WMS_DO_CONTAINER_DETAIL_LIST)
    public DoContainerPageQueryResponse queryContainerDetailList(@RequestBody @Valid DoContainerDetailPageQueryRequest request) {
        return doContainerBussiness.queryContainerDetailList(request);
    }

    @ApiOperation("撤销装柜")
    @PostMapping(ServiceId.WMS_DO_CONTAINER_REVOKE)
    public DoContainerCudBaseResponse revokeDoContainer(@RequestBody @Valid DoContainerRevokeRequest request) {
        return doContainerBussiness.revokeDoContainer(request);
    }

    // 退库
    // 退库单明细

}
