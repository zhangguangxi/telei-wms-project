package com.telei.wms.project.api.endpoint.backlc;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.backlc.dto.*;
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
 * @date: 2020/8/26 09:35
 */

@RestController
@Api(tags = "退库管理")
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class BacklcEndpoint {
    @Autowired
    private  BaclcBussiness backlcBussiness;

    @ApiOperation("新增退库记录")
    @PostMapping(ServiceId.WMS_BACKLC_ADD)
    public BacklcAddAddResponse addInventory(@Valid @RequestBody BacklcAddRequest request) {
        BacklcAddABussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, BacklcAddABussinessRequest.class);
        BacklcAddABussinessResponse bussinessResponse = backlcBussiness.addBacklc(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, BacklcAddAddResponse.class);
    }

    @ApiOperation("退库记录列表")
    @PostMapping(ServiceId.WMS_BACKLC_LIST)
    public BacklcListResponse increaseInventory(@Valid @RequestBody BacklcListRequest request) {
        BacklcListBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, BacklcListBussinessRequest.class);
        BacklcListBussinessResponse bussinessResponse = backlcBussiness.listBacklc(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, BacklcListResponse.class);
    }

    @ApiOperation("已出库-柜记录列表")
    @PostMapping(ServiceId.WMS_OUT_STOCK_CONTAINER_LIST)
    public BacklcOutStockContainerListResponse reduceInventory(@Valid @RequestBody BacklcOutStockContainerListRequest request) {
        BacklcOutStockContainerListBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, BacklcOutStockContainerListBussinessRequest.class);
        BacklcOutStockContainerListBussinessResponse bussinessResponse = backlcBussiness.outStockContainerListBacklc(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, BacklcOutStockContainerListResponse.class);
    }
}
