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
import com.telei.wms.datasource.wms.vo.RooHeaderResponseVo;
import com.telei.wms.datasource.wms.vo.RooLineResponseVo;
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
    public RooHeaderBusinessResponse addRooHeader(RooHeaderBusinessRequest request) {
        WmsRooHeader wmsRooHeader = DataConvertUtil.parseDataAsObject(request, WmsRooHeader.class);
        // 收货单id
        long rooId = idGenerator.getUnique();
        String userName = CustomRequestContext.getUserInfo().getUserName();
        Long companyId = CustomRequestContext.getUserInfo().getCompanyId();
        Long accountId = CustomRequestContext.getUserInfo().getAccountId();
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
        wmsRooHeader.setCreateUser(userName);
        wmsRooHeader.setCompanyId(companyId);
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
            productDetailRequest.setCompanyId(companyId);
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
                rooLine.setRecvUser(userName);
                rooLine.setCreateTime(DateUtils.nowWithUTC());
                rooLine.setCreateUser(userName);
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
                wmsIvAttributebatch.setSupplierId(wmsRooHeader.getSupplierId());
                wmsIvAttributebatch.setCreateUser(accountId);
                wmsIvAttributebatch.setCreateTime(DateUtils.nowWithUTC());
                // 根据产品id获取产品详情对象
                ProductDetailResponse productResponse = productDetailResponseMap.get(rooLine.getProductId());
                if (null != productResponse) {
                    wmsIvAttributebatch.setProductId(productResponse.getProductId());
                    wmsIvAttributebatch.setProductNo(productResponse.getProductNo());
                    wmsIvAttributebatch.setProductName(productResponse.getProductName());
                    wmsIvAttributebatch.setProductNameLocal(productResponse.getProductNameLocal());
                    wmsIvAttributebatch.setProductCategoryId(productResponse.getProductCategoryId());
                    wmsIvAttributebatch.setStockUnit(productResponse.getStockUnit());
                    wmsIvAttributebatch.setProductColor(productResponse.getProductColor());
                    wmsIvAttributebatch.setProductSize(productResponse.getProductSize());
                    wmsIvAttributebatch.setPrice(productResponse.getSellingPriceReference());
                    wmsIvAttributebatch.setVol(productResponse.getUnitVol());
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
        RooHeaderBusinessResponse response = new RooHeaderBusinessResponse();
        // 获取唯一锁标识
        String roHeadLockKey = String.valueOf(wmsRooHeader.getRoId());
        Object roHeadLockValue = tryLock(roHeadLockKey);
        // 组装入库任务数据
        WmsRoHeader wmsRoHeader = wmsRoHeaderService.selectByPrimaryKey(wmsRooHeader.getRoId());
        if (StringUtils.isNotNull(wmsRoHeader.getReceQty())) {
            wmsRoHeader.setReceQty(wmsRoHeader.getReceQty().add(receQty));
        } else {
            wmsRoHeader.setReceQty(receQty);
        }
        if (StringUtils.isNotNull(wmsRoHeader.getReceSpeciesQty())) {
            WmsRooLine wmsRooLine = new WmsRooLine();
            wmsRooLine.setRoId(wmsRooHeader.getRoId());
            List<WmsRooLine> rooLineList = wmsRooLineService.selectByEntity(wmsRooLine);
            if (StringUtils.isNotNull(rooLineList) && !rooLineList.isEmpty()) {
                List<Long> productIds = rooLineList.stream().map(WmsRooLine::getProductId).collect(Collectors.toList());
                productIds.addAll(categoryIds);
                // 产品id去重
                List<Long> list = productIds.stream().distinct().collect(Collectors.toList());
                wmsRoHeader.setReceSpeciesQty(list.size());
            } else {
                wmsRoHeader.setReceSpeciesQty(categoryIds.size());
            }
        } else {
            wmsRoHeader.setReceSpeciesQty(categoryIds.size());
        }
        wmsRoHeader.setRecvAllTime(DateUtils.nowWithUTC());
        // 组装入库任务明细数据
        List<WmsRoLine> wmsRoLineList = wmsRoLineService.selectByPrimaryKeys(rolIds);
        if (StringUtils.isNotNull(wmsRoLineList) && !wmsRoLineList.isEmpty()) {
            for (WmsRoLine wmsRoLine : wmsRoLineList) {
                BigDecimal rooReceQty = roLineMap.get(wmsRoLine.getId());
                if (StringUtils.isNotNull(wmsRoLine.getReceiptsQty())) {
                    wmsRoLine.setReceiptsQty(wmsRoLine.getReceiptsQty().add(rooReceQty));
                } else {
                    wmsRoLine.setReceiptsQty(rooReceQty);
                }
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
    public RooHeaderBusinessResponse revokeRooHeader(RooHeaderBusinessRequest request) {
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
        if (wmsRooLines.isEmpty() || wmsRooLines.size() != request.getRoolIds().size()) {
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
        RooHeaderBusinessResponse response = new RooHeaderBusinessResponse();
        response.setIsSuccess(Boolean.TRUE);
        return response;
    }

    /**
     * 订单详细
     *
     * @param request
     * @return
     */
    public RooHeaderBusinessResponse rooHeaderDetail(RooHeaderBusinessRequest request) {
        RooHeaderResponseVo rooHeaderResponseVo = wmsRooHeaderService.selectRooHeaderDetail(request.getId());
        if (Objects.isNull(rooHeaderResponseVo)) {
            ErrorCode.ROO_NOT_EXIST_4001.throwError();
        }
        RooHeaderBusinessResponse response = DataConvertUtil.parseDataAsObject(rooHeaderResponseVo, RooHeaderBusinessResponse.class);
        List<RooLineResponseVo> wmsRooLines = wmsRooLineService.findAll(request.getId());
        if (StringUtils.isNotNull(wmsRooLines) && !wmsRooLines.isEmpty()) {
            response.setRooLines(wmsRooLines);
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
        if (StringUtils.isNotNull(request.getRoStatus())) {
            conditionsBuilder.eq("roStatus", request.getRoStatus());
        }else{
            conditionsBuilder.ne("roStatus", 99);
        }
        if (StringUtils.isNotNull(request.getCompanyId())) {
            conditionsBuilder.eq("companyId", request.getCompanyId());
        } else {
            conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());
        }
        Map<String, Object> paramMap = conditionsBuilder.build();
        paramMap.put("orderBy", " wrh.roo_id DESC");
        Pagination page = (Pagination) wmsRooHeaderService.findAll(pagination, paramMap);
        RooHeaderBusinessPageQueryResponse response = new RooHeaderBusinessPageQueryResponse();
        response.setPage(page);
        return response;
    }

}
