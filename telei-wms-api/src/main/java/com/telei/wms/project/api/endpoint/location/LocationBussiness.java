package com.telei.wms.project.api.endpoint.location;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.ExcelUtil;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.warehouse.WarehouseFeignClient;
import com.telei.wms.customer.warehouse.dto.WarehouseDetailRequest;
import com.telei.wms.customer.warehouse.dto.WarehouseDetailResponse;
import com.telei.wms.datasource.wms.model.WmsLocation;
import com.telei.wms.datasource.wms.service.WmsLocationService;
import com.telei.wms.datasource.wms.vo.WmsLocationAisleVo;
import com.telei.wms.datasource.wms.vo.WmsLocationVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.location.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 库位
 * @Auther: gongrp
 * @Date: 2020/8/25 17:05
 */
@Service
public class LocationBussiness {

    @Autowired
    private Id idGenerator;

    @Autowired
    private WarehouseFeignClient warehouseFeignClient;

    @Autowired
    private WmsLocationService wmsLocationService;

    /**
     * 新增库位
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public LocationBusinessResponse addLocation(LocationBusinessRequest request) {
        WmsLocation wmsLocation = DataConvertUtil.parseDataAsObject(request, WmsLocation.class);
        wmsLocation.setId(idGenerator.getUnique());
        // 校验库位唯一性
        WmsLocation location = new WmsLocation();
        location.setLcCode(wmsLocation.getLcCode());
        location.setWarehouseCode(wmsLocation.getWarehouseCode());
        WmsLocation oneByEntity = wmsLocationService.selectOneByEntity(location);
        if (StringUtils.isNotNull(oneByEntity)) {
            ErrorCode.WMS_LOCATION_EXIST_4001.throwError();
        }
        int count = wmsLocationService.insert(wmsLocation);
        if (count <= 0) {
            ErrorCode.WMS_LOCATION_ADD_ERROR_4004.throwError();
        }
        LocationBusinessResponse businessResponse = new LocationBusinessResponse();
        businessResponse.setIsSuccess(true);
        return businessResponse;
    }

    /**
     * 修改库位
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public LocationBusinessResponse updateLocation(LocationBusinessRequest request) {
        WmsLocation wmsLocation = DataConvertUtil.parseDataAsObject(request, WmsLocation.class);
        WmsLocation location = wmsLocationService.selectByPrimaryKey(request.getLocationId());
        if (Objects.isNull(location)) {
            ErrorCode.WMS_LOCATION_NOT_EXIST_4002.throwError();
        }
        // 校验库位唯一性
        WmsLocation wLocation = new WmsLocation();
        wLocation.setLcCode(wmsLocation.getLcCode());
        wLocation.setWarehouseCode(wmsLocation.getWarehouseCode());
        WmsLocation oneByEntity = wmsLocationService.selectOneByEntity(wLocation);
        if (StringUtils.isNotNull(oneByEntity)) {
            if (!oneByEntity.getLocationId().equals(location.getLocationId())) {
                ErrorCode.WMS_LOCATION_LC_CODE_EXIST_4005.throwError();
            }
        }
        int count = wmsLocationService.updateByPrimaryKeySelective(wmsLocation);
        if (count <= 0) {
            ErrorCode.WMS_LOCATION_UPDATE_ERROR_4003.throwError();
        }
        LocationBusinessResponse businessResponse = new LocationBusinessResponse();
        businessResponse.setIsSuccess(true);
        return businessResponse;
    }

    /**
     * 删除库位
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public LocationBusinessResponse deleteLocation(LocationBusinessRequest request) {
        List<WmsLocation> locationList = wmsLocationService.selectByPrimaryKeys(request.getIds());
        if (locationList.size() <= 0) {
            ErrorCode.WMS_LOCATION_NOT_EXIST_4002.throwError();
        }
        int count = wmsLocationService.deleteByPrimaryKeys(request.getIds());
        if (count <= 0) {
            ErrorCode.WMS_LOCATION_DELETE_ERROR_4006.throwError();
        }
        LocationBusinessResponse businessResponse = new LocationBusinessResponse();
        businessResponse.setIsSuccess(true);
        return businessResponse;
    }

    /**
     * 库位详细
     *
     * @param request
     * @return
     */
    public LocationBusinessResponse locationDetail(LocationBusinessRequest request) {
        WmsLocation wmsLocation = wmsLocationService.selectByPrimaryKey(request.getLocationId());
        if (Objects.isNull(wmsLocation)) {
            ErrorCode.WMS_LOCATION_NOT_EXIST_4002.throwError();
        }
        return DataConvertUtil.parseDataAsObject(wmsLocation, LocationBusinessResponse.class);
    }

    /**
     * 分页查询主单
     *
     * @param request
     * @return
     */
    public LocationBusinessPageQueryResponse locationPageQuery(LocationBusinessPageQueryRequest request) {
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (StringUtils.isNoneBlank(request.getLcAisle())) {
            conditionsBuilder.like("lcAisle", request.getLcAisle());
        }
        if (StringUtils.isNoneBlank(request.getLcCode())) {
            conditionsBuilder.like("lcCode", request.getLcCode());
        }
        if (StringUtils.isNoneBlank(request.getLcType())) {
            conditionsBuilder.eq("lcType", request.getLcType());
        }
        if (StringUtils.isNoneBlank(request.getWarehouseCode())) {
            conditionsBuilder.eq("warehouseCode", request.getWarehouseCode());
        }
        if (StringUtils.isNotNull(request.getWarehouseId())) {
            conditionsBuilder.eq("warehouseId", request.getWarehouseId());
        }
        Map<String, Object> paramMap = conditionsBuilder.build();
        paramMap.put("orderBy", " location_id DESC");
        paramMap.put("lcLock", request.getLcLock());
        Pagination page = (Pagination) wmsLocationService.findAll(pagination, paramMap);
        LocationBusinessPageQueryResponse response = new LocationBusinessPageQueryResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 分页查询主单
     *
     * @param request
     * @return
     */
    public LocationExcelResponse exportByQuery(LocationBusinessPageQueryRequest businessRequest, HttpServletRequest request) {
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (StringUtils.isNoneBlank(businessRequest.getLcAisle())) {
            conditionsBuilder.like("lcAisle", businessRequest.getLcAisle());
        }
        if (StringUtils.isNoneBlank(businessRequest.getLcCode())) {
            conditionsBuilder.like("lcCode", businessRequest.getLcCode());
        }
        if (StringUtils.isNoneBlank(businessRequest.getLcType())) {
            conditionsBuilder.eq("lcType", businessRequest.getLcType());
        }
        if (StringUtils.isNoneBlank(businessRequest.getWarehouseCode())) {
            conditionsBuilder.eq("warehouseCode", businessRequest.getWarehouseCode());
        }
        if (StringUtils.isNotNull(businessRequest.getWarehouseId())) {
            conditionsBuilder.eq("warehouseId", businessRequest.getWarehouseId());
        }
        Map<String, Object> paramMap = conditionsBuilder.build();
        paramMap.put("orderBy", " location_id DESC");
        paramMap.put("lcLock", businessRequest.getLcLock());
        List<WmsLocationVo> locationList = wmsLocationService.findAll(paramMap);
        List<LocationExportExcel> list = new ArrayList<>();
        if (locationList.size() > 0) {
            for (WmsLocationVo wmsLocation : locationList) {
                LocationExportExcel locationExcel = DataConvertUtil.parseDataAsObject(wmsLocation, LocationExportExcel.class);
                list.add(locationExcel);
            }
        }
        ExcelUtil<LocationExportExcel> util = new ExcelUtil<>(LocationExportExcel.class);
        util.exportExcel(list, "库位模板", request);
        LocationExcelResponse excelResponse = new LocationExcelResponse();
        excelResponse.setIsSuccess(true);
        return excelResponse;
    }

    /**
     * 导入库位数据列表
     *
     * @param localtionList
     * @return 结果
     */
    public void importLocation(List<LocationExcel> localtionList, HttpServletRequest request, HttpServletResponse response) {
        List<LocationExcel> list = new ArrayList<>();
        ExcelUtil<LocationExcel> util = new ExcelUtil<>(LocationExcel.class);
        int successNum = 0;
        int failureNum = 0;
        String returnMsg;
        // 获取当前公司的默认仓库信息
        WarehouseDetailRequest warehouseDetailRequest = new WarehouseDetailRequest();
        warehouseDetailRequest.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        String warehouseCode = "";
        Long warehouseId = 0L;
        try {
            ApiResponse productResponse = warehouseFeignClient.getCompanyWarehouse(warehouseDetailRequest);
            WarehouseDetailResponse detailResponse = JSON.parseObject(JSON.toJSONString(productResponse.getData()), WarehouseDetailResponse.class);
            if (StringUtils.isNotNull(detailResponse)) {
                warehouseCode = detailResponse.getWarehouseCode();
                warehouseId = detailResponse.getId();
            }
        } catch (Exception e) {
            ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
        }
        for (LocationExcel location : localtionList) {
            /**
             * 判断数据库是否存在
             * 存在【提示数据已存在】
             * 不存在【插入数据】
             */
            boolean localtionFlag = false;
            WmsLocation wLocation = new WmsLocation();
            wLocation.setLcCode(location.getLcCode());
            wLocation.setWarehouseCode(warehouseCode);
            List<WmsLocation> cdmCustomerList = wmsLocationService.selectByEntity(wLocation);
            if (cdmCustomerList.size() > 0) {
                localtionFlag = true;
            }
            if (!localtionFlag) {
                WmsLocation wmsLocation = DataConvertUtil.parseDataAsObject(location, WmsLocation.class);
                wmsLocation.setId(idGenerator.getUnique());
                wmsLocation.setWarehouseId(warehouseId);
                wmsLocation.setWarehouseCode(warehouseCode);
                if ("样品库位".equals(location.getLcType())) {
                    wmsLocation.setLcType("S");
                } else if ("高架库位".equals(location.getLcType())) {
                    wmsLocation.setLcType("Z");
                }
                if ("有锁".equals(location.getLcPutawaylock())) {
                    wmsLocation.setLcPutawaylock("1");
                } else if ("无锁".equals(location.getLcPutawaylock())) {
                    wmsLocation.setLcPutawaylock("0");
                }
                if ("有锁".equals(location.getLcPickinglock())) {
                    wmsLocation.setLcType("1");
                } else if ("无锁".equals(location.getLcPickinglock())) {
                    wmsLocation.setLcType("0");
                }
                int insertSelective = wmsLocationService.insertSelective(wmsLocation);
                if (insertSelective > 0) {
                    successNum++;
                }
            } else {
                failureNum++;
                list.add(location);
            }
        }
        returnMsg = localtionList.size() + "," + successNum + "," + failureNum;
        response.setHeader("return-msg", returnMsg);
        if (list.size() > 0) {
            util.exportExcel(list, "上传失败库位", request);
        }
    }

    /**
     * 库位通道列表
     */
    public LocationListResponse listLcAisle(LocationBusinessPageQueryRequest businessRequest) {
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (StringUtils.isNoneBlank(businessRequest.getWarehouseCode())) {
            conditionsBuilder.eq("warehouseCode", businessRequest.getWarehouseCode());
        }
        if (StringUtils.isNotNull(businessRequest.getWarehouseId())) {
            conditionsBuilder.eq("warehouseId", businessRequest.getWarehouseId());
        }
        Map<String, Object> paramMap = conditionsBuilder.build();
        paramMap.put("orderBy", " wl.lc_aisle");
        List<WmsLocation> locationList = wmsLocationService.selectLcAisleByEntity(paramMap);
        LocationListResponse response = new LocationListResponse();
        if (Objects.nonNull(locationList) && !locationList.isEmpty()) {
            response.setLcAisleList(locationList);
        }
        return response;
    }

    /**
     * 根据通道查询库位列表
     */
    public LocationListResponse queryLcLocationByLcAisle(LocationBusinessPageQueryRequest businessRequest) {
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (StringUtils.isNoneBlank(businessRequest.getWarehouseCode())) {
            conditionsBuilder.eq("warehouseCode", businessRequest.getWarehouseCode());
        }
        if (StringUtils.isNotNull(businessRequest.getWarehouseId())) {
            conditionsBuilder.eq("warehouseId", businessRequest.getWarehouseId());
        }
        if (StringUtils.isNoneBlank(businessRequest.getLcAisle())) {
            conditionsBuilder.eq("lcAisle", businessRequest.getLcAisle());
        }
        conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());
        Map<String, Object> paramMap = conditionsBuilder.build();
        paramMap.put("orderBy", " wl.lc_aisle,wl.lc_x,wl.lc_y,wl.lc_z");
        List<WmsLocationVo> locationList = wmsLocationService.queryLcLocationByLcAisle(paramMap);
        // 根据货架对数据进行分组
        Map<String, List<WmsLocationVo>> loListMap = new HashMap<>();
        if (StringUtils.isNotNull(locationList) && !locationList.isEmpty()) {
            loListMap = locationList.stream().collect(Collectors.groupingBy(WmsLocationVo::getLcX));
        }
        LocationListResponse response = new LocationListResponse();
        List<String> lcxlist = new ArrayList<>();
        /**
         * 组装通道个数
         * 默认还原通道示意图
         */
        int lcxcount = 64;
        for (int i = 0; i < lcxcount; i++) {
            if (i < 9) {
                lcxlist.add("0" + (i + 1));
            } else {
                lcxlist.add("" + (i + 1));
            }
        }
        List<WmsLocationVo> wmsLocationVoList = new ArrayList<>();
        if (Objects.nonNull(locationList) && !locationList.isEmpty()) {
            for (int j = 0; j < lcxlist.size(); j++) {
                WmsLocationVo wmsLocationVo = new WmsLocationVo();
                wmsLocationVo.setLcX(lcxlist.get(j));
                List<WmsLocationAisleVo> locationAisleVoList = new ArrayList<>();
                for (WmsLocationVo locationVo : locationList) {
                    if (locationVo.getLcX().equals(lcxlist.get(j))) {
                        List<WmsLocationVo> locationVoList = loListMap.get(locationVo.getLcX());
                        // 将相同层的数据拼接成新数组 每3个一循环
                        String lcY = locationVoList.get(0).getLcY();
                        Map<String, List<WmsLocationVo>> lcymap = new HashMap<>();
                        List<WmsLocationVo> lcylist = new ArrayList<>();
                        for (int i = 0; i < locationVoList.size(); i++) {
                            if (locationVoList.get(i).getLcY().equals(lcY)) {
                                lcylist.add(locationVoList.get(i));
                                if (i == locationVoList.size() - 1) {
                                    lcymap.put(lcY, lcylist);
                                }
                            } else {
                                lcymap.put(lcY, lcylist);
                                lcylist = new ArrayList<>();
                                lcylist.add(locationVoList.get(i));
                                lcY = locationVoList.get(i).getLcY();
                            }
                        }
                        if (j % 2 == 0) {
                            for (int i = 5; i > 0; i--) {
                                List<WmsLocationVo> wmsLocationVos = lcymap.get("" + i);
                                if (wmsLocationVos.size() == 3) {
                                    WmsLocationAisleVo locationAisleVo = new WmsLocationAisleVo();
                                    locationAisleVo.setLcX1(lcxlist.get(j));
                                    locationAisleVo.setLcY1("" + i);
                                    locationAisleVo.setLcZ1(wmsLocationVos.get(0).getLcZ());
                                    locationAisleVo.setProductCount1(wmsLocationVos.get(0).getProductCount());
                                    locationAisleVo.setQty1(wmsLocationVos.get(0).getQty());
                                    locationAisleVo.setRealLc1("Y");

                                    locationAisleVo.setLcX2(lcxlist.get(j));
                                    locationAisleVo.setLcY2("" + i);
                                    locationAisleVo.setLcZ2(wmsLocationVos.get(1).getLcZ());
                                    locationAisleVo.setProductCount2(wmsLocationVos.get(1).getProductCount());
                                    locationAisleVo.setQty2(wmsLocationVos.get(1).getQty());
                                    locationAisleVo.setRealLc2("Y");

                                    locationAisleVo.setLcX3(lcxlist.get(j));
                                    locationAisleVo.setLcY3("" + i);
                                    locationAisleVo.setLcZ3(wmsLocationVos.get(2).getLcZ());
                                    locationAisleVo.setProductCount3(wmsLocationVos.get(2).getProductCount());
                                    locationAisleVo.setQty3(wmsLocationVos.get(2).getQty());
                                    locationAisleVo.setRealLc3("Y");
                                    locationAisleVoList.add(locationAisleVo);
                                }
                            }
                        } else {
                            for (int i = 1; i <= 5; i++) {
                                List<WmsLocationVo> wmsLocationVos = lcymap.get("" + i);
                                if (wmsLocationVos.size() == 3) {
                                    WmsLocationAisleVo locationAisleVo = new WmsLocationAisleVo();
                                    locationAisleVo.setLcX1(lcxlist.get(j));
                                    locationAisleVo.setLcY1("" + i);
                                    locationAisleVo.setLcZ1(wmsLocationVos.get(0).getLcZ());
                                    locationAisleVo.setProductCount1(wmsLocationVos.get(0).getProductCount());
                                    locationAisleVo.setQty1(wmsLocationVos.get(0).getQty());
                                    locationAisleVo.setRealLc1("Y");

                                    locationAisleVo.setLcX2(lcxlist.get(j));
                                    locationAisleVo.setLcY2("" + i);
                                    locationAisleVo.setLcZ2(wmsLocationVos.get(1).getLcZ());
                                    locationAisleVo.setProductCount2(wmsLocationVos.get(1).getProductCount());
                                    locationAisleVo.setQty2(wmsLocationVos.get(1).getQty());
                                    locationAisleVo.setRealLc2("Y");

                                    locationAisleVo.setLcX3(lcxlist.get(j));
                                    locationAisleVo.setLcY3("" + i);
                                    locationAisleVo.setLcZ3(wmsLocationVos.get(2).getLcZ());
                                    locationAisleVo.setProductCount3(wmsLocationVos.get(2).getProductCount());
                                    locationAisleVo.setQty3(wmsLocationVos.get(2).getQty());
                                    locationAisleVo.setRealLc3("Y");
                                    locationAisleVoList.add(locationAisleVo);
                                }
                            }
                        }
                    } else {
                        locationAisleVoList = this.dealLocationList(lcxlist.get(j), j);
                        locationVo.setLcX(lcxlist.get(j));
                    }
                }
                wmsLocationVo.setAisleVoList(locationAisleVoList);
                wmsLocationVoList.add(wmsLocationVo);
            }
            response.setLcCodeList(wmsLocationVoList);
        } else {
            for (int j = 0; j < lcxlist.size(); j++) {
                List<WmsLocationAisleVo> locationAisleVoList = this.dealLocationList(lcxlist.get(j), j);
                WmsLocationVo wmsLocationVo = new WmsLocationVo();
                wmsLocationVo.setLcX(lcxlist.get(j));
                wmsLocationVo.setAisleVoList(locationAisleVoList);
                wmsLocationVoList.add(wmsLocationVo);
            }
            response.setLcCodeList(wmsLocationVoList);
        }
        return response;
    }

    public List<WmsLocationAisleVo> dealLocationList(String lcX, Integer index) {
        List<WmsLocationAisleVo> locationAisleVoList = new ArrayList<>();
        if (index % 2 == 0) {
            for (int i = 5; i > 0; i--) {
                WmsLocationAisleVo locationAisleVo = new WmsLocationAisleVo();
                locationAisleVo.setLcX1(lcX);
                locationAisleVo.setLcY1("" + i);
                locationAisleVo.setLcZ1("1");
                locationAisleVo.setProductCount1(0);
                locationAisleVo.setQty1(0);
                locationAisleVo.setRealLc1("N");

                locationAisleVo.setLcX2(lcX);
                locationAisleVo.setLcY2("" + i);
                locationAisleVo.setLcZ2("2");
                locationAisleVo.setProductCount2(0);
                locationAisleVo.setQty2(0);
                locationAisleVo.setRealLc2("N");

                locationAisleVo.setLcX3(lcX);
                locationAisleVo.setLcY3("" + i);
                locationAisleVo.setLcZ3("3");
                locationAisleVo.setProductCount3(0);
                locationAisleVo.setQty3(0);
                locationAisleVo.setRealLc3("N");
                locationAisleVoList.add(locationAisleVo);
            }
        } else {
            for (int i = 1; i <= 5; i++) {
                WmsLocationAisleVo locationAisleVo = new WmsLocationAisleVo();
                locationAisleVo.setLcX1(lcX);
                locationAisleVo.setLcY1("" + i);
                locationAisleVo.setLcZ1("1");
                locationAisleVo.setProductCount1(0);
                locationAisleVo.setQty1(0);
                locationAisleVo.setRealLc1("N");

                locationAisleVo.setLcX2(lcX);
                locationAisleVo.setLcY2("" + i);
                locationAisleVo.setLcZ2("2");
                locationAisleVo.setProductCount2(0);
                locationAisleVo.setQty2(0);
                locationAisleVo.setRealLc2("N");

                locationAisleVo.setLcX3(lcX);
                locationAisleVo.setLcY3("" + i);
                locationAisleVo.setLcZ3("3");
                locationAisleVo.setProductCount3(0);
                locationAisleVo.setQty3(0);
                locationAisleVo.setRealLc3("N");
                locationAisleVoList.add(locationAisleVo);
            }
        }
        return locationAisleVoList;
    }

}
