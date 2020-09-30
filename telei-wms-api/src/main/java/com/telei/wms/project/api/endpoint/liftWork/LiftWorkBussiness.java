package com.telei.wms.project.api.endpoint.liftWork;

import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.utils.DateUtils;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.businessNumber.BusinessNumberFeignClient;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberRequest;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberResponse;
import com.telei.wms.datasource.wms.model.WmsInventory;
import com.telei.wms.datasource.wms.model.WmsLiftWork;
import com.telei.wms.datasource.wms.service.WmsInventoryService;
import com.telei.wms.datasource.wms.service.WmsLiftWorkService;
import com.telei.wms.datasource.wms.service.WmsLocationService;
import com.telei.wms.datasource.wms.vo.WmsInventoryVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.inventory.InventoryBussiness;
import com.telei.wms.project.api.endpoint.liftWork.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gongrp
 */
@Service
public class LiftWorkBussiness {

    @Autowired
    private Id idGenerator;

    @Autowired
    private BusinessNumberFeignClient businessNumberFeignClient;

    @Autowired
    private WmsLiftWorkService wmsLiftWorkService;

    @Autowired
    private WmsInventoryService wmsInventoryService;

    @Autowired
    private WmsLocationService wmsLocationService;

    @Autowired
    private InventoryBussiness inventoryBussiness;

    /**
     * 新增升降任务
     */
    @Transactional(rollbackFor = Exception.class)
    public LiftWorkBusinessResponse addLiftWork(LiftWorkBusinessRequest request) {
        WmsLiftWork wmsLiftWork = DataConvertUtil.parseDataAsObject(request, WmsLiftWork.class);
        wmsLiftWork.setId(idGenerator.getUnique());
        wmsLiftWork.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        // 获取业务单号
        BusinessNumberRequest businessNumberRequest = new BusinessNumberRequest();
        businessNumberRequest.setType("WMS");
        ApiResponse apiResponse = businessNumberFeignClient.get(businessNumberRequest);
        BusinessNumberResponse businessNumberResponse = apiResponse.convertDataToObject(BusinessNumberResponse.class);
        if (StringUtils.isEmpty(businessNumberResponse.getBusinessNumber())) {
            // 未获取到业务单号
            ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
        }
        // 升降任务
        wmsLiftWork.setLiftCode(businessNumberResponse.getBusinessNumber());
        wmsLiftWork.setLiftStatus("10");
        wmsLiftWork.setLiftDocumentType("ADD");
        wmsLiftWork.setCreateTime(DateUtils.nowWithUTC());
        wmsLiftWork.setCreateUser(CustomRequestContext.getUserInfo().getUserName());
        // 计算大包数量以及大包剩余数量
        BigDecimal[] big = wmsLiftWork.getLiftQty().divideAndRemainder(wmsLiftWork.getBigBagRate());
        wmsLiftWork.setBigBagQty(big[0]);
        wmsLiftWork.setBigBagExtraQty(big[1]);
        int count = wmsLiftWorkService.insert(wmsLiftWork);
        if (count <= 0) {
            ErrorCode.LIFT_WORK_ADD_ERROR_4002.throwError();
        }
        LiftWorkBusinessResponse response = new LiftWorkBusinessResponse();
        response.setIsSuccess(true);
        return response;
    }

    /**
     * 批量撤销升降任务
     */
    @Transactional(rollbackFor = Exception.class)
    public LiftWorkBusinessResponse revokeLiftWork(LiftWorkBusinessRequest request) {
        List<WmsLiftWork> liftWorkList = wmsLiftWorkService.selectByPrimaryKeys(request.getIds());
        if (liftWorkList.size() <= 0 || liftWorkList.size() != request.getIds().size()) {
            ErrorCode.LIFT_WORK_NOT_EXIST_4001.throwError();
        }
        int count = wmsLiftWorkService.revokeLiftWork(request.getIds());
        if (count <= 0) {
            ErrorCode.LIFT_WORK_REVOKE_ERROR_4003.throwError();
        }
        LiftWorkBusinessResponse businessResponse = new LiftWorkBusinessResponse();
        businessResponse.setIsSuccess(true);
        return businessResponse;
    }

    /**
     * 批量处理升降任务
     */
    @Transactional(rollbackFor = Exception.class)
    public LiftWorkBusinessResponse updateLiftWork(LiftWorkBusinessRequest request) {
        List<LiftWorkCommonRequest> updateRequestList = request.getUpdateRequestList();
        List<Long> liftIds = updateRequestList.stream().map(LiftWorkCommonRequest::getId).collect(Collectors.toList());
        List<WmsLiftWork> liftWorkList = wmsLiftWorkService.selectByPrimaryKeys(liftIds);
        if (liftWorkList.size() <= 0 || liftWorkList.size() != updateRequestList.size()) {
            ErrorCode.LIFT_WORK_NOT_EXIST_4001.throwError();
        }
        for (LiftWorkCommonRequest workCommonRequest : updateRequestList) {
            WmsLiftWork wmsLiftWork = DataConvertUtil.parseDataAsObject(workCommonRequest, WmsLiftWork.class);
            if ("20".equals(wmsLiftWork.getLiftStatus())) {
                ErrorCode.LIFT_WORK_IS_UPDATE_4008.throwError();
            }
            wmsLiftWork.setLiftStatus("20");
            wmsLiftWork.setOperateTime(DateUtils.nowWithUTC());
            wmsLiftWork.setLastupdateTime(DateUtils.nowWithUTC());
            wmsLiftWork.setLastupdateUser(CustomRequestContext.getUserInfo().getUserName());
            /**
             * 检查当前库位是否为空
             * 有实际库位取实际库位 无实际库位取推荐库位
             */
            String lcCode = StringUtils.isNoneBlank(wmsLiftWork.getLcCode()) ? wmsLiftWork.getLcCode() : wmsLiftWork.getPrepLcCode();
            WmsInventory wmsInventory = new WmsInventory();
            wmsInventory.setLcType("Z");
            wmsInventory.setLcCode(lcCode);
            List<WmsInventory> inventoryList = wmsInventoryService.selectByEntity(wmsInventory);
            // 初始化库存调整单
            InventoryLiftBussinessRequest businessRequest = new InventoryLiftBussinessRequest();
            businessRequest.setCompanyId(wmsLiftWork.getCompanyId());
            businessRequest.setProductId(wmsLiftWork.getProductId());
            businessRequest.setWarehouseCode(wmsLiftWork.getWarehouseCode());
            businessRequest.setWarehouseId(wmsLiftWork.getWarehouseId());
            if ("RISE".equals(request.getLiftType())) {
                /**
                 * 判断当前库位是否为空
                 * 为空继续 反之抛出异常
                 */
                if (StringUtils.isNotNull(inventoryList) && !inventoryList.isEmpty()) {
                    ErrorCode.LIFT_WORK_LC_CODE_IS_NOT_NULL_4007.throwError();
                }
                // 查询当前商品 当前库位 库存
                WmsInventory inventory = new WmsInventory();
                inventory.setCompanyId(wmsLiftWork.getCompanyId());
                inventory.setProductId(wmsLiftWork.getProductId());
                inventory.setWarehouseCode(wmsLiftWork.getWarehouseCode());
                inventory.setWarehouseId(wmsLiftWork.getWarehouseId());
                inventory.setLcCode(wmsLiftWork.getSampleLcCode());
                List<WmsInventory> inventories = wmsInventoryService.selectByEntity(inventory);
                BigDecimal ivQty = BigDecimal.ZERO;
                if (StringUtils.isNotNull(inventories) && !inventories.isEmpty()) {
                    ivQty = inventories.stream().map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                // 写入库存调整单
                businessRequest.setLcCode(wmsLiftWork.getSampleLcCode());
                businessRequest.setIvQty(ivQty);
                businessRequest.setLcCodeAdjt(lcCode);
                businessRequest.setIvQtyAdjt(wmsLiftWork.getLiftQty());
                businessRequest.setReason("升降任务-升货处理");
                inventoryBussiness.adjustInventory(businessRequest, "LIFTUP");
            } else if ("DROP".equals(request.getLiftType())) {
                /**
                 * 判断当前库位是否为空
                 * 为空抛异常 反之继续
                 * 判断样品库位与高库位是否为同一sku
                 */
                if (StringUtils.isNull(inventoryList) || inventoryList.isEmpty()) {
                    ErrorCode.LIFT_WORK_LC_CODE_IS_NULL_4006.throwError();
                }
                List<Long> productIds = inventoryList.stream().map(WmsInventory::getProductId).collect(Collectors.toList());
                if (!productIds.contains(wmsLiftWork.getProductId())) {
                    ErrorCode.LIFT_WORK_PRODUCT_IS_NULL_EQUALS_4009.throwError();
                }
                // 查询当前商品 当前库位 库存
                WmsInventory inventory = new WmsInventory();
                inventory.setCompanyId(wmsLiftWork.getCompanyId());
                inventory.setProductId(wmsLiftWork.getProductId());
                inventory.setWarehouseCode(wmsLiftWork.getWarehouseCode());
                inventory.setWarehouseId(wmsLiftWork.getWarehouseId());
                inventory.setLcCode(lcCode);
                List<WmsInventory> inventories = wmsInventoryService.selectByEntity(inventory);
                BigDecimal ivQty = BigDecimal.ZERO;
                if (StringUtils.isNotNull(inventories) && !inventories.isEmpty()) {
                    ivQty = inventories.stream().map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                // 写入库存调整单
                businessRequest.setLcCode(lcCode);
                businessRequest.setIvQty(ivQty);
                businessRequest.setLcCodeAdjt(wmsLiftWork.getSampleLcCode());
                businessRequest.setIvQtyAdjt(wmsLiftWork.getLiftQty());
                businessRequest.setReason("升降任务-降货处理");
                inventoryBussiness.adjustInventory(businessRequest, "LIFTDOWN");
            }
            if (StringUtils.isNotNull(wmsLiftWork.getBigBagRate())) {
                BigDecimal[] big = wmsLiftWork.getLiftQty().divideAndRemainder(wmsLiftWork.getBigBagRate());
                wmsLiftWork.setBigBagQty(big[0]);
                wmsLiftWork.setBigBagExtraQty(big[1]);
            }
            int count = wmsLiftWorkService.updateByPrimaryKeySelective(wmsLiftWork);
            if (count <= 0) {
                ErrorCode.LIFT_WORK_UPDATE_ERROR_4004.throwError(wmsLiftWork.getProductName(), wmsLiftWork.getSampleLcCode(), wmsLiftWork.getPrepLcCode());
            }
        }
        LiftWorkBusinessResponse businessResponse = new LiftWorkBusinessResponse();
        businessResponse.setIsSuccess(true);
        return businessResponse;
    }

    /**
     * 分页查询主单
     */
    public LiftWorkBusinessPageQueryResponse liftWorkPageQuery(LiftWorkBusinessPageQueryRequest request) {
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (null != request.getStartTime() && null != request.getEndTime()) {
            conditionsBuilder.between("createTime", request.getStartTime(), request.getEndTime());
        }
        if (StringUtils.isNoneBlank(request.getProductNo())) {
            conditionsBuilder.like("productNo", request.getProductNo());
        }
        if (StringUtils.isNoneBlank(request.getProductName())) {
            conditionsBuilder.like("productName", request.getProductName());
        }
        if (StringUtils.isNoneBlank(request.getProductBarcode())) {
            conditionsBuilder.like("productBarcode", request.getProductBarcode());
        }
        if (StringUtils.isNoneBlank(request.getSampleLcCode())) {
            conditionsBuilder.like("sampleLcCode", request.getSampleLcCode());
        }
        if (StringUtils.isNoneBlank(request.getLiftType())) {
            conditionsBuilder.eq("liftType", request.getLiftType());
        }
        if (StringUtils.isNoneBlank(request.getLiftStatus())) {
            conditionsBuilder.eq("liftStatus", request.getLiftStatus());
        }
        if (StringUtils.isNoneBlank(request.getOperateUser())) {
            conditionsBuilder.like("operateUser", request.getOperateUser());
        }
        conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());
        Map<String, Object> paramMap = conditionsBuilder.build();
        paramMap.put("orderBy", " lift_id DESC");
        Pagination page = (Pagination) wmsLiftWorkService.selectPage(pagination, paramMap);
        LiftWorkBusinessPageQueryResponse response = new LiftWorkBusinessPageQueryResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 查询产品对应库位信息
     */
    public LiftWorkBusinessResponse getLiftWorkInfo(LiftWorkBusinessRequest request) {
        /**
         * 查询当前商品的样品库位
         *  0/n个抛出异常
         *  拿到当前样品库位计算推荐库位
         */
        WmsInventory wmsInventory = new WmsInventory();
        wmsInventory.setProductId(request.getProductId());
        wmsInventory.setWarehouseId(request.getWarehouseId());
        wmsInventory.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        wmsInventory.setLcType("S");
        List<WmsInventory> wmsInventoryList = wmsInventoryService.selectByEntity(wmsInventory);
        if (StringUtils.isNull(wmsInventoryList) || wmsInventoryList.isEmpty()) {
            ErrorCode.INVENTORY_ERROR_PRODUCT_NOT_EXIST_40041.throwError();
        }
        if (wmsInventoryList.size() > 1) {
            ErrorCode.INVENTORY_ERROR_PRODUCT_IS_MULTIPLE_40042.throwError();
        }
        String lcCode = wmsInventoryList.get(0).getLcCode();
        String prepLcCode = "";
        BigDecimal qty = BigDecimal.ZERO;
        int lcCodeNumber = Integer.parseInt(lcCode.replace("-", "").replace("S", ""));
        if ("RISE".equals(request.getLiftType())) {
            prepLcCode = wmsLocationService.getLcCodeByLocation(lcCodeNumber);
            if (StringUtils.isBlank(prepLcCode)) {
                ErrorCode.LIFT_WORK_PREP_LC_CODE_IS_NULL_4005.throwError();
            }
        } else if ("DROP".equals(request.getLiftType())) {
            WmsInventoryVo inventoryVo = wmsInventoryService.getLcCodeByInventory(request.getProductId(), request.getWarehouseId(), CustomRequestContext.getUserInfo().getCompanyId(), lcCodeNumber);
            if (inventoryVo == null) {
                ErrorCode.LIFT_WORK_PREP_LC_CODE_IS_NULL_4005.throwError();
            } else {
                qty = inventoryVo.getQty();
                prepLcCode = inventoryVo.getLcCode();
            }
        }
        LiftWorkBusinessResponse response = new LiftWorkBusinessResponse();
        response.setIsSuccess(true);
        response.setPrepLcCode(prepLcCode);
        response.setSampleLcCode(lcCode);
        response.setQty(qty);
        return response;
    }

}
