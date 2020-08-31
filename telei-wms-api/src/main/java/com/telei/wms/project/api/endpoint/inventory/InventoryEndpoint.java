package com.telei.wms.project.api.endpoint.inventory;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.inventory.dto.*;
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
@Api(tags = "库存管理")
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class InventoryEndpoint {
    @Autowired
    private InventoryBussiness inventoryBussiness;

    @ApiOperation("上架")
    @PostMapping(ServiceId.WMS_INVENTORY_PUT_ON_SHELF)
    public InventoryAddResponse addInventory(@Valid @RequestBody InventoryAddRequest request){
        InventoryAddBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryAddBussinessRequest.class);
        InventoryAddBussinessResponse bussinessResponse = inventoryBussiness.addInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse,InventoryAddResponse.class);
    }

    @ApiOperation("调多")
    @PostMapping(ServiceId.WMS_INVENTORY_ADJUST_INCREASE)
    public InventoryIncreaseResponse increaseInventory(@Valid @RequestBody InventoryIncreaseRequest request){
        InventoryIncreaseBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryIncreaseBussinessRequest.class);
        InventoryIncreaseBussinessResponse bussinessResponse = inventoryBussiness.adjustInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryIncreaseResponse.class);
    }

    @ApiOperation("调少")
    @PostMapping(ServiceId.WMS_INVENTORY_ADJUST_REDUCE)
    public InventoryReduceResponse reduceInventory(@Valid @RequestBody InventoryReduceRequest request){
        InventoryReduceBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryReduceBussinessRequest.class);
        InventoryReduceBussinessResponse bussinessResponse = inventoryBussiness.reduceInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryReduceResponse.class);
    }

    @ApiOperation("移库")
    @PostMapping(ServiceId.WMS_INVENTORY_SHIFT)
    public InventoryShiftResponse shiftInventory(@Valid @RequestBody InventoryShiftRequest request){
        InventoryShiftBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryShiftBussinessRequest.class);
        InventoryShiftBussinessResponse bussinessResponse = inventoryBussiness.shiftInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryShiftResponse.class);
    }

//    @ApiOperation("移库导入")
//    @PostMapping(ServiceId.WMS_INVENTORY_IMPORT)
//    public InventoryImportResponse shiftInventory(@Valid @RequestBody InventoryImportRequest request){
//        InventoryImportBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryImportBussinessRequest.class);
//        InventoryImportBussinessResponse bussinessResponse = inventoryBussiness.ImportInventory(bussinessRequest);
//        return DataConvertUtil.parseDataAsObject(bussinessResponse,InventoryImportResponse.class);
//    }


}
