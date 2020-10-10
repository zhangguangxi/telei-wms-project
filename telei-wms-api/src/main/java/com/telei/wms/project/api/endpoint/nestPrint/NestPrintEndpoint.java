package com.telei.wms.project.api.endpoint.nestPrint;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.nestPrint.dto.NestRooPrintDetailBussinessRequest;
import com.telei.wms.project.api.endpoint.nestPrint.dto.NestRooPrintDetailBussinessResponse;
import com.telei.wms.project.api.endpoint.nestPrint.dto.NestRooPrintDetailRequest;
import com.telei.wms.project.api.endpoint.nestPrint.dto.NestRooPrintDetailResponse;
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
 * @author: leo
 * @date: 2020/10/10 14:07
 */
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
@Api(tags = "套打接口(嵌套打印)")
public class NestPrintEndpoint {
    @Autowired
    private NestPrintBussiness nestPrintBussiness;

    @ApiOperation("收货-套打详情")
    @PostMapping(ServiceId.WMS_ROO_NEST_PRINT_DETAIL)
    public NestRooPrintDetailResponse nestRooPrintDetail(@Valid @RequestBody NestRooPrintDetailRequest request){
        NestRooPrintDetailBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, NestRooPrintDetailBussinessRequest.class);
        NestRooPrintDetailBussinessResponse bussinessResponse = nestPrintBussiness.nestRooPrintDetail(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, NestRooPrintDetailResponse.class);
    }
}
