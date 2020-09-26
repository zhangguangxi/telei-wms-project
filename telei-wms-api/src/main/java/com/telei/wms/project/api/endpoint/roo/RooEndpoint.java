package com.telei.wms.project.api.endpoint.roo;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.roo.dto.*;
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
@Api(tags = "收货单接口")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class RooEndpoint {

    @Autowired
    private RooBussiness rooBussiness;

    @ApiOperation("新增收货单")
    @PostMapping(ServiceId.WMS_ROO_ADD)
    public RooHeaderAddResponse addRooHeader(@RequestBody @Valid RooHeaderAddRequest request) {
        RooHeaderBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, RooHeaderBusinessRequest.class);
        RooHeaderBusinessResponse businessResponse = rooBussiness.addRooHeader(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, RooHeaderAddResponse.class);
    }

    @ApiOperation("撤销收货单")
    @PostMapping(ServiceId.WMS_ROO_REVOKE)
    public RooHeaderRevokeResponse revokeRooHeader(@RequestBody @Valid RooHeaderRevokeRequest request) {
        RooHeaderBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, RooHeaderBusinessRequest.class);
        RooHeaderBusinessResponse businessResponse = rooBussiness.revokeRooHeader(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, RooHeaderRevokeResponse.class);
    }

    @ApiOperation("收货单分页")
    @PostMapping(ServiceId.WMS_ROO_PAGE_QUERY)
    public RooHeaderPageQueryResponse rooHeaderPageQuery(@RequestBody @Valid RooHeaderPageQueryRequest request) {
        RooHeaderBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(request, RooHeaderBusinessPageQueryRequest.class);
        RooHeaderBusinessPageQueryResponse businessResponse = rooBussiness.rooHeaderPageQuery(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, RooHeaderPageQueryResponse.class);
    }

    @ApiOperation("收货单详情")
    @PostMapping(ServiceId.WMS_ROO_DETAIL)
    public RooHeaderDetailResponse rooHeaderDetail(@RequestBody @Valid RooHeaderDetailRequest request) {
        RooHeaderBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, RooHeaderBusinessRequest.class);
        RooHeaderBusinessResponse businessResponse = rooBussiness.rooHeaderDetail(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, RooHeaderDetailResponse.class);
    }

}
