package com.telei.wms.project.api.endpoint.nestPrint;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.nestPrint.dto.*;
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

    @ApiOperation("上架-套打详情")
    @PostMapping(ServiceId.WMS_PAO_NEST_PRINT_DETAIL)
    public NestPaoPrintDetailResponse nestRooPrintDetail(@Valid @RequestBody NestPaoPrintDetailRequest request){
        NestPaoPrintDetailBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, NestPaoPrintDetailBussinessRequest.class);
        NestPaoPrintDetailBussinessResponse bussinessResponse = nestPrintBussiness.nestPaoPrintDetail(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, NestPaoPrintDetailResponse.class);
    }

    @ApiOperation("升降任务-套打详情")
    @PostMapping(ServiceId.WMS_LIFT_WORK_NEST_PRINT_DETAIL)
    public NestLiftWorkPrintDetailResponse nestRooPrintDetail(@Valid @RequestBody NestLiftWorkPrintDetailRequest request){
        NestLiftWorkPrintDetailBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, NestLiftWorkPrintDetailBussinessRequest.class);
        NestLiftWorkPrintDetailBussinessResponse bussinessResponse = nestPrintBussiness.nestLiftWorkPrintDetail(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, NestLiftWorkPrintDetailResponse.class);
    }


    @ApiOperation("拣货单-套打详情")
    @PostMapping(ServiceId.WMS_PLO_NEST_PRINT_DETAIL)
    public NestPloPrintDetailResponse nestRooPrintDetail(@Valid @RequestBody NestPloPrintDetailRequest request){
        NestPloPrintDetailBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, NestPloPrintDetailBussinessRequest.class);
        NestPloPrintDetailBussinessResponse bussinessResponse = nestPrintBussiness.nestPloPrintDetail(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, NestPloPrintDetailResponse.class);
    }

    @ApiOperation("核验单-套打详情")
    @PostMapping(ServiceId.WMS_CHECK_NEST_PRINT_DETAIL)
    public NestCheckPrintDetailResponse nestRooPrintDetail(@Valid @RequestBody NestCheckPrintDetailRequest request){
        NestCheckPrintDetailBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, NestCheckPrintDetailBussinessRequest.class);
        NestCheckPrintDetailBussinessResponse bussinessResponse = nestPrintBussiness.nestCheckPrintDetail(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, NestCheckPrintDetailResponse.class);
    }
}
