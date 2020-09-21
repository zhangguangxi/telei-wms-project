package com.telei.wms.project.api.endpoint.doHeader;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.doHeader.dto.*;
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
@Api(tags = "出库任务")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class DoHeaderEndpoint {

    @Autowired
    private DoHeaderBussiness doBussiness;

    @ApiOperation("查询出库任务")
    @PostMapping(ServiceId.WMS_DO_HEADER_PAGE_QUERY)
    public DoHeaderPageQueryResponse pageQueryDoHeader(@RequestBody @Valid DoHeaderPageQueryRequest request) {
        return doBussiness.pageQueryDoHeader(request);
    }

    @ApiOperation("查询出库任务详细")
    @PostMapping(ServiceId.WMS_DO_DETAIL)
    public DoHeaderDetailResponse doDetail(@RequestBody @Valid DoDetailRequest request) {
        return doBussiness.doDetail(request);
    }

    @ApiOperation("核验")
    @PostMapping(ServiceId.WMS_DO_VERIFICATION)
    public DoCudBaseResponse doVerification(@RequestBody @Valid DoHeaderUpdateRequest request) {
        return doBussiness.doVerification(request);
    }

    @ApiOperation("出库")
    @PostMapping(ServiceId.WMS_DO_SHIP)
    public DoCudBaseResponse doShip(@RequestBody @Valid DoHeaderUpdateRequest request) {
        return doBussiness.doShip(request);
    }

}
