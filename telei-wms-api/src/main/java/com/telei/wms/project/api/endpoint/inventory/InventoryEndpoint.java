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
    public InventoryAddResponse addInventory(@Valid @RequestBody InventoryAddRequest request) {
        InventoryAddBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryAddBussinessRequest.class);
        InventoryAddBussinessResponse bussinessResponse = inventoryBussiness.addInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryAddResponse.class);
    }

    @ApiOperation("调多")
    @PostMapping(ServiceId.WMS_INVENTORY_ADJUST_INCREASE)
    public InventoryIncreaseResponse increaseInventory(@Valid @RequestBody InventoryIncreaseRequest request) {
        InventoryIncreaseBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryIncreaseBussinessRequest.class);
        InventoryIncreaseBussinessResponse bussinessResponse = inventoryBussiness.increaseInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryIncreaseResponse.class);
    }

    @ApiOperation("调少")
    @PostMapping(ServiceId.WMS_INVENTORY_ADJUST_REDUCE)
    public InventoryReduceResponse reduceInventory(@Valid @RequestBody InventoryReduceRequest request) {
        InventoryReduceBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryReduceBussinessRequest.class);
        InventoryReduceBussinessResponse bussinessResponse = inventoryBussiness.reduceInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryReduceResponse.class);
    }

    @ApiOperation("移库")
    @PostMapping(ServiceId.WMS_INVENTORY_ADJUST_SHIFT)
    public InventoryShiftResponse shiftInventory(@Valid @RequestBody InventoryShiftRequest request) {
        InventoryShiftBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryShiftBussinessRequest.class);
        InventoryShiftBussinessResponse bussinessResponse = inventoryBussiness.shiftInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryShiftResponse.class);
    }

    @ApiOperation("升任务")
    @PostMapping(ServiceId.WMS_INVENTORY_ADJUST_LIFT_UP)
    public InventoryLiftUpResponse liftUpInventory(@Valid @RequestBody InventoryLiftUpRequest request) {
        InventoryLiftUpBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryLiftUpBussinessRequest.class);
        InventoryLiftUpBussinessResponse bussinessResponse = inventoryBussiness.liftUpInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryLiftUpResponse.class);
    }

    @ApiOperation("降任务")
    @PostMapping(ServiceId.WMS_INVENTORY_ADJUST_LIFT_DOWN)
    public InventoryLiftDownResponse liftDownInventory(@Valid @RequestBody InventoryLiftDownRequest request) {
        InventoryLiftDownBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryLiftDownBussinessRequest.class);
        InventoryLiftDownBussinessResponse bussinessResponse = inventoryBussiness.liftDownInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryLiftDownResponse.class);
    }

    @ApiOperation("库存分页")
    @PostMapping(ServiceId.WMS_INVENTORY_PAGE_QUERY)
    public InventoryPageQueryResponse pageQueryInventory(@Valid @RequestBody InventoryPageQueryRequest request){
        InventoryPageQueryBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryPageQueryBussinessRequest.class);
        InventoryPageQueryBussinessResponse bussinessResponse = inventoryBussiness.pageQueryInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryPageQueryResponse.class);
    }

    @ApiOperation("库存调整分页")
    @PostMapping(ServiceId.WMS_INVENTORY_ADJUST_PAGE_QUERY)
    public InventoryAdjustPageQueryResponse pageQueryInventory(@Valid @RequestBody InventoryAdjustPageQueryRequest request){
        InventoryAdjustPageQueryBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryAdjustPageQueryBussinessRequest.class);
        InventoryAdjustPageQueryBussinessResponse bussinessResponse = inventoryBussiness.pageQueryAdjustInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryAdjustPageQueryResponse.class);
    }

    @ApiOperation("库存详情")
    @PostMapping(ServiceId.WMS_INVENTORY_DETAIL)
    public InventoryDetailResponse detialInventory(@Valid @RequestBody InventoryDetailRequest request){
        InventoryDetailBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryDetailBussinessRequest.class);
        InventoryDetailBussinessResponse bussinessResponse = inventoryBussiness.detailInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryDetailResponse.class);
    }

    @ApiOperation("库存扣减")
    @PostMapping(ServiceId.WMS_INVENTORY_DEDUCT)
    public InventoryDeductResponse deductInventory(@Valid @RequestBody InventoryDeductRequest request){
        InventoryDeductBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryDeductBussinessRequest.class);
        InventoryDeductBussinessResponse bussinessResponse = inventoryBussiness.deductInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryDeductResponse.class);
    }

    @ApiOperation("库存变动分页")
    @PostMapping(ServiceId.WMS_INVENTORY_CHANGE_PAGE_QUERY)
    public InventoryChangePageQueryResponse changePageQueryInventory(@Valid @RequestBody InventoryChangePageQueryRequest request){
        InventoryChangePageQueryBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryChangePageQueryBussinessRequest.class);
        InventoryChangePageQueryBussinessResponse bussinessResponse = inventoryBussiness.changePageQueryInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryChangePageQueryResponse.class);
    }


    @ApiOperation("库存变动详情列表")
    @PostMapping(ServiceId.WMS_INVENTORY_CHANGE_LIST)
    public InventoryChangeListResponse changeListInventory(@Valid @RequestBody InventoryChangeListRequest request){
        InventoryChangeListBussinessRequest bussinessRequest = DataConvertUtil.parseDataAsObject(request, InventoryChangeListBussinessRequest.class);
        InventoryDeductBussinessResponse bussinessResponse = inventoryBussiness.changeListInventory(bussinessRequest);
        return DataConvertUtil.parseDataAsObject(bussinessResponse, InventoryChangeListResponse.class);
    }
}
