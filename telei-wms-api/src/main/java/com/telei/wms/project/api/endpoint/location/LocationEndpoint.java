package com.telei.wms.project.api.endpoint.location;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.commons.utils.ExcelUtil;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.location.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gongrp
 * @date 2020/8/25 15:03
 */
@Api(tags = "库位接口")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class LocationEndpoint {

    @Autowired
    private LocationBussiness locationBussiness;

    @ApiOperation("新增库位")
    @PostMapping(ServiceId.WMS_LOCATION_ADD)
    public LocationAddResponse addLocation(@RequestBody @Valid LocationAddRequest request) {
        LocationBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LocationBusinessRequest.class);
        LocationBusinessResponse businessResponse = locationBussiness.addLocation(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LocationAddResponse.class);
    }

    @ApiOperation("修改库位")
    @PostMapping(ServiceId.WMS_LOCATION_UPDATE)
    public LocationUpdateResponse updateLocation(@RequestBody @Valid LocationUpdateRequest request) {
        LocationBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LocationBusinessRequest.class);
        LocationBusinessResponse businessResponse = locationBussiness.updateLocation(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LocationUpdateResponse.class);
    }

    @ApiOperation("删除库位")
    @PostMapping(ServiceId.WMS_LOCATION_DELETE)
    public LocationDeleteResponse deleteLocation(@RequestBody @Valid LocationDeleteRequest request) {
        LocationBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LocationBusinessRequest.class);
        LocationBusinessResponse businessResponse = locationBussiness.deleteLocation(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LocationDeleteResponse.class);
    }

    @ApiOperation("库位分页")
    @PostMapping(ServiceId.WMS_LOCATION_PAGE_QUERY)
    public LocationPageQueryResponse locationPageQuery(@RequestBody @Valid LocationPageQueryRequest request) {
        LocationBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LocationBusinessPageQueryRequest.class);
        LocationBusinessPageQueryResponse businessResponse = locationBussiness.locationPageQuery(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LocationPageQueryResponse.class);
    }

    @ApiOperation("库位详情")
    @PostMapping(ServiceId.WMS_LOCATION_DETAIL)
    public LocationDetailResponse locationDetail(@RequestBody @Valid LocationDetailRequest request) {
        LocationBusinessRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LocationBusinessRequest.class);
        LocationBusinessResponse businessResponse = locationBussiness.locationDetail(businessRequest);
        return DataConvertUtil.parseDataAsObject(businessResponse, LocationDetailResponse.class);
    }

    @ApiOperation("下载批量新增库位模板")
    @PostMapping(ServiceId.WMS_LOCATION_EXCEL_EXPORT)
    public LocationExcelResponse exportLocation(HttpServletRequest request) {
        List<LocationExcel> list = new ArrayList<>();
        list.add(new LocationExcel("11-01-01", "11", "01", "01", "01", "S", BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.valueOf(100), 100, "0", "0", "备注"));
        ExcelUtil<LocationExcel> util = new ExcelUtil<>(LocationExcel.class);
        util.exportExcel(list, "库位模板", request);
        LocationExcelResponse excelResponse = new LocationExcelResponse();
        excelResponse.setIsSuccess(true);
        return excelResponse;
    }

    @ApiOperation("上传批量新增库位模板")
    @PostMapping(ServiceId.WMS_LOCATION_EXCEL_IMPORT)
    public LocationExcelResponse importLocation(MultipartFile file, HttpServletResponse response, HttpServletRequest request) throws Exception {
        ExcelUtil<LocationExcel> util = new ExcelUtil<>(LocationExcel.class);
        List<LocationExcel> locationList = util.importExcel(file.getInputStream());
        if (StringUtils.isNull(locationList) || locationList.size() == 0) {
            ErrorCode.WMS_LOCATION_LIST_IS_NULL_4007.throwError();
        }
        locationBussiness.importLocation(locationList, request, response);
        LocationExcelResponse excelResponse = new LocationExcelResponse();
        excelResponse.setIsSuccess(true);
        return excelResponse;
    }

    @ApiOperation("根据条件导出库位列表数据")
    @PostMapping(ServiceId.WMS_LOCATION_EXPORT_BY_QUERY)
    public LocationExcelResponse exportByQuery(@RequestBody LocationPageQueryRequest pageQueryRequest, HttpServletRequest request) {
        LocationBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(pageQueryRequest, LocationBusinessPageQueryRequest.class);
        return locationBussiness.exportByQuery(businessRequest, request);

    }

    @ApiOperation("库位通道列表")
    @PostMapping(ServiceId.WMS_LOCATION_LC_AISLE_LIST)
    public LocationListResponse listLcAisle(@RequestBody @Valid LocationListRequest request){
        LocationBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LocationBusinessPageQueryRequest.class);
        return locationBussiness.listLcAisle(businessRequest);
    }

    @ApiOperation("根据通道查询库位列表【库存】")
    @PostMapping(ServiceId.WMS_LOCATION_BY_LC_AISLE)
    public LocationListResponse queryLcLocationByLcAisle(@RequestBody @Valid LocationListRequest request){
        LocationBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LocationBusinessPageQueryRequest.class);
        return locationBussiness.queryLcLocationByLcAisle(businessRequest);
    }

    @ApiOperation("根据通道查询库位列表【库存+推荐库位】")
    @PostMapping(ServiceId.WMS_LOCATION_BY_LC_AISLE_INVENTORY)
    public LocationListResponse queryLcLocationByLcAisleAndInventory(@RequestBody @Valid LocationListRequest request){
        LocationBusinessPageQueryRequest businessRequest = DataConvertUtil.parseDataAsObject(request, LocationBusinessPageQueryRequest.class);
        return locationBussiness.queryLcLocationByLcAisleAndInventory(businessRequest);
    }

}
