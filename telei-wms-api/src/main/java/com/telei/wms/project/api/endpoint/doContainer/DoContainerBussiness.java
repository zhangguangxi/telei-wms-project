package com.telei.wms.project.api.endpoint.doContainer;

import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.utils.LockMapUtil;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.amqp.containerWriteBack.OmsContainerWriteBack;
import com.telei.wms.datasource.wms.model.WmsDoContainer;
import com.telei.wms.datasource.wms.model.WmsDoHeader;
import com.telei.wms.datasource.wms.model.WmsDoLine;
import com.telei.wms.datasource.wms.model.WmsIdInstantdirective;
import com.telei.wms.datasource.wms.service.WmsDoContainerService;
import com.telei.wms.datasource.wms.service.WmsDoHeaderService;
import com.telei.wms.datasource.wms.service.WmsDoLineService;
import com.telei.wms.datasource.wms.vo.DoContainerPageQueryRequestVo;
import com.telei.wms.datasource.wms.vo.DoContainerResponseVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.amqp.producer.WmsOmsContainerWriteBackProducer;
import com.telei.wms.project.api.endpoint.doContainer.dto.*;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.WmsIdInstantdirectiveBussiness;
import com.telei.wms.project.api.utils.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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
    private WmsDoHeaderService wmsDoHeaderService;

    @Autowired
    private WmsDoLineService wmsDoLineService;

    @Autowired
    private WmsDoContainerService wmsDoContainerService;

    @Autowired
    private WmsIdInstantdirectiveBussiness wmsIdInstantdirectiveBussiness;

    @Autowired
    private WmsOmsContainerWriteBackProducer wmsOmsContainerWriteBackProducer;

    /**
     * 新增装柜信息
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public DoContainerCudBaseResponse addDoContainer(DoContainerAddRequest request) {
        DoContainerCudBaseResponse response = new DoContainerCudBaseResponse();
        // 校验装柜数量是否超额
        List<DoContainerCommonRequest> containerAddList = request.getContainerAddList();
        List<WmsDoContainer> wmsDoContainers = new ArrayList<>();
        OmsContainerWriteBack omsContainerWriteBack = new OmsContainerWriteBack();
        // 装箱金额
        BigDecimal cAmount = BigDecimal.ZERO;
        // 装箱体积(CBM)
        BigDecimal cVol = BigDecimal.ZERO;
        // 装箱重量(KG)
        BigDecimal cWeight = BigDecimal.ZERO;
        BigDecimal sumQty = BigDecimal.ZERO;
        List<WmsDoLine> wmsDoLines = new ArrayList<>();
        // 获取唯一锁标识
        Object lockKey = request.getDohId();
        Object lockValue = LockMapUtil.tryLock(lockKey);
        // 根据出库任务id查询装柜列表【每个产品的待装柜数量】
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (StringUtils.isNotNull(request.getDohId())) {
            conditionsBuilder.eq("dohId", request.getDohId());
        }
        Map<String, Object> paramMap = conditionsBuilder.build();
        List<DoContainerPageQueryRequestVo> doContainerList = wmsDoContainerService.findDoContainerList(paramMap);
        if (StringUtils.isNull(doContainerList) || doContainerList.size() <= 0) {
            // 暂无待装柜记录
            ErrorCode.DO_CONTAINER_LIST_NOT_EXIST_4003.throwError();
        }
        Map<Long, DoContainerPageQueryRequestVo> containerMap = doContainerList.stream().collect(Collectors.toMap(DoContainerPageQueryRequestVo::getProductId, Function.identity()));
        for (DoContainerCommonRequest commonRequest : containerAddList) {
            DoContainerPageQueryRequestVo queryRequestVo = containerMap.get(commonRequest.getProductId());
            if (Objects.nonNull(queryRequestVo)) {
                BigDecimal pickedQty = BigDecimal.ZERO;
                BigDecimal bQty = BigDecimal.ZERO;
                BigDecimal cQty = BigDecimal.ZERO;
                if (Objects.nonNull(queryRequestVo.getPickedQty())) {
                    pickedQty = queryRequestVo.getPickedQty();
                } else {
                    ErrorCode.DO_CONTAINER_QTY_IS_MAX_4002.throwError(queryRequestVo.getProductName());
                }
                if (Objects.nonNull(queryRequestVo.getBQty())) {
                    bQty = queryRequestVo.getBQty();
                }
                if (Objects.nonNull(queryRequestVo.getCQty())) {
                    cQty = queryRequestVo.getCQty();
                }
                // 待装柜数量
                BigDecimal dQty = pickedQty.subtract(bQty).subtract(cQty);
                if (StringUtils.isNotNull(commonRequest.getQty()) && commonRequest.getQty().compareTo(BigDecimal.ZERO) > 0) {
                    if (commonRequest.getQty().compareTo(dQty) > 0) {
                        ErrorCode.DO_CONTAINER_QTY_IS_MAX_4002.throwError(queryRequestVo.getProductName());
                    }
                    WmsDoContainer wmsDoContainer = DataConvertUtil.parseDataAsObject(commonRequest, WmsDoContainer.class);
                    wmsDoContainer.setId(idGenerator.getUnique());
                    wmsDoContainer.setCQty(commonRequest.getQty());
                    wmsDoContainers.add(wmsDoContainer);

                    omsContainerWriteBack.setOrderType(commonRequest.getOrderType());
                    omsContainerWriteBack.setCId(commonRequest.getCId());
                    omsContainerWriteBack.setDohId(commonRequest.getDohId());
                    omsContainerWriteBack.setSoId(commonRequest.getSoId());
                    omsContainerWriteBack.setSpId(commonRequest.getSpId());

                    cAmount = cAmount.add(commonRequest.getCAmount());
                    if (StringUtils.isNotNull(commonRequest.getCVol())) {
                        cVol = cVol.add(commonRequest.getCVol());
                    }
                    if (StringUtils.isNotNull(commonRequest.getCWeight())) {
                        cWeight = cWeight.add(commonRequest.getCWeight());
                    }
                    sumQty = sumQty.add(commonRequest.getQty());

                    WmsDoLine wmsDoLine = wmsDoLineService.selectByPrimaryKey(commonRequest.getDolId());
                    if (StringUtils.isNotNull(wmsDoLine.getContainerQty())) {
                        wmsDoLine.setContainerQty(wmsDoLine.getContainerQty().add(commonRequest.getQty()));
                    } else {
                        wmsDoLine.setContainerQty(commonRequest.getQty());
                    }
                    wmsDoLines.add(wmsDoLine);
                } else {
                    ErrorCode.DO_CONTAINER_QTY_IS_NULL_4004.throwError(queryRequestVo.getProductName());
                }
            } else {
                ErrorCode.DO_CONTAINER_QTY_IS_MAX_4002.throwError(commonRequest.getProductId());
            }
        }
        int count = 0;
        if (wmsDoContainers.size() > 0) {
            if (LockMapUtil.confirmLock(lockKey, lockValue)) {
                count = wmsDoContainerService.insertBatch(wmsDoContainers);
                WmsDoHeader wmsDoHeader = wmsDoHeaderService.selectByPrimaryKey(omsContainerWriteBack.getDohId());
                if (StringUtils.isNotNull(wmsDoHeader.getContainerQty())) {
                    wmsDoHeader.setContainerQty(wmsDoHeader.getContainerQty().add(sumQty));
                } else {
                    wmsDoHeader.setContainerQty(sumQty);
                }
                wmsDoHeaderService.updateByPrimaryKeySelective(wmsDoHeader);
                if (wmsDoLines.size() > 0) {
                    wmsDoLineService.updateBatch(wmsDoLines);
                }
                omsContainerWriteBack.setCAmount(cAmount);
                omsContainerWriteBack.setCVol(cVol);
                omsContainerWriteBack.setCWeight(cWeight);
                omsContainerWriteBack.setDohCode(wmsDoHeader.getDohCode());

                WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add(wmsOmsContainerWriteBackProducer.getQueueName(), "", omsContainerWriteBack);
                // MQ发送指令
                wmsOmsContainerWriteBackProducer.send(wmsIdInstantdirective);
            }
            LockMapUtil.cancelLock(lockKey, lockValue);
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
    @Transactional(rollbackFor = Exception.class)
    public DoContainerCudBaseResponse revokeDoContainer(DoContainerRevokeRequest request) {
        DoContainerCudBaseResponse response = new DoContainerCudBaseResponse();
        WmsDoHeader doHeader = wmsDoHeaderService.selectByPrimaryKey(request.getDohId());
        if (Objects.nonNull(doHeader)) {
            if ("40".equals(doHeader.getOrderStatus())) {
                ErrorCode.DO_HEADER_CONTAINER_ERROR_4004.throwError();
            }
        } else {
            ErrorCode.DO_NOT_EXIST_4001.throwError();
        }
        WmsDoContainer wmsDoContainer = new WmsDoContainer();
        wmsDoContainer.setCId(request.getCId());
        wmsDoContainer.setDohId(request.getDohId());
        List<Long> lockKey = Stream.of(request.getCId(), request.getDohId()).collect(toList());
        Object lockValue = LockMapUtil.tryLock(lockKey);
        List<WmsDoContainer> doContainerList = wmsDoContainerService.selectByEntity(wmsDoContainer);
        int count = 0;
        if (StringUtils.isNotNull(doContainerList) && !doContainerList.isEmpty()) {
            // 出库任务装柜id列表
            List<Long> docIds = doContainerList.stream().map(WmsDoContainer::getId).collect(toList());
            // 出库任务明细id列表
            List<Long> dolIds = doContainerList.stream().map(WmsDoContainer::getDolId).collect(toList());
            // 出库任务装箱信息明细
            Map<Long, BigDecimal> dolMap = doContainerList.stream().collect(Collectors.toMap(WmsDoContainer::getDolId, WmsDoContainer::getCQty));
            List<WmsDoLine> doLineList = wmsDoLineService.selectByPrimaryKeys(dolIds);
            BigDecimal sumContainerQty = BigDecimal.ZERO;
            for (WmsDoLine wmsDoLine : doLineList) {
                BigDecimal containerQty = dolMap.get(wmsDoLine.getId());
                sumContainerQty = sumContainerQty.add(containerQty);
                wmsDoLine.setContainerQty(wmsDoLine.getContainerQty().subtract(containerQty));
            }
            if (LockMapUtil.confirmLock(lockKey, lockValue)) {
                // 批量扣减出库任务明细数据
                wmsDoLineService.updateBatch(doLineList);
                // 扣减出库任务单头数据
                WmsDoHeader wmsDoHeader = wmsDoHeaderService.selectByPrimaryKey(request.getDohId());
                wmsDoHeader.setContainerQty(wmsDoHeader.getContainerQty().subtract(sumContainerQty));
                wmsDoHeaderService.updateByPrimaryKeySelective(wmsDoHeader);
                if (docIds.size() > 0) {
                    count = wmsDoContainerService.deleteByPrimaryKeys(docIds);
                }
            }
            LockMapUtil.cancelLock(lockKey, lockValue);
        }
        response.setIsSuccess(count > 0);
        return response;
    }

}
