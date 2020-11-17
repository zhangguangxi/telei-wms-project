package com.telei.wms.project.api.endpoint.doContainer;

import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.datasource.wms.model.WmsDoContainer;
import com.telei.wms.datasource.wms.service.WmsDoContainerService;
import com.telei.wms.datasource.wms.vo.DoContainerResponseVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.doContainer.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 出库任务
 * @Auther: Dean
 * @Date: 2020/8/19 17:05
 */
@Service
@Slf4j
public class DoContainerBussiness {

    @Autowired
    private Id idGenerator;

    @Autowired
    private WmsDoContainerService wmsDoContainerService;

    /**
     * 新增装柜信息
     *
     * @param request
     * @return
     */
    public DoContainerCudBaseResponse addDoContainer(DoContainerAddRequest request) {
        DoContainerCudBaseResponse response = new DoContainerCudBaseResponse();
        // 校验装柜数量是否超额
        List<DoContainerCommonRequest> containerAddList = request.getContainerAddList();
        List<WmsDoContainer> wmsDoContainers = new ArrayList<>();
        for (DoContainerCommonRequest commonRequest : containerAddList) {
            // 待装柜数量
            BigDecimal dQty = commonRequest.getPickedQty().subtract(commonRequest.getBQty()).subtract(commonRequest.getCQty());
            if (StringUtils.isNull(commonRequest.getQty()) || commonRequest.getQty().compareTo(BigDecimal.ZERO) == 0) {
                // 装箱数量不能为空或者为0
                ErrorCode.DO_CONTAINER_QTY_IS_NULL_4001.throwError();
            }
            if (commonRequest.getQty().compareTo(dQty) > 0) {
                // 装柜数量不能超过待装柜数量
                ErrorCode.DO_CONTAINER_QTY_IS_MAX_4002.throwError();
            }
            WmsDoContainer wmsDoContainer = DataConvertUtil.parseDataAsObject(commonRequest, WmsDoContainer.class);
            wmsDoContainer.setId(idGenerator.getUnique());
            wmsDoContainer.setCQty(commonRequest.getQty());
            wmsDoContainers.add(wmsDoContainer);
        }
        int count = 0;
        if (wmsDoContainers.size() > 0) {
            count = wmsDoContainerService.insertBatch(wmsDoContainers);
        }
        response.setIsSuccess(count > 0);
        return response;
    }

    /**
     * 订单详细
     *
     * @param request
     * @return
     */
    public DoContainerDetailResponse queryOMSContainerDetail(DoContainerDetailRequest request) {
        DoContainerDetailResponse response = new DoContainerDetailResponse();
        List<DoContainerResponseVo> containerDetailList = wmsDoContainerService.findAll(request.getCId());
        List<DoContainerCommonResponse> doContainerResponses = new ArrayList<>();
        if (containerDetailList.size() > 0) {
            doContainerResponses = DataConvertUtil.parseDataAsArray(containerDetailList, DoContainerCommonResponse.class);
        }
        response.setContainerDetailList(doContainerResponses);
        return response;
    }

    /**
     * 订单详细
     *
     * @param request
     * @return
     */
    public DoContainerDetailResponse queryWMSContainerDetail(DoContainerPageQueryRequest request) {
        DoContainerDetailResponse response = new DoContainerDetailResponse();
        List<DoContainerResponseVo> containerDetailList = wmsDoContainerService.findAllByDohId(request.getDohId());
        List<DoContainerCommonResponse> doContainerResponses = new ArrayList<>();
        if (containerDetailList.size() > 0) {
            doContainerResponses = DataConvertUtil.parseDataAsArray(containerDetailList, DoContainerCommonResponse.class);
        }
        response.setContainerDetailList(doContainerResponses);
        return response;
    }

    /**
     * 订单详细
     *
     * @param request
     * @return
     */
    public DoContainerPageQueryResponse queryContainerDetailList(DoContainerDetailPageQueryRequest request) {
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (StringUtils.isNotNull(request.getDohId())) {
            conditionsBuilder.eq("dohId", request.getDohId());
        }
        if (StringUtils.isNotNull(request.getCId())) {
            conditionsBuilder.eq("cId", request.getCId());
        }
        conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());
        Map<String, Object> paramMap = conditionsBuilder.build();
        Pagination page = (Pagination) wmsDoContainerService.queryContainerDetailList(pagination, paramMap);
        DoContainerPageQueryResponse response = new DoContainerPageQueryResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 分页查询主单
     *
     * @param request
     * @return
     */
    public DoContainerPageQueryResponse pageQueryDoContainer(DoContainerPageQueryRequest request) {
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (StringUtils.isNotNull(request.getDohId())) {
            conditionsBuilder.eq("dohId", request.getDohId());
        }
        Map<String, Object> paramMap = conditionsBuilder.build();
        Pagination page = (Pagination) wmsDoContainerService.findAllDoContainer(pagination, paramMap);
        DoContainerPageQueryResponse response = new DoContainerPageQueryResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 撤销装柜信息
     *
     * @param request
     * @return
     */
    public DoContainerCudBaseResponse revokeDoContainer(DoContainerRevokeRequest request) {
        DoContainerCudBaseResponse response = new DoContainerCudBaseResponse();
        WmsDoContainer wmsDoContainer = new WmsDoContainer();
        wmsDoContainer.setCId(request.getCId());
        wmsDoContainer.setDohId(request.getDohId());
        List<WmsDoContainer> doContainerList = wmsDoContainerService.selectByEntity(wmsDoContainer);
        int count = 0;
        if (StringUtils.isNotNull(doContainerList) && !doContainerList.isEmpty()) {
            List<Long> docIds = doContainerList.stream().map(WmsDoContainer::getId).collect(Collectors.toList());
            if (docIds.size() > 0) {
                count = wmsDoContainerService.deleteByPrimaryKeys(docIds);
            }
        }
        response.setIsSuccess(count > 0);
        return response;
    }

}
