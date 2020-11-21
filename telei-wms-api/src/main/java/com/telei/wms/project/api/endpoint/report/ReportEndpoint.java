package com.telei.wms.project.api.endpoint.report;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.report.dto.ReportBusinessPageQueryRequest;
import com.telei.wms.project.api.endpoint.report.dto.ReportBusinessPageQueryResponse;
import com.telei.wms.project.api.endpoint.report.dto.ReportPageQueryRequest;
import com.telei.wms.project.api.endpoint.report.dto.ReportPageQueryResponse;
import com.telei.wms.project.api.endpoint.roo.RooBussiness;
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
 * @date 2020/6/10 16:41
 */
@Api(tags = "报表统计接口")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class ReportEndpoint {

    @Autowired
    private RooBussiness rooBussiness;

    @ApiOperation("查询收货统计报表")
    @PostMapping(ServiceId.WMS_REPORT_ROO_QUERY)
    public ReportPageQueryResponse rooReportQuery(@RequestBody @Valid ReportPageQueryRequest request) {
        ReportBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(request, ReportBusinessPageQueryRequest.class);
        ReportBusinessPageQueryResponse businessResponse = rooBussiness.rooReportQuery(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, ReportPageQueryResponse.class);
    }

}
