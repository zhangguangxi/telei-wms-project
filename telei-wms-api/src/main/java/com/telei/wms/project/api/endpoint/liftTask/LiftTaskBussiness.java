package com.telei.wms.project.api.endpoint.liftTask;

import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.businessNumber.BusinessNumberFeignClient;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberRequest;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberResponse;
import com.telei.wms.datasource.wms.model.WmsLiftWork;
import com.telei.wms.datasource.wms.model.WmsProductSample;
import com.telei.wms.datasource.wms.service.WmsInventoryService;
import com.telei.wms.datasource.wms.service.WmsLiftWorkService;
import com.telei.wms.datasource.wms.service.WmsLocationService;
import com.telei.wms.datasource.wms.service.WmsProductSampleService;
import com.telei.wms.datasource.wms.vo.LiftTaskPageQueryResponseVo;
import com.telei.wms.datasource.wms.vo.WmsInventoryVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.liftTask.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author gongrp
 */
@Service
public class LiftTaskBussiness {

    @Autowired
    private Id idGenerator;

    @Autowired
    private BusinessNumberFeignClient businessNumberFeignClient;

    @Autowired
    private WmsInventoryService wmsInventoryService;

    @Autowired
    private WmsProductSampleService wmsProductSampleService;

    @Autowired
    private WmsLiftWorkService wmsLiftWorkService;

    @Autowired
    private WmsLocationService wmsLocationService;

    /**
     * 新增升降任务
     */
    @Transactional(rollbackFor = Exception.class)
    public LiftTaskBusinessResponse addLiftTask(LiftTaskBusinessRequest request) {
        List<LiftTaskPageQueryResponseVo> liftWorkList = request.getAddLiftWorkList();
        if (StringUtils.isNotNull(liftWorkList) && !liftWorkList.isEmpty()) {
            List<WmsLiftWork> wmsLiftWorkList = new ArrayList<>();
            for (LiftTaskPageQueryResponseVo responseVo : liftWorkList) {
                // 查询当前货位是否有进行中升降任务
                WmsLiftWork wmsLiftWork = new WmsLiftWork();
                wmsLiftWork.setSampleLcCode(responseVo.getLcCode());
                List<WmsLiftWork> liftWorks = wmsLiftWorkService.selectByEntity(wmsLiftWork);
                if (StringUtils.isNotNull(liftWorks) && !liftWorks.isEmpty()) {
                    ErrorCode.LIFT_TASK_ADD_ERROR_4002.throwError(responseVo.getLcCode());
                }
                wmsLiftWork.setId(idGenerator.getUnique());
                wmsLiftWork.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
                wmsLiftWork.setWarehouseId(responseVo.getWarehouseId());
                wmsLiftWork.setWarehouseCode(responseVo.getWarehouseCode());
                // 获取业务单号
                BusinessNumberRequest businessNumberRequest = new BusinessNumberRequest();
                businessNumberRequest.setType("SJH");
                ApiResponse apiResponse = businessNumberFeignClient.get(businessNumberRequest);
                BusinessNumberResponse businessNumberResponse = apiResponse.convertDataToObject(BusinessNumberResponse.class);
                if (StringUtils.isEmpty(businessNumberResponse.getBusinessNumber())) {
                    // 未获取到业务单号
                    ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
                }
                // 升降任务
                wmsLiftWork.setLiftCode(businessNumberResponse.getBusinessNumber());
                wmsLiftWork.setLiftStatus("10");
                wmsLiftWork.setProductId(responseVo.getProductId());
                wmsLiftWork.setProductNo(responseVo.getProductNo());
                wmsLiftWork.setProductName(responseVo.getProductName());
                wmsLiftWork.setProductNameLocal(responseVo.getProductName());
                wmsLiftWork.setProductBarcode(responseVo.getProductBarcode());
                wmsLiftWork.setProductUpcCode(responseVo.getProductUpcCode());
                wmsLiftWork.setBigBagRate(responseVo.getBigBagRate());
                wmsLiftWork.setLiftDocumentType(responseVo.getLcType());
                String lcCode = responseVo.getLcCode();
                String prepLcCode = "";
                int lcCodeNumber = Integer.parseInt(lcCode.replace("-", "").replace("S", ""));
                if ("RISE".equals(responseVo.getLcType())) {
                    prepLcCode = wmsLocationService.getLcCodeByLocation(responseVo.getWarehouseId(), lcCodeNumber);
                    if (StringUtils.isBlank(prepLcCode)) {
                        continue;
//                        ErrorCode.LIFT_WORK_PREP_LC_CODE_IS_NULL_4005.throwError("升货");
                    }
                } else if ("DROP".equals(responseVo.getLcType())) {
                    WmsInventoryVo inventoryVo = wmsInventoryService.getLcCodeByInventory(responseVo.getProductId(), responseVo.getWarehouseId(), CustomRequestContext.getUserInfo().getCompanyId(), lcCodeNumber);
                    if (inventoryVo == null) {
                        continue;
//                        ErrorCode.LIFT_WORK_PREP_LC_CODE_IS_NULL_4005.throwError("降货");
                    } else {
                        prepLcCode = inventoryVo.getLcCode();
                    }
                }
                wmsLiftWork.setLiftType(responseVo.getLcType());
                wmsLiftWork.setPrepLcCode(prepLcCode);
                wmsLiftWork.setCreateTime(DateUtils.nowWithUTC());
                wmsLiftWork.setCreateUser(CustomRequestContext.getUserInfo().getEmployeeName());
                wmsLiftWorkList.add(wmsLiftWork);
            }
            if (wmsLiftWorkList.size() > 0) {
                int count = wmsLiftWorkService.insertBatch(wmsLiftWorkList);
                if (count <= 0) {
                    ErrorCode.LIFT_WORK_ADD_ERROR_4002.throwError();
                }
            } else {
                ErrorCode.LIFT_WORK_ADD_ERROR_4002.throwError();
            }
        }
        LiftTaskBusinessResponse response = new LiftTaskBusinessResponse();
        response.setIsSuccess(true);
        return response;
    }

    /**
     * 批量设置上限/下限
     */
    @Transactional(rollbackFor = Exception.class)
    public LiftTaskBusinessResponse updateLiftTask(LiftTaskBusinessRequest request) {
        List<WmsProductSampleCommonRequest> updateRequestList = request.getUpdateRequestList();
        if (StringUtils.isNotNull(updateRequestList) && !updateRequestList.isEmpty()) {
            List<WmsProductSample> productSampleUpdateList = new ArrayList<>();
            List<WmsProductSample> productSampleAddList = new ArrayList<>();
            for (WmsProductSampleCommonRequest commonRequest : updateRequestList) {
                WmsProductSample wmsProductSample = DataConvertUtil.parseDataAsObject(commonRequest, WmsProductSample.class);
                if ("ceiling".equalsIgnoreCase(request.getLiftTaskType())) {
                    //上限限制
                    if (commonRequest.getCeilingMultiple().compareTo(BigDecimal.valueOf(2)) > 0 || commonRequest.getCeilingMultiple().compareTo(BigDecimal.valueOf(0.2)) < 0) {
                        ErrorCode.LIFT_TASK_COMMON_ERROR_4003.throwError("样品库位上限倍数范围为【0.2~2】");
                    }
                } else if ("limit".equalsIgnoreCase(request.getLiftTaskType())) {
                    //下限限制
                    if (commonRequest.getLimitMultiple().compareTo(BigDecimal.valueOf(2)) > 0 || commonRequest.getLimitMultiple().compareTo(BigDecimal.valueOf(0.2)) < 0) {
                        ErrorCode.LIFT_TASK_COMMON_ERROR_4003.throwError("样品库位下限倍数范围为【0.2~2】");
                    }
                }
                if (StringUtils.isNotNull(wmsProductSample.getId())) {
                    WmsProductSample productSample = wmsProductSampleService.selectByPrimaryKey(wmsProductSample.getId());
                    if ("ceiling".equalsIgnoreCase(request.getLiftTaskType())) {
                        if (StringUtils.isNotNull(productSample.getLimitMultiple())) {
                            if (commonRequest.getCeilingMultiple().compareTo(productSample.getLimitMultiple()) < 0) {
                                ErrorCode.LIFT_TASK_COMMON_ERROR_4003.throwError("样品库位上限倍数不能小于下限倍数");
                            }
                        }
                    } else if ("limit".equalsIgnoreCase(request.getLiftTaskType())) {
                        if (StringUtils.isNotNull(productSample.getCeilingMultiple())) {
                            if (commonRequest.getLimitMultiple().compareTo(productSample.getCeilingMultiple()) > 0) {
                                ErrorCode.LIFT_TASK_COMMON_ERROR_4003.throwError("样品库位下限倍数不能高于上限倍数");
                            }
                        }
                    }
                    productSampleUpdateList.add(wmsProductSample);
                } else {
                    wmsProductSample.setId(idGenerator.getUnique());
                    productSampleAddList.add(wmsProductSample);
                }
            }
            if (productSampleUpdateList.size() > 0) {
                int count = wmsProductSampleService.updateBatch(productSampleUpdateList);
                if (count <= 0) {
                    ErrorCode.LIFT_TASK_UPDATE_ERROR_4001.throwError();
                }
            }
            if (productSampleAddList.size() > 0) {
                int count = wmsProductSampleService.insertBatch(productSampleAddList);
                if (count <= 0) {
                    ErrorCode.LIFT_TASK_UPDATE_ERROR_4001.throwError();
                }
            }
        }
        LiftTaskBusinessResponse businessResponse = new LiftTaskBusinessResponse();
        businessResponse.setIsSuccess(true);
        return businessResponse;
    }

    /**
     * 分页查询主单
     */
    public LiftTaskBusinessPageQueryResponse liftTaskPageQuery(LiftTaskBusinessPageQueryRequest request) {
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (StringUtils.isNoneBlank(request.getProductNo())) {
            conditionsBuilder.like("productNo", request.getProductNo());
        }
        if (StringUtils.isNoneBlank(request.getProductName())) {
            conditionsBuilder.like("productName", request.getProductName());
        }
        if (StringUtils.isNoneBlank(request.getProductBarcode())) {
            conditionsBuilder.like("productBarcode", request.getProductBarcode());
        }
        if (StringUtils.isNoneBlank(request.getLcCode())) {
            conditionsBuilder.like("lcCode", request.getLcCode());
        }
        if (StringUtils.isNoneBlank(request.getLcType())) {
            conditionsBuilder.like("lcType", request.getLcType());
        }
        conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());
        Map<String, Object> paramMap = conditionsBuilder.build();
        paramMap.put("orderBy", " ps.ps_id DESC");
        Pagination page = (Pagination) wmsInventoryService.liftTaskPageQuery(pagination, paramMap);
        LiftTaskBusinessPageQueryResponse response = new LiftTaskBusinessPageQueryResponse();
        response.setPage(page);
        return response;
    }

}
