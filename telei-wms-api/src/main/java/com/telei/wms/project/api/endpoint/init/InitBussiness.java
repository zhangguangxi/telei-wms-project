package com.telei.wms.project.api.endpoint.init;

import com.alibaba.fastjson.JSON;
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
import com.telei.wms.customer.product.ProductFeignClient;
import com.telei.wms.customer.product.dto.ProductDetailRequest;
import com.telei.wms.customer.product.dto.ProductDetailResponse;
import com.telei.wms.customer.product.dto.ProductListResponse;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.*;
import com.telei.wms.datasource.wms.vo.WmsInitLineVO;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.init.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 收货单
 * @Auther: gongrp
 * @Date: 2020/8/25 17:05
 */
@Service
public class InitBussiness {

    @Autowired
    private Id idGenerator;

    @Autowired
    private BusinessNumberFeignClient businessNumberFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private WmsInitHeaderService wmsInitHeaderService;

    @Autowired
    private WmsInitLineService wmsInitLineService;

    @Autowired
    private WmsIvAttributebatchService wmsIvAttributebatchService;

    @Autowired
    private WmsInventoryService wmsInventoryService;

    @Autowired
    private WmsInventoryHistoryService wmsInventoryHistoryService;

    /**
     * 新增
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InitHeaderCudBaseResponse addInitHeader(InitHeaderAddRequest request) {
        WmsInitHeader wmsInitHeader = DataConvertUtil.parseDataAsObject(request, WmsInitHeader.class);
        // 获取业务单号
        BusinessNumberRequest businessNumberRequest = new BusinessNumberRequest();
        businessNumberRequest.setType("WMS");
        ApiResponse apiResponse = businessNumberFeignClient.get(businessNumberRequest);
        BusinessNumberResponse businessNumberResponse = apiResponse.convertDataToObject(BusinessNumberResponse.class);
        if (StringUtils.isEmpty(businessNumberResponse.getBusinessNumber())) {
            // 未获取到业务单号
            ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
        }
        // 库存初始化单头
        long ivihId = idGenerator.getUnique();
        wmsInitHeader.setId(ivihId);
        wmsInitHeader.setIvihCode(businessNumberResponse.getBusinessNumber());
        wmsInitHeader.setIvihStatus("01");
        wmsInitHeader.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        wmsInitHeader.setCreateUser(CustomRequestContext.getUserInfo().getUserName());
        wmsInitHeader.setCreateTime(DateUtils.nowWithUTC());
        List<WmsInitLine> wmsInitLines = DataConvertUtil.parseDataAsArray(request.getInitLines(), WmsInitLine.class);
        if (!wmsInitLines.isEmpty()) {
            for (WmsInitLine initLine : wmsInitLines) {
                // 库存初始化明细
                initLine.setId(idGenerator.getUnique());
                initLine.setIvihId(ivihId);
            }
        }
        InitHeaderCudBaseResponse response = new InitHeaderCudBaseResponse();
        int initHeaderCount = wmsInitHeaderService.insert(wmsInitHeader);
        if (initHeaderCount <= 0) {
            ErrorCode.INIT_ADD_ERROR_4002.throwError();
        }
        int initLineCount = wmsInitLineService.insertBatch(wmsInitLines);
        if (initLineCount <= 0) {
            ErrorCode.INIT_ADD_ERROR_4002.throwError();
        }
        response.setIsSuccess(Boolean.TRUE);
        return response;
    }

    /**
     * 审核初始化库存
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InitHeaderCudBaseResponse auditInitHeader(InitHeaderAuditRequest request) {
        WmsInitHeader wmsInitHeader = wmsInitHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsInitHeader)) {
            ErrorCode.INIT_NOT_EXIST_4001.throwError();
        }
        wmsInitHeader.setIvihStatus("20");
        wmsInitHeader.setAuditUser(CustomRequestContext.getUserInfo().getUserName());
        wmsInitHeader.setAuditTime(DateUtils.nowWithUTC());

        WmsInitLine wmsInitLine = new WmsInitLine();
        wmsInitLine.setIvihId(request.getId());
        List<WmsInitLine> initLineList = wmsInitLineService.selectByEntity(wmsInitLine);
        List<WmsInventory> wmsInventoryList = new ArrayList<>();
        List<WmsInventoryHistory> wmsInventoryHistoryList = new ArrayList<>();
        List<WmsIvAttributebatch> wmsIvAttributebatchList = new ArrayList<>();
        if (!initLineList.isEmpty()) {
            List<Long> productIds = initLineList.stream().map(WmsInitLine::getProductId).collect(Collectors.toList());
            // 根据产品id列表获取产品列表信息
            ProductDetailRequest productDetailRequest = new ProductDetailRequest();
            productDetailRequest.setIds(productIds);
            productDetailRequest.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
            ApiResponse detailResponse = productFeignClient.selectProductList(productDetailRequest);
            ProductListResponse response = JSON.parseObject(JSON.toJSONString(detailResponse.getData()), ProductListResponse.class);
            Map<Long, ProductDetailResponse> productDetailResponseMap = new HashMap<>();
            if (StringUtils.isNotNull(response)) {
                productDetailResponseMap = response.getProductList().stream().collect(Collectors.toMap(ProductDetailResponse::getProductId, Function.identity()));
            }
            for (WmsInitLine initLine : initLineList) {
                long iabId = idGenerator.getUnique();
                // 组装库存批次表
                WmsIvAttributebatch wmsIvAttributebatch = DataConvertUtil.parseDataAsObject(request, WmsIvAttributebatch.class);
                wmsIvAttributebatch.setId(iabId);
                wmsIvAttributebatch.setWarehouseId(wmsInitHeader.getWarehouseId());
                wmsIvAttributebatch.setWarehouseCode(wmsInitHeader.getWarehouseCode());
                // 获取业务单号
                BusinessNumberRequest businessNumberRequest = new BusinessNumberRequest();
                businessNumberRequest.setType("WMS");
                ApiResponse apiResponse = businessNumberFeignClient.get(businessNumberRequest);
                BusinessNumberResponse businessNumberResponse = apiResponse.convertDataToObject(BusinessNumberResponse.class);
                if (StringUtils.isEmpty(businessNumberResponse.getBusinessNumber())) {
                    // 未获取到业务单号
                    ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
                }
                wmsIvAttributebatch.setQsCode("GD");
                wmsIvAttributebatch.setApCode("ADJT");
                wmsIvAttributebatch.setDocumentCode(businessNumberResponse.getBusinessNumber());
                wmsIvAttributebatch.setIabDocumentId(request.getId());
                wmsIvAttributebatch.setIabDocumentlineId(initLine.getId());
                wmsIvAttributebatch.setCompanyId(wmsInitHeader.getCompanyId());
                wmsIvAttributebatch.setCreateUser(CustomRequestContext.getUserInfo().getAccountId());
                wmsIvAttributebatch.setCreateTime(DateUtils.nowWithUTC());
                // 根据产品id获取产品详情对象
                ProductDetailResponse productResponse = productDetailResponseMap.get(initLine.getProductId());
                if (null != productResponse) {
                    wmsIvAttributebatch.setProductName(productResponse.getProductName());
                    wmsIvAttributebatch.setProductNameLocal(productResponse.getProductNameLocal());
                    wmsIvAttributebatch.setProductCategoryId(productResponse.getProductCategoryId());
                    wmsIvAttributebatch.setStockUnit(productResponse.getStockUnit());
                    wmsIvAttributebatch.setProductColor(productResponse.getProductColor());
                    wmsIvAttributebatch.setProductSize(productResponse.getProductSize());
                    wmsIvAttributebatch.setPrice(productResponse.getSellingPriceReference());
                    wmsIvAttributebatch.setTexture(productResponse.getTexture());
                    wmsIvAttributebatch.setImagePath(productResponse.getImagePath());
                    wmsIvAttributebatch.setMidBagBarcode(productResponse.getMidBagBarcode());
                    wmsIvAttributebatch.setMidBagHeight(productResponse.getMidBagHeight());
                    wmsIvAttributebatch.setMidBagLength(productResponse.getMidBagLength());
                    wmsIvAttributebatch.setMidBagRate(productResponse.getMidBagQty());
                    wmsIvAttributebatch.setMidBagVol(productResponse.getMidBagVol());
                    wmsIvAttributebatch.setMidBagWeight(productResponse.getMidBagWeight());
                    wmsIvAttributebatch.setMidBagWidth(productResponse.getMidBagWidth());
                    wmsIvAttributebatch.setBigBagBarcode(productResponse.getBigBagBarcode());
                    wmsIvAttributebatch.setBigBagHeight(productResponse.getBigBagHeight());
                    wmsIvAttributebatch.setBigBagLength(productResponse.getBigBagLength());
                    wmsIvAttributebatch.setBigBagRate(productResponse.getBigBagQty());
                    wmsIvAttributebatch.setBigBagVol(productResponse.getBigBagVol());
                    wmsIvAttributebatch.setBigBagWeight(productResponse.getBigBagWeight());
                    wmsIvAttributebatch.setBigBagWidth(productResponse.getBigBagWidth());
                    // 组装库存表
                    WmsInventory wmsInventory = new WmsInventory();
                    wmsInventory.setId(idGenerator.getUnique());
                    wmsInventory.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
                    wmsInventory.setWarehouseId(wmsInitHeader.getWarehouseId());
                    wmsInventory.setWarehouseCode(wmsInitHeader.getWarehouseCode());
                    wmsInventory.setLcCode(initLine.getLcCode());
                    wmsInventory.setProductId(initLine.getProductId());
                    wmsInventory.setIabId(iabId);
                    wmsInventory.setQsCode("GD");
                    wmsInventory.setIvFifoTime(initLine.getIvFifoTime());
                    wmsInventory.setIvQty(initLine.getIvQty());
                    wmsInventory.setStockUnit(productResponse.getStockUnit());
                    // 中包转换运算
                    BigDecimal[] mid = initLine.getIvQty().divideAndRemainder(new BigDecimal(productResponse.getMidBagQty()));
                    wmsInventory.setMidBagQty(mid[0]);
                    wmsInventory.setMidBagRate(productResponse.getMidBagQty());
                    wmsInventory.setMidBagExtraQty(mid[1]);
                    // 大包转换运算
                    BigDecimal[] big = initLine.getIvQty().divideAndRemainder(new BigDecimal(productResponse.getBigBagQty()));
                    wmsInventory.setBigBagQty(big[0]);
                    wmsInventory.setBigBagRate(productResponse.getBigBagQty());
                    wmsInventory.setBigBagExtraQty(big[1]);
                    wmsInventory.setIvLocksign(0);
                    wmsInventory.setIvFreezesign(0);
                    wmsInventory.setBizDate(DateUtils.nowWithUTC());
                    wmsInventory.setIvCreatetime(DateUtils.nowWithUTC());
                    wmsInventory.setIvTranstime(DateUtils.nowWithUTC());
                    wmsInventory.setApCodeDc("ADJT");
                    wmsInventory.setIvDocumentCode(wmsInitHeader.getIvihCode());
                    wmsInventory.setIvDocumentId(wmsInitHeader.getId());
                    wmsInventory.setIvDocumentlineId(initLine.getId());
                    wmsInventory.setProductDate(initLine.getProductDate());
                    wmsInventory.setIvEffectiveDate(initLine.getIvEffectiveDate());
                    wmsInventory.setBatchNo(initLine.getBatchNo());
                    wmsInventoryList.add(wmsInventory);
                    // 组装库存历史表
                    WmsInventoryHistory wmsInventoryHistory = DataConvertUtil.parseDataAsObject(wmsInventory, WmsInventoryHistory.class);
                    wmsInventoryHistoryList.add(wmsInventoryHistory);
                }
                wmsIvAttributebatchList.add(wmsIvAttributebatch);
            }
        }
        int count = wmsInitHeaderService.updateByPrimaryKeySelective(wmsInitHeader);
        if (count <= 0) {
            ErrorCode.INIT_AUDIT_ERROR_4003.throwError();
        }
        if (!wmsInventoryList.isEmpty()) {
            int ibaCount = wmsInventoryService.insertBatch(wmsInventoryList);
            if (ibaCount <= 0) {
                ErrorCode.INIT_AUDIT_ERROR_4003.throwError();
            }
        }
        if (!wmsInventoryHistoryList.isEmpty()) {
            int ibaCount = wmsInventoryHistoryService.insertBatch(wmsInventoryHistoryList);
            if (ibaCount <= 0) {
                ErrorCode.INIT_AUDIT_ERROR_4003.throwError();
            }
        }
        if (!wmsIvAttributebatchList.isEmpty()) {
            int ibaCount = wmsIvAttributebatchService.insertBatch(wmsIvAttributebatchList);
            if (ibaCount <= 0) {
                ErrorCode.INIT_AUDIT_ERROR_4003.throwError();
            }
        }
        InitHeaderCudBaseResponse response = new InitHeaderCudBaseResponse();
        response.setIsSuccess(Boolean.TRUE);
        return response;
    }

    /**
     * 订单详细
     *
     * @param request
     * @return
     */
    public InitHeaderDetailResponse initHeaderDetail(InitHeaderDetailRequest request) {
        WmsInitHeader wmsInitHeader = wmsInitHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsInitHeader)) {
            ErrorCode.INIT_NOT_EXIST_4001.throwError();
        }
        InitHeaderDetailResponse response = DataConvertUtil.parseDataAsObject(wmsInitHeader, InitHeaderDetailResponse.class);
        WmsInitLine wmsInitLine = new WmsInitLine();
        wmsInitLine.setIvihId(request.getId());
        // 应展示商品字段
        List<WmsInitLineVO> wmsInitLineList = wmsInitLineService.selectInitLinesByEntity(wmsInitLine);
        if (!wmsInitLineList.isEmpty()) {
            response.setInitLines(wmsInitLineList);
        }
        return response;
    }

    /**
     * 分页查询主单
     *
     * @param request
     * @return
     */
    public InitHeaderBusinessPageQueryResponse initHeaderPageQuery(InitHeaderBusinessPageQueryRequest request) {
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (StringUtils.isNotBlank(request.getStartTime()) && StringUtils.isNotBlank(request.getEndTime())) {
            conditionsBuilder.between("createTime", request.getStartTime(), request.getEndTime());
        }
        if (StringUtils.isNotBlank(request.getIvihCode())) {
            conditionsBuilder.like("ivihCode", request.getIvihCode());
        }
        if (StringUtils.isNotBlank(request.getCreateUser())) {
            conditionsBuilder.like("createUser", request.getCreateUser());
        }
        if (StringUtils.isNotBlank(request.getIvihStatus())) {
            conditionsBuilder.eq("ivihStatus", request.getIvihStatus());
        }
        if (StringUtils.isNotNull(request.getWarehouseId())) {
            conditionsBuilder.eq("warehouseId", request.getWarehouseId());
        }
        if (StringUtils.isNotBlank(request.getWarehouseCode())) {
            conditionsBuilder.like("warehouseCode", request.getWarehouseCode());
        }
        Map<String, Object> paramMap = conditionsBuilder.build();
        Pagination page = (Pagination) wmsInitHeaderService.selectPage(pagination, paramMap);
        InitHeaderBusinessPageQueryResponse response = new InitHeaderBusinessPageQueryResponse();
        response.setPage(page);
        return response;
    }

}
