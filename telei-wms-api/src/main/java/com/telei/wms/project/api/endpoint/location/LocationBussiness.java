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
        if (StringUtils.isBlank(wmsLocation.getLcPickinglock())) {
            wmsLocation.setLcPickinglock("0");
        }
        if (StringUtils.isBlank(wmsLocation.getLcPutawaylock())) {
            wmsLocation.setLcPutawaylock("0");
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
        Pagination page = new Pagination(request.getPageNumber(), request.getPageSize());
        if (StringUtils.isNoneBlank(request.getWarehouseCode()) && StringUtils.isNotNull(request.getWarehouseId())) {
            conditionsBuilder.eq("warehouseCode", request.getWarehouseCode());
            conditionsBuilder.eq("warehouseId", request.getWarehouseId());
            Map<String, Object> paramMap = conditionsBuilder.build();
            paramMap.put("orderBy", " location_id DESC");
            paramMap.put("lcLock", request.getLcLock());
            page = (Pagination) wmsLocationService.findAll(pagination, paramMap);
        }
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
        LocationListResponse response = new LocationListResponse();
        List<WmsLocationVo> wmsLocationVoList = doWmsLocation(locationList, businessRequest.getLcAisle());
        response.setLcCodeList(wmsLocationVoList);
        return response;
    }

    /**
     * 根据通道查询库位列表
     */
    public LocationListResponse queryLcLocationByLcAisleAndInventory(LocationBusinessPageQueryRequest businessRequest) {
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
        List<WmsLocationVo> locationList = wmsLocationService.queryLcLocationByLcAisleAndInventory(paramMap);
        LocationListResponse response = new LocationListResponse();
        List<WmsLocationVo> wmsLocationVoList = doWmsLocation(locationList, businessRequest.getLcAisle());
        response.setLcCodeList(wmsLocationVoList);
        return response;
    }

    /**
     * 根据通道示意图组装数据
     * @param locationList
     * @param lcAisle
     * @return
     */
    public List<WmsLocationVo> doWmsLocation(List<WmsLocationVo> locationList, String lcAisle) {
        // 根据货架对数据进行分组
        Map<String, List<WmsLocationVo>> loListMap = new HashMap<>();
        if (StringUtils.isNotNull(locationList) && !locationList.isEmpty()) {
            loListMap = locationList.stream().collect(Collectors.groupingBy(WmsLocationVo::getLcX));
        }
        List<String> lcxlist = new ArrayList<>();
        /**
         * 组装通道个数
         * 默认还原通道示意图
         */
        int lcxCount = 64;
        for (int i = 0; i < lcxCount; i++) {
            if (i < 9) {
                lcxlist.add("0" + (i + 1));
            } else {
                lcxlist.add("" + (i + 1));
            }
        }
        List<WmsLocationVo> wmsLocationVoList = new ArrayList<>();
        if (Objects.nonNull(locationList) && !locationList.isEmpty()) {
            // 循环64个通道
            for (int j = 0; j < lcxlist.size(); j++) {
                WmsLocationVo wmsLocationVo = new WmsLocationVo();
                wmsLocationVo.setLcX(lcxlist.get(j));
                List<WmsLocationAisleVo> locationAisleVoList = new ArrayList<>();
                // 获取当前通道的数据
                List<WmsLocationVo> locationVoList = loListMap.get(lcxlist.get(j));
                if (StringUtils.isNotNull(locationVoList) && !locationVoList.isEmpty()) {
                    // 将相同层的数据拼接成新数组 每3个一循环
                    String lcY = locationVoList.get(0).getLcY();
                    Map<String, List<WmsLocationVo>> lcymap = new HashMap<>();
                    List<WmsLocationVo> lcylist = new ArrayList<>();
                    for (int i = 0; i < locationVoList.size(); i++) {
                        // 判断是否为相同层 相同层的数据放在一个Map
                        if (locationVoList.get(i).getLcY().equals(lcY)) {
                            lcylist.add(locationVoList.get(i));
                            // 判断是否为最后一个数据
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
                        for (int k = 5; k > 0; k--) {
                            List<WmsLocationVo> wmsLocationVos = lcymap.get("" + k);
                            if (StringUtils.isNotNull(wmsLocationVos) && !wmsLocationVos.isEmpty()) {
                                if (wmsLocationVos.size() == 3) {
                                    doLocationAisleVoList(lcxlist.get(j), "" + k, wmsLocationVos.get(0).getLcZ(), wmsLocationVos.get(0).getProductCount(), wmsLocationVos.get(0).getQty(), wmsLocationVos.get(0).getLcType(), wmsLocationVos.get(0).getLcCode(), "Y", wmsLocationVos.get(1).getLcZ(), wmsLocationVos.get(1).getProductCount(), wmsLocationVos.get(1).getQty(), wmsLocationVos.get(1).getLcType(), wmsLocationVos.get(1).getLcCode(), "Y", wmsLocationVos.get(2).getLcZ(), wmsLocationVos.get(2).getProductCount(), wmsLocationVos.get(2).getQty(), wmsLocationVos.get(2).getLcType(), wmsLocationVos.get(2).getLcCode(), "Y", locationAisleVoList);
                                } else {
                                    String lcX1 = lcxlist.get(j);
                                    String lcY1 = "" + k;
                                    String lcZ1 = "1", lcZ2 = "2", lcZ3 = "3", lcType = "Z";
                                    String lcExist1 = "N", lcExist2 = "N", lcExist3 = "N";
                                    String lcCode1 = lcType + lcAisle + "-" + lcX1 + "-" + lcY1 + lcZ1;
                                    String lcCode2 = lcType + lcAisle + "-" + lcX1 + "-" + lcY1 + lcZ2;
                                    String lcCode3 = lcType + lcAisle + "-" + lcX1 + "-" + lcY1 + lcZ3;
                                    Integer productCount1 = 0, qty1 = 0, productCount2 = 0, qty2 = 0, productCount3 = 0, qty3 = 0;
                                    if (k < 3) {
                                        lcType = "S";
                                    }
                                    for (WmsLocationVo locationVo : wmsLocationVos) {
                                        if ("1".equals(locationVo.getLcZ())) {
                                            lcType = locationVo.getLcType();
                                            productCount1 = locationVo.getProductCount();
                                            qty1 = locationVo.getQty();
                                            lcExist1 = "Y";
                                        } else if ("2".equals(locationVo.getLcZ())) {
                                            lcType = locationVo.getLcType();
                                            productCount2 = locationVo.getProductCount();
                                            qty2 = locationVo.getQty();
                                            lcExist2 = "Y";
                                        } else if ("3".equals(locationVo.getLcZ())) {
                                            lcType = locationVo.getLcType();
                                            productCount3 = locationVo.getProductCount();
                                            qty3 = locationVo.getQty();
                                            lcExist3 = "Y";
                                        }
                                    }
                                    doLocationAisleVoList(lcX1, lcY1, lcZ1, productCount1, qty1, lcType, lcCode1, lcExist1, lcZ2, productCount2, qty2, lcType, lcCode2, lcExist2, lcZ3, productCount3, qty3, lcType, lcCode3, lcExist3, locationAisleVoList);
                                }
                            } else {
                                String lcType = "Z";
                                if (k < 3) {
                                    lcType = "S";
                                }
                                doLocationAisleVoList(lcxlist.get(j), "" + k, "1", 0, 0, lcType, lcType + lcAisle + "-" + lcxlist.get(j) + "-" + k + "1", "N", "2", 0, 0, lcType, lcType + lcAisle + "-" + lcxlist.get(j) + "-" + k + "2", "N", "3", 0, 0, lcType, lcType + lcAisle + "-" + lcxlist.get(j) + "-" + k + "3", "N", locationAisleVoList);
                            }
                        }
                    } else {
                        for (int k = 1; k <= 5; k++) {
                            List<WmsLocationVo> wmsLocationVos = lcymap.get("" + k);
                            if (StringUtils.isNotNull(wmsLocationVos) && !wmsLocationVos.isEmpty()) {
                                if (wmsLocationVos.size() == 3) {
                                    doLocationAisleVoList(lcxlist.get(j), "" + k, wmsLocationVos.get(0).getLcZ(), wmsLocationVos.get(0).getProductCount(), wmsLocationVos.get(0).getQty(), wmsLocationVos.get(0).getLcType(), wmsLocationVos.get(0).getLcCode(), "Y", wmsLocationVos.get(1).getLcZ(), wmsLocationVos.get(1).getProductCount(), wmsLocationVos.get(1).getQty(), wmsLocationVos.get(1).getLcType(), wmsLocationVos.get(1).getLcCode(), "Y", wmsLocationVos.get(2).getLcZ(), wmsLocationVos.get(2).getProductCount(), wmsLocationVos.get(2).getQty(), wmsLocationVos.get(2).getLcType(), wmsLocationVos.get(2).getLcCode(), "Y", locationAisleVoList);
                                } else {
                                    String lcX1 = lcxlist.get(j);
                                    String lcY1 = "" + k;
                                    String lcZ1 = "1", lcZ2 = "2", lcZ3 = "3", lcType = "Z";
                                    if (k < 3) {
                                        lcType = "S";
                                    }
                                    String lcExist1 = "N", lcExist2 = "N", lcExist3 = "N";
                                    String lcCode1 = lcType + lcAisle + "-" + lcX1 + "-" + lcY1 + lcZ1;
                                    String lcCode2 = lcType + lcAisle + "-" + lcX1 + "-" + lcY1 + lcZ2;
                                    String lcCode3 = lcType + lcAisle + "-" + lcX1 + "-" + lcY1 + lcZ3;
                                    Integer productCount1 = 0, qty1 = 0, productCount2 = 0, qty2 = 0, productCount3 = 0, qty3 = 0;
                                    for (WmsLocationVo locationVo : wmsLocationVos) {
                                        if ("1".equals(locationVo.getLcZ())) {
                                            lcType = locationVo.getLcType();
                                            productCount1 = locationVo.getProductCount();
                                            qty1 = locationVo.getQty();
                                            lcExist1 = "Y";
                                        } else if ("2".equals(locationVo.getLcZ())) {
                                            lcType = locationVo.getLcType();
                                            productCount2 = locationVo.getProductCount();
                                            qty2 = locationVo.getQty();
                                            lcExist2 = "Y";
                                        } else if ("3".equals(locationVo.getLcZ())) {
                                            lcType = locationVo.getLcType();
                                            productCount3 = locationVo.getProductCount();
                                            qty3 = locationVo.getQty();
                                            lcExist3 = "Y";
                                        }
                                    }
                                    doLocationAisleVoList(lcX1, lcY1, lcZ1, productCount1, qty1, lcType, lcCode1, lcExist1, lcZ2, productCount2, qty2, lcType, lcCode2, lcExist2, lcZ3, productCount3, qty3, lcType, lcCode3, lcExist3, locationAisleVoList);
                                }
                            } else {
                                String lcType = "Z";
                                if (k < 3) {
                                    lcType = "S";
                                }
                                doLocationAisleVoList(lcxlist.get(j), "" + k, "1", 0, 0, lcType, lcType + lcAisle + "-" + lcxlist.get(j) + "-" + k + "1", "N", "2", 0, 0, lcType, lcType + lcAisle + "-" + lcxlist.get(j) + "-" + k + "2", "N", "3", 0, 0, lcType, lcType + lcAisle + "-" + lcxlist.get(j) + "-" + k + "3", "N", locationAisleVoList);
                            }
                        }
                    }
                } else {
                    locationAisleVoList = this.dealLocationList(lcAisle, lcxlist.get(j), j);
                }
                wmsLocationVo.setAisleVoList(locationAisleVoList);
                wmsLocationVoList.add(wmsLocationVo);
            }
        } else {
            for (int j = 0; j < lcxlist.size(); j++) {
                List<WmsLocationAisleVo> locationAisleVoList = this.dealLocationList(lcAisle, lcxlist.get(j), j);
                WmsLocationVo wmsLocationVo = new WmsLocationVo();
                wmsLocationVo.setLcX(lcxlist.get(j));
                wmsLocationVo.setAisleVoList(locationAisleVoList);
                wmsLocationVoList.add(wmsLocationVo);
            }
        }
        return wmsLocationVoList;
    }

    /**
     * 处理通道上下层数据
     * 【上下层数据完全相反】
     */
    public List<WmsLocationAisleVo> dealLocationList(String lcAisle, String lcX, Integer index) {
        List<WmsLocationAisleVo> locationAisleVoList = new ArrayList<>();
        if (index % 2 == 0) {
            for (int i = 5; i > 0; i--) {
                String lcType = "Z";
                if (i < 3) {
                    lcType = "S";
                }
                doLocationAisleVoList(lcX, "" + i, "1", 0, 0, lcType, lcType + lcAisle + "-" + lcX + "-" + i + "1", "N", "2", 0, 0, lcType, lcType + lcAisle + "-" + lcX + "-" + i + "2", "N", "3", 0, 0, lcType, lcType + lcAisle + "-" + lcX + "-" + i + "3", "N", locationAisleVoList);
            }
        } else {
            for (int i = 1; i <= 5; i++) {
                String lcType = "Z";
                if (i < 3) {
                    lcType = "S";
                }
                doLocationAisleVoList(lcX, "" + i, "1", 0, 0, lcType, lcType + lcAisle + "-" + lcX + "-" + i + "1", "N", "2", 0, 0, lcType, lcType + lcAisle + "-" + lcX + "-" + i + "2", "N", "3", 0, 0, lcType, lcType + lcAisle + "-" + lcX + "-" + i + "3", "N", locationAisleVoList);
            }
        }
        return locationAisleVoList;
    }

    /**
     * 组装WmsLocationAisleVo对象
     */
    public void doLocationAisleVoList(String lcX, String lcY, String lcZ1, Integer productCount1, Integer qty1, String lcType1, String lcCode1, String lcExist1, String lcZ2, Integer productCount2, Integer qty2, String lcType2, String lcCode2, String lcExist2, String lcZ3, Integer productCount3, Integer qty3, String lcType3, String lcCode3, String lcExist3, List<WmsLocationAisleVo> locationAisleVoList) {
        WmsLocationAisleVo locationAisleVo = new WmsLocationAisleVo();
        locationAisleVo.setLcX1(lcX);
        locationAisleVo.setLcY1(lcY);
        locationAisleVo.setLcZ1(lcZ1);
        locationAisleVo.setProductCount1(productCount1);
        locationAisleVo.setQty1(qty1);
        locationAisleVo.setLcType1(lcType1);
        locationAisleVo.setLcCode1(lcCode1);
        locationAisleVo.setLcExist1(lcExist1);

        locationAisleVo.setLcX2(lcX);
        locationAisleVo.setLcY2(lcY);
        locationAisleVo.setLcZ2(lcZ2);
        locationAisleVo.setProductCount2(productCount2);
        locationAisleVo.setQty2(qty2);
        locationAisleVo.setLcType2(lcType2);
        locationAisleVo.setLcCode2(lcCode2);
        locationAisleVo.setLcExist2(lcExist2);

        locationAisleVo.setLcX3(lcX);
        locationAisleVo.setLcY3(lcY);
        locationAisleVo.setLcZ3(lcZ3);
        locationAisleVo.setProductCount3(productCount3);
        locationAisleVo.setQty3(qty3);
        locationAisleVo.setLcType3(lcType3);
        locationAisleVo.setLcCode3(lcCode3);
        locationAisleVo.setLcExist3(lcExist3);
        locationAisleVoList.add(locationAisleVo);
    }

}
