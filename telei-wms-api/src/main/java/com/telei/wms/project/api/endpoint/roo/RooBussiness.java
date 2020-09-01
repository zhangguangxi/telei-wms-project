package com.telei.wms.project.api.endpoint.roo;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.utils.DateUtils;
import com.telei.infrastructure.component.commons.utils.LockMapUtil;
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
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.roo.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.telei.infrastructure.component.commons.utils.LockMapUtil.*;

/**
 * @Description: 收货单
 * @Auther: gongrp
 * @Date: 2020/8/25 17:05
 */
@Service
public class RooBussiness {

    @Autowired
    private Id idGenerator;

    @Autowired
    private BusinessNumberFeignClient businessNumberFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private WmsRooHeaderService wmsRooHeaderService;

    @Autowired
    private WmsRooLineService wmsRooLineService;

    @Autowired
    private WmsRoHeaderService wmsRoHeaderService;

    @Autowired
    private WmsRoLineService wmsRoLineService;

    @Autowired
    private WmsIvAttributebatchService wmsIvAttributebatchService;

    /**
     * 新增
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public RooHeaderCudBaseResponse addRooHeader(RooHeaderAddRequest request) {
        WmsRooHeader wmsRooHeader = DataConvertUtil.parseDataAsObject(request, WmsRooHeader.class);
        // 收货单id
        long rooId = idGenerator.getUnique();
        // 获取业务单号
        BusinessNumberRequest businessNumberRequest = new BusinessNumberRequest();
        businessNumberRequest.setType("WMS");
        ApiResponse apiResponse = businessNumberFeignClient.get(businessNumberRequest);
        BusinessNumberResponse businessNumberResponse = apiResponse.convertDataToObject(BusinessNumberResponse.class);
        if (StringUtils.isEmpty(businessNumberResponse.getBusinessNumber())) {
            // 未获取到业务单号
            ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
        }
        // 收货单
        wmsRooHeader.setId(rooId);
        wmsRooHeader.setRooCode(businessNumberResponse.getBusinessNumber());
        wmsRooHeader.setOrderType("20");
        wmsRooHeader.setRoStatus("20");
        wmsRooHeader.setCreateUser(CustomRequestContext.getUserInfo().getUserName());
        wmsRooHeader.setCreateTime(DateUtils.nowWithUTC());
        BigDecimal receQty = BigDecimal.ZERO;
        List<Long> categoryIds = new ArrayList<>();
        List<WmsRooLine> wmsRooLines = DataConvertUtil.parseDataAsArray(request.getRooLines(), WmsRooLine.class);
        List<WmsIvAttributebatch> wmsIvAttributebatchList = new ArrayList<>();
        List<Long> rolIds = new ArrayList<>();
        Map<Long, BigDecimal> roLineMap = new HashMap<>();
        if (!wmsRooLines.isEmpty()) {
            roLineMap = wmsRooLines.stream().collect(Collectors.toMap(WmsRooLine::getRolId, WmsRooLine::getReceQty));
            List<Long> productIds = wmsRooLines.stream().map(WmsRooLine::getProductId).collect(Collectors.toList());
            //根据产品id列表获取产品列表信息
            ProductDetailRequest productDetailRequest = new ProductDetailRequest();
            productDetailRequest.setIds(productIds);
            productDetailRequest.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
            ApiResponse detailResponse = productFeignClient.selectProductList(productDetailRequest);
            ProductListResponse response = JSON.parseObject(JSON.toJSONString(detailResponse.getData()), ProductListResponse.class);
            Map<Long, ProductDetailResponse> productDetailResponseMap = new HashMap<>();
            if (StringUtils.isNotNull(response)) {
                categoryIds = response.getProductList().stream().map(ProductDetailResponse::getProductCategoryId).collect(Collectors.toList());
                productDetailResponseMap = response.getProductList().stream().collect(Collectors.toMap(ProductDetailResponse::getProductId, Function.identity()));
            }
            for (WmsRooLine rooLine : wmsRooLines) {
                rolIds.add(rooLine.getRolId());
                // 库存批次id
                long iabId = idGenerator.getUnique();
                // 收货单明细
                rooLine.setId(idGenerator.getUnique());
                rooLine.setRooId(rooId);
                rooLine.setRoolStatus("20");
                rooLine.setRecvTime(DateUtils.nowWithUTC());
                rooLine.setRecvUser(CustomRequestContext.getUserInfo().getUserName());
                rooLine.setCreateTime(DateUtils.nowWithUTC());
                rooLine.setCreateUser(CustomRequestContext.getUserInfo().getUserName());
                rooLine.setIabId(iabId);
                receQty = receQty.add(rooLine.getReceQty());
                // 组装库存批次表
                WmsIvAttributebatch wmsIvAttributebatch = DataConvertUtil.parseDataAsObject(request, WmsIvAttributebatch.class);
                wmsIvAttributebatch.setId(iabId);
                wmsIvAttributebatch.setWarehouseId(wmsRooHeader.getWarehouseId());
                wmsIvAttributebatch.setWarehouseCode(wmsRooHeader.getWarehouseCode());
                apiResponse = businessNumberFeignClient.get(businessNumberRequest);
                businessNumberResponse = apiResponse.convertDataToObject(BusinessNumberResponse.class);
                if (StringUtils.isEmpty(businessNumberResponse.getBusinessNumber())) {
                    // 未获取到业务单号
                    ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
                }
                wmsIvAttributebatch.setQsCode("GD");
                wmsIvAttributebatch.setApCode("RECV");
                wmsIvAttributebatch.setDocumentCode(businessNumberResponse.getBusinessNumber());
                wmsIvAttributebatch.setIabDocumentId(rooLine.getRoId());
                wmsIvAttributebatch.setIabDocumentlineId(rooLine.getRolId());
                wmsIvAttributebatch.setCompanyId(wmsRooHeader.getCompanyId());
                wmsIvAttributebatch.setCreateUser(CustomRequestContext.getUserInfo().getAccountId());
                wmsIvAttributebatch.setCreateTime(DateUtils.nowWithUTC());
                // 根据产品id获取产品详情对象
                ProductDetailResponse productResponse = productDetailResponseMap.get(rooLine.getProductId());
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
                }
                wmsIvAttributebatchList.add(wmsIvAttributebatch);
            }
        }
        wmsRooHeader.setTotalQty(receQty);
        RooHeaderCudBaseResponse response = new RooHeaderCudBaseResponse();
        // 获取唯一锁标识
        String roHeadLockKey = String.valueOf(wmsRooHeader.getRoId());
        Object roHeadLockValue = tryLock(roHeadLockKey);
        // 组装入库任务数据
        WmsRoHeader wmsRoHeader = wmsRoHeaderService.selectByPrimaryKey(wmsRooHeader.getRoId());
        wmsRoHeader.setReceQty(wmsRoHeader.getReceQty().add(receQty));
        wmsRoHeader.setReceSpeciesQty(wmsRoHeader.getReceSpeciesQty() + categoryIds.size());
        wmsRoHeader.setRecvAllTime(DateUtils.nowWithUTC());
        // 组装入库任务明细数据
        List<WmsRoLine> wmsRoLineList = wmsRoLineService.selectByPrimaryKeys(rolIds);
        if (StringUtils.isNotEmpty(wmsRoLineList)) {
            for (WmsRoLine wmsRoLine : wmsRoLineList) {
                BigDecimal rooReceQty = roLineMap.get(wmsRoLine.getId());
                wmsRoLine.setReceiptsQty(wmsRoLine.getReceiptsQty().add(rooReceQty));
            }
        }
        if (confirmLock(roHeadLockKey, roHeadLockValue)) {
            // 回写入库任务
            int rooHeaderCount = wmsRooHeaderService.insert(wmsRooHeader);
            if (rooHeaderCount <= 0) {
                ErrorCode.ROO_ADD_ERROR_4002.throwError();
            }
            int rooLineCount = wmsRooLineService.insertBatch(wmsRooLines);
            if (rooLineCount <= 0) {
                ErrorCode.ROO_ADD_ERROR_4002.throwError();
            }
            if (!wmsIvAttributebatchList.isEmpty()) {
                int ibaCount = wmsIvAttributebatchService.insertBatch(wmsIvAttributebatchList);
                if (ibaCount <= 0) {
                    ErrorCode.ROO_ADD_ERROR_4002.throwError();
                }
            }
            int roHeaderCount = wmsRoHeaderService.updateByPrimaryKeySelective(wmsRoHeader);
            if (roHeaderCount <= 0) {
                ErrorCode.ROO_ADD_ERROR_4002.throwError();
            }
            if (!wmsRoLineList.isEmpty()) {
                int roLineCount = wmsRoLineService.updateBatch(wmsRoLineList);
                if (roLineCount <= 0) {
                    ErrorCode.ROO_ADD_ERROR_4002.throwError();
                }
            }
            cancelLock(roHeadLockKey, roHeadLockValue);
        } else {
            ErrorCode.ROO_ADD_ERROR_4002.throwError();
        }
        return response;
    }

    /**
     * 撤销收货单
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public RooHeaderCudBaseResponse revokeRooHeader(RooHeaderCancelRequest request) {
        WmsRooHeader wmsRooHeader = wmsRooHeaderService.selectByPrimaryKey(request.getRooId());
        if (Objects.isNull(wmsRooHeader)) {
            ErrorCode.ROO_NOT_EXIST_4001.throwError();
        }
        Long roId = wmsRooHeader.getRoId();
        // 获取唯一锁标识
        Object lockValue = tryLock(Arrays.asList(request.getRooId(), roId));
        // 组装收货单
        wmsRooHeader.setRoStatus("99");
        // 组装收货单明细
        List<WmsRooLine> wmsRooLines = wmsRooLineService.selectByPrimaryKeys(request.getRoolIds());
        if (!wmsRooLines.isEmpty() && wmsRooLines.size() == request.getRoolIds().size()) {
            ErrorCode.ROO_LINE_NOT_EXIST_4004.throwError();
        }
        BigDecimal receQty = BigDecimal.ZERO;
        List<Long> categoryIds = new ArrayList<>();
        List<Long> rolIds = new ArrayList<>();
        Map<Long, BigDecimal> roLineMap = new HashMap<>();
        if (!wmsRooLines.isEmpty()) {
            roLineMap = wmsRooLines.stream().collect(Collectors.toMap(WmsRooLine::getRolId, WmsRooLine::getReceQty));
            List<Long> productIds = wmsRooLines.stream().map(WmsRooLine::getProductId).collect(Collectors.toList());
            //根据产品id列表获取产品列表信息
            ProductDetailRequest productDetailRequest = new ProductDetailRequest();
            productDetailRequest.setIds(productIds);
            productDetailRequest.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
            ApiResponse detailResponse = productFeignClient.selectProductList(productDetailRequest);
            ProductListResponse response = JSON.parseObject(JSON.toJSONString(detailResponse.getData()), ProductListResponse.class);
            if (StringUtils.isNotNull(response)) {
                categoryIds = response.getProductList().stream().map(ProductDetailResponse::getProductCategoryId).collect(Collectors.toList());
            }
            for (WmsRooLine wmsRooLine : wmsRooLines) {
                wmsRooLine.setRoolStatus("99");
                rolIds.add(wmsRooLine.getRolId());
                receQty = receQty.add(wmsRooLine.getReceQty());
            }
        }
        // 组装入库任务数据
        WmsRoHeader wmsRoHeader = wmsRoHeaderService.selectByPrimaryKey(wmsRooHeader.getRoId());
        wmsRoHeader.setReceQty(wmsRoHeader.getReceQty().subtract(receQty));
        wmsRoHeader.setReceSpeciesQty(wmsRoHeader.getReceSpeciesQty() - categoryIds.size());
        // 组装入库任务明细数据
        List<WmsRoLine> wmsRoLineList = wmsRoLineService.selectByPrimaryKeys(rolIds);
        if (StringUtils.isNotEmpty(wmsRoLineList)) {
            for (WmsRoLine wmsRoLine : wmsRoLineList) {
                BigDecimal rooReceQty = roLineMap.get(wmsRoLine.getId());
                wmsRoLine.setReceiptsQty(wmsRoLine.getReceiptsQty().subtract(rooReceQty));
            }
        }
        if (LockMapUtil.confirmLock(Arrays.asList(request.getRooId(), roId), lockValue)) {
            int rooHeaderCount = wmsRooHeaderService.updateByPrimaryKeySelective(wmsRooHeader);
            if (rooHeaderCount <= 0) {
                ErrorCode.ROO_REVOKE_ERROR_4003.throwError();
            }
            if (!wmsRooLines.isEmpty()) {
                int rooLineCount = wmsRooLineService.updateBatch(wmsRooLines);
                if (rooLineCount <= 0) {
                    ErrorCode.ROO_REVOKE_ERROR_4003.throwError();
                }
            }
            int roHeaderCount = wmsRoHeaderService.updateByPrimaryKeySelective(wmsRoHeader);
            if (roHeaderCount <= 0) {
                ErrorCode.ROO_REVOKE_ERROR_4003.throwError();
            }
            if (!wmsRoLineList.isEmpty()) {
                int roLineCount = wmsRoLineService.updateBatch(wmsRoLineList);
                if (roLineCount <= 0) {
                    ErrorCode.ROO_REVOKE_ERROR_4003.throwError();
                }
            }
            LockMapUtil.cancelLock(Arrays.asList(request.getRooId(), roId), lockValue);
        } else {
            ErrorCode.ROO_REVOKE_ERROR_4003.throwError();
        }
        RooHeaderCudBaseResponse response = new RooHeaderCudBaseResponse();
        response.setIsSuccess(Boolean.TRUE);
        return response;
    }

    /**
     * 订单详细
     *
     * @param request
     * @return
     */
    public RooHeaderDetailResponse rooHeaderDetail(RooHeaderDetailRequest request) {
        WmsRooHeader wmsRooHeader = wmsRooHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsRooHeader)) {
            ErrorCode.ROO_NOT_EXIST_4001.throwError();
        }
        RooHeaderDetailResponse response = DataConvertUtil.parseDataAsObject(wmsRooHeader, RooHeaderDetailResponse.class);
        WmsRooLine wmsRooLine = new WmsRooLine();
        wmsRooLine.setRooId(request.getId());
        List<WmsRooLine> wmsRooLines = wmsRooLineService.selectByEntity(wmsRooLine);
        if (!wmsRooLines.isEmpty()) {
            List<RooLineDetailResponse> rooLineDetailResponses = DataConvertUtil.parseDataAsArray(wmsRooLines, RooLineDetailResponse.class);
            response.setRoLines(rooLineDetailResponses);
        }
        return response;
    }

    /**
     * 分页查询主单
     *
     * @param request
     * @return
     */
    public RooHeaderBusinessPageQueryResponse rooHeaderPageQuery(RooHeaderBusinessPageQueryRequest request) {
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (null != request.getStartTime() && null != request.getEndTime()) {
            conditionsBuilder.between("createTime", request.getStartTime(), request.getEndTime());
        }
        if (StringUtils.isNotNull(request.getOwnerUser())) {
            conditionsBuilder.eq("ownerUser", request.getOwnerUser());
        }
        if (StringUtils.isNotNull(request.getRoCode())) {
            conditionsBuilder.eq("roCode", request.getRoCode());
        }
        if (StringUtils.isNotNull(request.getRooCode())) {
            conditionsBuilder.eq("rooCode", request.getRooCode());
        }
        if(StringUtils.isNotNull(request.getRoStatus())){
            conditionsBuilder.eq("roStatus", request.getRoStatus());
        }
        Map<String, Object> paramMap = conditionsBuilder.build();
        Pagination page = (Pagination) wmsRooHeaderService.selectPage(pagination, paramMap);
        RooHeaderBusinessPageQueryResponse response = new RooHeaderBusinessPageQueryResponse();
        response.setPage(page);
        return response;
    }

}
