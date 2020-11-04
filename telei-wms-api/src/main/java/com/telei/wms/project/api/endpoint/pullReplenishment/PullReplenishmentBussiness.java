package com.telei.wms.project.api.endpoint.pullReplenishment;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.wms.commons.utils.CommonResponse;
import com.telei.wms.commons.utils.ExcelUtil;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.product.ProductFeignClient;
import com.telei.wms.customer.product.dto.ProductCategoryRequest;
import com.telei.wms.customer.product.dto.ProductCategoryResponse;
import com.telei.wms.datasource.wms.service.WmsDoHeaderService;
import com.telei.wms.datasource.wms.vo.PullReplenishmentPageQueryResponseVo;
import com.telei.wms.project.api.endpoint.pullReplenishment.dto.PullReplenishmentBusinessPageQueryRequest;
import com.telei.wms.project.api.endpoint.pullReplenishment.dto.PullReplenishmentBusinessPageQueryResponse;
import com.telei.wms.project.api.endpoint.pullReplenishment.dto.PullReplenishmentExcel;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author gongrp
 */
@Service
public class PullReplenishmentBussiness {

    @Autowired
    private WmsDoHeaderService wmsDoHeaderService;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * 分页查询主单
     */
    public PullReplenishmentBusinessPageQueryResponse pullReplenishmentPageQuery(PullReplenishmentBusinessPageQueryRequest request) {
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (StringUtils.isNotBlank(request.getStartTime()) && StringUtils.isNotBlank(request.getEndTime())) {
            conditionsBuilder.between("shippingTime", request.getStartTime(), request.getEndTime());
        }
        if (StringUtils.isNoneBlank(request.getProductNo())) {
            conditionsBuilder.like("productNo", request.getProductNo());
        }
        if (StringUtils.isNoneBlank(request.getInternalSupplier())) {
            conditionsBuilder.eq("internalSupplier", request.getInternalSupplier());
        }
        if (StringUtils.isNotNull(request.getWarehouseId())) {
            conditionsBuilder.eq("warehouseId", request.getWarehouseId());
        }
        if (StringUtils.isNotNull(request.getProductCategoryIds())) {
            // 根据产品id列表获取产品列表信息
            ProductCategoryRequest productCategoryRequest = new ProductCategoryRequest();
            productCategoryRequest.setProductCategoryIds(request.getProductCategoryIds());
            ApiResponse detailResponse = productFeignClient.productCategoryListQuery(productCategoryRequest);
            ProductCategoryResponse response = JSON.parseObject(JSON.toJSONString(detailResponse.getData()), ProductCategoryResponse.class);
            if (response.getPCategoryIds().size() > 0) {
                conditionsBuilder.in("productCategoryId", response.getPCategoryIds());
            }
        }
        if (StringUtils.isNoneBlank(request.getProductName())) {
            conditionsBuilder.like("productName", request.getProductName());
        }
        if (StringUtils.isNoneBlank(request.getProductBarcode())) {
            conditionsBuilder.like("productBarcode", request.getProductBarcode());
        }
        if (StringUtils.isNoneBlank(request.getSupplierName())) {
            conditionsBuilder.like("supplierName", request.getSupplierName());
        }
        conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());
        Map<String, Object> paramMap = conditionsBuilder.build();
        Pagination page = (Pagination) wmsDoHeaderService.pullReplenishmentPageQuery(pagination, paramMap);
        PullReplenishmentBusinessPageQueryResponse response = new PullReplenishmentBusinessPageQueryResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 分页查询主单
     */
    public CommonResponse pullReplenishmentList(PullReplenishmentBusinessPageQueryRequest businessPageQueryRequest, HttpServletRequest request) {
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        StringBuffer title = new StringBuffer();
        if (StringUtils.isNotBlank(businessPageQueryRequest.getStartTime()) && StringUtils.isNotBlank(businessPageQueryRequest.getEndTime())) {
            conditionsBuilder.between("shippingTime", businessPageQueryRequest.getStartTime(), businessPageQueryRequest.getEndTime());
            title.append("出库时间：");
            title.append(businessPageQueryRequest.getStartTime());
            title.append("-");
            title.append(businessPageQueryRequest.getEndTime());
            title.append(" ");
        }
        if (StringUtils.isNoneBlank(businessPageQueryRequest.getProductNo())) {
            conditionsBuilder.like("productNo", businessPageQueryRequest.getProductNo());
            title.append("商品码：");
            title.append(businessPageQueryRequest.getProductNo());
            title.append(" ");
        }
        if (StringUtils.isNoneBlank(businessPageQueryRequest.getInternalSupplier())) {
            conditionsBuilder.eq("internalSupplier", businessPageQueryRequest.getInternalSupplier());
        }
        if (StringUtils.isNotNull(businessPageQueryRequest.getWarehouseId())) {
            conditionsBuilder.eq("warehouseId", businessPageQueryRequest.getWarehouseId());
        }
        if (StringUtils.isNotNull(businessPageQueryRequest.getProductCategoryIds())) {
            // 根据产品id列表获取产品列表信息
            ProductCategoryRequest productCategoryRequest = new ProductCategoryRequest();
            productCategoryRequest.setProductCategoryIds(Lists.newArrayList(businessPageQueryRequest.getProductCategoryIds()));
            ApiResponse detailResponse = productFeignClient.productCategoryListQuery(productCategoryRequest);
            ProductCategoryResponse response = JSON.parseObject(JSON.toJSONString(detailResponse.getData()), ProductCategoryResponse.class);
            if (response.getPCategoryIds().size() > 0) {
                conditionsBuilder.in("productCategoryId", response.getPCategoryIds());
            }
        }
        if (StringUtils.isNoneBlank(businessPageQueryRequest.getProductName())) {
            conditionsBuilder.like("productName", businessPageQueryRequest.getProductName());
            title.append("产品名称：");
            title.append(businessPageQueryRequest.getProductName());
            title.append(" ");
        }
        if (StringUtils.isNoneBlank(businessPageQueryRequest.getProductCategoryName())) {
            title.append("产品分类名称：");
            title.append(businessPageQueryRequest.getProductCategoryName());
            title.append(" ");
        }
        if (StringUtils.isNoneBlank(businessPageQueryRequest.getProductBarcode())) {
            conditionsBuilder.like("productBarcode", businessPageQueryRequest.getProductBarcode());
            title.append("商品条码：");
            title.append(businessPageQueryRequest.getProductBarcode());
            title.append(" ");
        }
        if (StringUtils.isNoneBlank(businessPageQueryRequest.getSupplierName())) {
            conditionsBuilder.like("supplierName", businessPageQueryRequest.getSupplierName());
            title.append("供应商名称：");
            title.append(businessPageQueryRequest.getProductBarcode());
            title.append(" ");
        }
        conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());
        Map<String, Object> paramMap = conditionsBuilder.build();
        List<PullReplenishmentPageQueryResponseVo> pullReplenishmentList = wmsDoHeaderService.pullReplenishmentList(paramMap);
        List<PullReplenishmentExcel> list = new ArrayList<>();
        if (pullReplenishmentList.size() > 0) {
            for (PullReplenishmentPageQueryResponseVo responseVo : pullReplenishmentList) {
                PullReplenishmentExcel productExcel = DataConvertUtil.parseDataAsObject(responseVo, PullReplenishmentExcel.class);
                list.add(productExcel);
            }
        }
        ExcelUtil<PullReplenishmentExcel> util = new ExcelUtil<>(PullReplenishmentExcel.class);
        util.exportExcel(list, "拉式补货=" + title, request);
        CommonResponse excelResponse = new CommonResponse();
        excelResponse.setIsSuccess(true);
        return excelResponse;
    }

}
