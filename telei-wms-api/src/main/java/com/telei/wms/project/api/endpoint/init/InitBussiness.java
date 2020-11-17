package com.telei.wms.project.api.endpoint.init;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.utils.DateUtils;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.ExcelUtil;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.amqp.inventoryInitWriteBack.OmsInventoryInitWriteBack;
import com.telei.wms.customer.businessNumber.BusinessNumberFeignClient;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberRequest;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberResponse;
import com.telei.wms.customer.product.ProductFeignClient;
import com.telei.wms.customer.product.dto.ProductDetailRequest;
import com.telei.wms.customer.product.dto.ProductDetailResponse;
import com.telei.wms.customer.product.dto.ProductListResponse;
import com.telei.wms.customer.supplier.SupplierFeignClient;
import com.telei.wms.customer.supplier.dto.SupplierListResponse;
import com.telei.wms.customer.supplier.dto.SupplierRequest;
import com.telei.wms.customer.supplier.dto.SupplierResponse;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.*;
import com.telei.wms.datasource.wms.vo.WmsInitLineVO;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.amqp.producer.WmsOmsInventoryInitWriteBackProducer;
import com.telei.wms.project.api.endpoint.init.dto.*;
import com.telei.wms.project.api.endpoint.inventory.InventoryBussiness;
import com.telei.wms.project.api.endpoint.lcRecommend.LcRecommendBussiness;
import com.telei.wms.project.api.endpoint.lcRecommend.dto.LcRecommendDeleteRequest;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.WmsIdInstantdirectiveBussiness;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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
    private InventoryBussiness inventoryBussiness;

    @Autowired
    private Id idGenerator;

    @Autowired
    private BusinessNumberFeignClient businessNumberFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private SupplierFeignClient supplierFeignClient;

    @Autowired
    private WmsInitHeaderService wmsInitHeaderService;

    @Autowired
    private WmsInitLineService wmsInitLineService;

    @Autowired
    private WmsLocationService wmsLocationService;

    @Autowired
    private WmsIvAttributebatchService wmsIvAttributebatchService;

    @Autowired
    private WmsIvTransactionService wmsIvTransactionService;

    @Autowired
    private WmsInventoryService wmsInventoryService;

    @Autowired
    private WmsInventoryHistoryService wmsInventoryHistoryService;

    @Autowired
    private LcRecommendBussiness lcRecommendBussiness;

    @Autowired
    private WmsOmsInventoryInitWriteBackProducer wmsOmsInventoryInitWriteBackProducer;

    @Autowired
    private WmsIdInstantdirectiveBussiness wmsIdInstantdirectiveBussiness;

    /**
     * 新增&修改
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InitHeaderCudBaseResponse addInitHeader(InitHeaderAddRequest request) {
        WmsInitHeader wmsInitHeader = DataConvertUtil.parseDataAsObject(request, WmsInitHeader.class);
        long ivihId = -1;
        if (Objects.isNull(wmsInitHeader.getId())) {
            // 获取业务单号
            BusinessNumberRequest businessNumberRequest = new BusinessNumberRequest();
            businessNumberRequest.setType("KCS");
            ApiResponse apiResponse = businessNumberFeignClient.get(businessNumberRequest);
            BusinessNumberResponse businessNumberResponse = apiResponse.convertDataToObject(BusinessNumberResponse.class);
            if (StringUtils.isEmpty(businessNumberResponse.getBusinessNumber())) {
                // 未获取到业务单号
                ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
            }
            // 库存初始化单头
            ivihId = idGenerator.getUnique();
            wmsInitHeader.setId(ivihId);
            wmsInitHeader.setIvihCode(businessNumberResponse.getBusinessNumber());
            wmsInitHeader.setIvihStatus("01");
            wmsInitHeader.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
            wmsInitHeader.setCreateUser(CustomRequestContext.getUserInfo().getEmployeeName());
            wmsInitHeader.setCreateTime(DateUtils.nowWithUTC());
            int initHeaderCount = wmsInitHeaderService.insert(wmsInitHeader);
            if (initHeaderCount <= 0) {
                ErrorCode.INIT_ADD_ERROR_4002.throwError();
            }
        } else {
            ivihId = wmsInitHeader.getId();
            WmsInitHeader initHeader = wmsInitHeaderService.selectByPrimaryKey(ivihId);
            if (Objects.isNull(initHeader)) {
                //订单不存在
                ErrorCode.INIT_NOT_EXIST_4001.throwError();
            }
            initHeader.setMemo(wmsInitHeader.getMemo());
            wmsInitHeaderService.updateByPrimaryKeySelective(initHeader);
            // 删除库存初始化明细
            WmsInitLine wmsInitLine = new WmsInitLine();
            wmsInitLine.setIvihId(ivihId);
            List<WmsInitLine> wmsInitLines = wmsInitLineService.selectByEntity(wmsInitLine);
            if (StringUtils.isNotNull(wmsInitLines) && !wmsInitLines.isEmpty()) {
                List<Long> ivilIds = wmsInitLines.stream().map(WmsInitLine::getId).collect(Collectors.toList());
                wmsInitLineService.deleteByPrimaryKeys(ivilIds);
            }
        }
        if (StringUtils.isNotNull(request.getInitLines()) && !request.getInitLines().isEmpty()) {
            // 通过产品barcode查询产品列表
            Map<String, ProductDetailResponse> productMap = new HashMap<>();
            List<String> productBarcodeList = request.getInitLines().stream().map(InitLineAddRequest::getProductBarcode).collect(Collectors.toList());
            if (!productBarcodeList.isEmpty()) {
                ProductDetailRequest productDetailRequest = new ProductDetailRequest();
                productDetailRequest.setProductBarcodes(productBarcodeList);
                productDetailRequest.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
                try {
                    ApiResponse productResponse = productFeignClient.getProductListByBarCode(productDetailRequest);
                    ProductListResponse response = JSON.parseObject(JSON.toJSONString(productResponse.getData()), ProductListResponse.class);
                    if (StringUtils.isNotNull(response)) {
                        productMap = response.getProductList().stream().collect(Collectors.toMap(ProductDetailResponse::getProductBarcode, Function.identity()));
                    }
                } catch (Exception e) {
                    ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
                }
            }
            for (InitLineAddRequest initLine : request.getInitLines()) {
                // 库存初始化明细
                initLine.setId(idGenerator.getUnique());
                ProductDetailResponse productDetailResponse = productMap.get(initLine.getProductBarcode());
                if (Objects.isNull(productDetailResponse)) {
                    ErrorCode.INIT_ADD_ERROR_4002.throwError();
                }
                initLine.setProductId(productDetailResponse.getProductId());
                initLine.setIvihId(ivihId);
                // 判断货位是否存在
                WmsLocation wmsLocation = wmsLocationService.getCompanyLcCodeByLocation(initLine.getLcCode(), wmsInitHeader.getWarehouseId());
                if (Objects.isNull(wmsLocation)) {
                    ErrorCode.WMS_LOCATION_NOT_EXIST_4002.throwError();
                }
                // 判断当前货位是否有其他商品
                WmsInventory wmsInventory = new WmsInventory();
                wmsInventory.setLcCode(initLine.getLcCode());
                wmsInventory.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
                wmsInventory.setWarehouseId(wmsInitHeader.getWarehouseId());
                List<WmsInventory> inventoryList = wmsInventoryService.selectByEntity(wmsInventory);
                if (inventoryList != null) {
                    for (WmsInventory inventory : inventoryList) {
                        if (!inventory.getProductId().equals(productDetailResponse.getProductId())){
                            ErrorCode.WMS_LOCATION_EXIST_PRODUCT_4008.throwError(initLine.getLcCode());
                        }
                    }
                }
                // 判断当前商品是否存在其他库位
                WmsInventory wmsInventory1 = new WmsInventory();
                wmsInventory1.setProductId(productDetailResponse.getProductId());
                wmsInventory1.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
                wmsInventory1.setWarehouseId(wmsInitHeader.getWarehouseId());
                List<WmsInventory> wmsInventoryList = wmsInventoryService.selectByEntity(wmsInventory1);
                if (wmsInventoryList != null) {
                    for (WmsInventory inventory1 : wmsInventoryList) {
                        if (!inventory1.getLcCode().equals(initLine.getLcCode())){
                            ErrorCode.WMS_LOCATION_EXIST_PRODUCT_BY_LOCODE_4009.throwError(inventory1.getLcCode());
                        }
                    }
                }
            }
            List<WmsInitLine> wmsInitLines = DataConvertUtil.parseDataAsArray(request.getInitLines(), WmsInitLine.class);
            int initLineCount = wmsInitLineService.insertBatch(wmsInitLines);
            if (initLineCount <= 0) {
                ErrorCode.INIT_ADD_ERROR_4002.throwError();
            }
        } else {
            ErrorCode.INIT_ADD_ERROR_4002.throwError();
        }
        InitHeaderCudBaseResponse response = new InitHeaderCudBaseResponse();
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
        wmsInitHeader.setIvihStatus("10");
        wmsInitHeader.setAuditUser(CustomRequestContext.getUserInfo().getEmployeeName());
        wmsInitHeader.setAuditTime(DateUtils.nowWithUTC());
        if (StringUtils.isNoneBlank(request.getMemo())) {
            wmsInitHeader.setMemo(request.getMemo());
        }
        WmsInitLine wmsInitLine = new WmsInitLine();
        wmsInitLine.setIvihId(request.getId());
        List<WmsInitLine> initLineList = wmsInitLineService.selectByEntity(wmsInitLine);
        List<WmsInventory> wmsInventoryList = new ArrayList<>();
        List<WmsInventoryHistory> wmsInventoryHistoryList = new ArrayList<>();
        List<WmsIvAttributebatch> wmsIvAttributebatchList = new ArrayList<>();
        List<WmsIvTransaction> wmsIvTransactionList = new ArrayList<>();
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
                wmsIvAttributebatch.setQsCode("GD");
                wmsIvAttributebatch.setApCode("ADJT");
                wmsIvAttributebatch.setDocumentCode(wmsInitHeader.getIvihCode());
                wmsIvAttributebatch.setIabDocumentId(request.getId());
                wmsIvAttributebatch.setIabDocumentlineId(initLine.getId());
                wmsIvAttributebatch.setCompanyId(wmsInitHeader.getCompanyId());
                wmsIvAttributebatch.setSupplierId(initLine.getSupplierId());
                wmsIvAttributebatch.setCreateUser(CustomRequestContext.getUserInfo().getAccountId());
                wmsIvAttributebatch.setCreateTime(DateUtils.nowWithUTC());
                // 根据产品id获取产品详情对象
                ProductDetailResponse productResponse = productDetailResponseMap.get(initLine.getProductId());
                if (null != productResponse) {
                    wmsIvAttributebatch.setProductId(initLine.getProductId());
                    wmsIvAttributebatch.setProductNo(productResponse.getProductNo());
                    wmsIvAttributebatch.setProductName(productResponse.getProductName());
                    wmsIvAttributebatch.setProductNameLocal(productResponse.getProductNameLocal());
                    wmsIvAttributebatch.setProductBarcode(productResponse.getProductBarcode());
                    wmsIvAttributebatch.setProductUpcCode(productResponse.getProductUpcCode());
                    wmsIvAttributebatch.setSpecType(productResponse.getSpecType());
                    wmsIvAttributebatch.setProductWidth(productResponse.getProductWidth());
                    wmsIvAttributebatch.setProductLength(productResponse.getProductLength());
                    wmsIvAttributebatch.setProductHeight(productResponse.getProductHeight());
                    wmsIvAttributebatch.setProductColor(productResponse.getProductColor());
                    wmsIvAttributebatch.setProductSize(productResponse.getProductSize());
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
                    wmsIvAttributebatch.setVol(productResponse.getUnitVol());
                    // 组装库存表
                    WmsInventory wmsInventory = new WmsInventory();
                    wmsInventory.setId(idGenerator.getUnique());
                    wmsInventory.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
                    wmsInventory.setWarehouseId(wmsInitHeader.getWarehouseId());
                    wmsInventory.setWarehouseCode(wmsInitHeader.getWarehouseCode());
                    wmsInventory.setLcCode(initLine.getLcCode());
                    if (initLine.getLcCode().contains("S")) {
                        wmsInventory.setLcType("S");
                    } else if (initLine.getLcCode().contains("Z")) {
                        wmsInventory.setLcType("Z");
                    } else {
                        wmsInventory.setLcType("");
                    }
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
                    wmsInventory.setIvFifoTime(DateUtils.nowWithUTC());
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
                    // 组装库存变动记录
                    WmsIvTransaction wmsIvTransaction = new WmsIvTransaction();
                    wmsIvTransaction.setId(idGenerator.getUnique());
                    wmsIvTransaction.setApCode("INIT");
                    wmsIvTransaction.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
                    wmsIvTransaction.setWarehouseCode(wmsInitHeader.getWarehouseCode());
                    wmsIvTransaction.setWarehouseId(wmsInitHeader.getWarehouseId());
                    wmsIvTransaction.setProductId(initLine.getProductId());
                    wmsIvTransaction.setIvFifoTime(DateUtils.nowWithUTC());
                    wmsIvTransaction.setLcCodeFrom(initLine.getLcCode());
                    wmsIvTransaction.setIvQtyFrom(BigDecimal.ZERO);
                    wmsIvTransaction.setLcCodeTo(initLine.getLcCode());
                    wmsIvTransaction.setIvQtyTo(initLine.getIvQty());
                    wmsIvTransaction.setIvtDocumentCode(wmsInitHeader.getIvihCode());
                    wmsIvTransaction.setIvtDocumentId(request.getId());
                    wmsIvTransaction.setIvtDocumentlineId(initLine.getId());
                    wmsIvTransaction.setIvtChangeType("INCR");
                    wmsIvTransaction.setDcQty(initLine.getIvQty());
                    wmsIvTransaction.setIvId(wmsInventory.getId());
                    wmsIvTransaction.setIabId(iabId);
                    wmsIvTransaction.setBizDate(DateUtils.nowWithUTC());
                    wmsIvTransaction.setCreateTime(DateUtils.nowWithUTC());
                    wmsIvTransaction.setCreateUser(CustomRequestContext.getUserInfo().getEmployeeName());
                    wmsIvTransactionList.add(wmsIvTransaction);
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

            /**
             * 库存初始化回写OMS库存
             */
            List<OmsInventoryInitWriteBack.OmsInventory> omsInventoryList = new ArrayList<>();
            for (WmsInventory wmsInventory : wmsInventoryList) {
                OmsInventoryInitWriteBack.OmsInventory omsInventory = new OmsInventoryInitWriteBack.OmsInventory();
                omsInventory.setQty(wmsInventory.getIvQty());
                omsInventory.setCompanyId(wmsInventory.getCompanyId());
                omsInventory.setProductId(wmsInventory.getProductId());
                omsInventory.setWarehouseCode(wmsInventory.getWarehouseCode());
                omsInventory.setWarehouseId(wmsInventory.getWarehouseId());
                omsInventoryList.add(omsInventory);
            }
            OmsInventoryInitWriteBack omsInventoryInitWriteBack = new OmsInventoryInitWriteBack();
            omsInventoryInitWriteBack.setOmsInventoryList(omsInventoryList);
            omsInventoryInitWriteBack.setFromCode(wmsInitHeader.getIvihCode());
            WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add("PUTON", "", omsInventoryInitWriteBack);
            // MQ发送指令
            wmsOmsInventoryInitWriteBackProducer.send(wmsIdInstantdirective);

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
        if (!wmsIvTransactionList.isEmpty()) {
            int insertBatch = wmsIvTransactionService.insertBatch(wmsIvTransactionList);
            if (insertBatch <= 0) {
                ErrorCode.INIT_AUDIT_ERROR_4003.throwError();
            }
        }
        //删除推荐库位埋点
        List<Long> productIds = initLineList.stream().map(WmsInitLine::getProductId).collect(Collectors.toList());
        LcRecommendDeleteRequest lcRecommendDeleteRequest = new LcRecommendDeleteRequest();
        lcRecommendDeleteRequest.setCompanyId(wmsInitHeader.getCompanyId());
        lcRecommendDeleteRequest.setWarehouseId(wmsInitHeader.getWarehouseId());
        lcRecommendDeleteRequest.setOrderCode(wmsInitHeader.getIvihCode());
        lcRecommendDeleteRequest.setProductIds(productIds);
        lcRecommendBussiness.deleteLcRecommend(lcRecommendDeleteRequest);
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
        // 应展示商品字段
        List<WmsInitLineVO> wmsInitLineList = wmsInitLineService.selectInitLinesByEntity(request.getId(), wmsInitHeader.getCompanyId());
        if (!wmsInitLineList.isEmpty()) {
            response.setInitLines(wmsInitLineList);
        }
        return response;
    }

    /**
     * 订单删除
     *
     * @param request
     * @return
     */
    public InitHeaderDeleteResponse deleteInitHeader(InitHeaderDeleteRequest request) {
        WmsInitHeader wmsInitHeader = DataConvertUtil.parseDataAsObject(request, WmsInitHeader.class);
        WmsInitHeader initHeader = wmsInitHeaderService.selectByPrimaryKey(wmsInitHeader.getId());
        if (Objects.isNull(initHeader)) {
            ErrorCode.INIT_NOT_EXIST_4001.throwError();
        }
        if ("10".equals(initHeader.getIvihStatus())) {
            ErrorCode.INIT_DELETE_ERROR_4004.throwError();
        }
        wmsInitHeader.setIvihStatus("98");
        int count = wmsInitHeaderService.updateByPrimaryKeySelective(wmsInitHeader);
        InitHeaderDeleteResponse response = new InitHeaderDeleteResponse();
        response.setIsSuccess(count > 0);
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
        } else {
            conditionsBuilder.ne("ivihStatus", "98");
        }
        if (StringUtils.isNotNull(request.getWarehouseId())) {
            conditionsBuilder.eq("warehouseId", request.getWarehouseId());
        }
        if (StringUtils.isNotBlank(request.getWarehouseCode())) {
            conditionsBuilder.like("warehouseCode", request.getWarehouseCode());
        }
        conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());
        Map<String, Object> paramMap = conditionsBuilder.build();
        paramMap.put("orderBy", " create_time DESC");
        Pagination page = (Pagination) wmsInitHeaderService.selectPage(pagination, paramMap);
        InitHeaderBusinessPageQueryResponse response = new InitHeaderBusinessPageQueryResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 导出模板
     *
     * @param request
     */
    public void exportTemplate(HttpServletRequest request) {
        List<InitLineDetailExcel> excelList = new ArrayList<>();
        InitLineDetailExcel saleOrderDetailExcel = new InitLineDetailExcel(
                "商品码",
                "商品名称",
                "商品条码",
                "货位",
                "",
                new BigDecimal(100),
                "供应商名称"
        );
        excelList.add(saleOrderDetailExcel);
        ExcelUtil<InitLineDetailExcel> excelUtil = new ExcelUtil<>(InitLineDetailExcel.class);
        excelUtil.exportExcel(excelList, "库存初始化商品模板", request);
    }

    /**
     * 检查产品库存是否存在
     *
     * @param request
     * @return
     */
    public List<InitLineCheckResponse> initLineCheck(InitLineCheckRequest request) {
        List<InitLineCheckResponse> responseList = DataConvertUtil.parseDataAsArray(request.getInitLineDetails(), InitLineCheckResponse.class);
        if (StringUtils.isNull(responseList) || responseList.isEmpty()) {
            ErrorCode.INIT_ADD_ERROR_4002.throwError();
        }
        // 通过产品barcode查询产品列表
        Map<String, ProductDetailResponse> productMap = new HashMap<>();
        List<String> productBarcodeList = responseList.stream().map(InitLineCheckResponse::getProductBarcode).collect(Collectors.toList());
        if (!productBarcodeList.isEmpty()) {
            ProductDetailRequest productDetailRequest = new ProductDetailRequest();
            productDetailRequest.setProductBarcodes(productBarcodeList);
            productDetailRequest.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
            try {
                ApiResponse productResponse = productFeignClient.getProductListByBarCode(productDetailRequest);
                ProductListResponse response = JSON.parseObject(JSON.toJSONString(productResponse.getData()), ProductListResponse.class);
                if (StringUtils.isNotNull(response)) {
                    productMap = response.getProductList().stream().collect(Collectors.toMap(ProductDetailResponse::getProductBarcode, Function.identity()));
                }
            } catch (Exception e) {
                ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
            }
        }
        // 通过供应商名字查询供应商列表
        Map<String, SupplierResponse> supplierMap = new HashMap<>();
        List<String> supplierNameList = responseList.stream().map(InitLineCheckResponse::getSupplierName).collect(Collectors.toList());
        SupplierRequest supplierRequest = new SupplierRequest();
        supplierRequest.setSupplierNames(supplierNameList);
        supplierRequest.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        try {
            ApiResponse supplierResponse = supplierFeignClient.getSupplierListByName(supplierRequest);
            SupplierListResponse response = JSON.parseObject(JSON.toJSONString(supplierResponse.getData()), SupplierListResponse.class);
            if (StringUtils.isNotNull(response)) {
                supplierMap = response.getSupplierList().stream().collect(Collectors.toMap(SupplierResponse::getSupplierName, Function.identity()));
            }
        } catch (Exception e) {
            ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
        }
        // 根据库存编码查询库存表记录
        Map<String, WmsInventory> inventoryMap = new HashMap<>();
        List<String> lcCodeList = responseList.stream().map(InitLineCheckResponse::getLcCode).collect(Collectors.toList());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (lcCodeList.size() > 0) {
            conditionsBuilder.in("lcCode", lcCodeList);
        }
        conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());
        Map<String, Object> paramMap = conditionsBuilder.build();
        List<WmsInventory> inventoryList = wmsInventoryService.selectByConditions(paramMap);
        if (StringUtils.isNotNull(inventoryList) && !inventoryList.isEmpty()) {
            inventoryMap = inventoryList.stream().collect(Collectors.toMap(WmsInventory::getLcCode, Function.identity()));
        }
        for (InitLineCheckResponse initLineCheck : responseList) {
            if (StringUtils.isBlank(initLineCheck.getProductBarcode()) ||
                    StringUtils.isBlank(initLineCheck.getLcCode()) ||
                    StringUtils.isBlank(initLineCheck.getSupplierName())) {
                initLineCheck.setReason("条码/供应商名称/库位编码为空!");
                continue;
            }
            ProductDetailResponse productResponse = productMap.get(initLineCheck.getProductBarcode());
            if (Objects.isNull(productResponse)) {
                initLineCheck.setReason("产品不存在!");
            }
            SupplierResponse supplierResponse = supplierMap.get(initLineCheck.getSupplierName());
            if (Objects.isNull(supplierResponse)) {
                initLineCheck.setReason("供应商不存在!");
            }
            WmsInventory inventory = inventoryMap.get(initLineCheck.getLcCode());
            if (Objects.nonNull(inventory) && !inventory.getLcCode().equals(initLineCheck.getLcCode())) {
                initLineCheck.setReason("当前库位已存放其他商品!");
            }
        }
        return responseList;
    }

}
