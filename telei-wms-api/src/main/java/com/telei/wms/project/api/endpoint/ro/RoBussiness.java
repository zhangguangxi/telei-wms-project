package com.telei.wms.project.api.endpoint.ro;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.customer.amqp.recovicePlan.OmsRecovicePlan;
import com.telei.wms.datasource.wms.model.WmsIdInstantdirective;
import com.telei.wms.datasource.wms.model.WmsRoHeader;
import com.telei.wms.datasource.wms.model.WmsRoLine;
import com.telei.wms.datasource.wms.service.WmsRoHeaderService;
import com.telei.wms.datasource.wms.service.WmsRoLineService;
import com.telei.wms.datasource.wms.vo.RoHeaderPageQueryRequestVo;
import com.telei.wms.datasource.wms.vo.RoLinePageQueryResponseVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.amqp.producer.WmsOmsRecovicePlanCancelCallbackProducer;
import com.telei.wms.project.api.amqp.producer.WmsOmsRecovicePlanUpdateProducer;
import com.telei.wms.project.api.endpoint.lcRecommend.LcRecommendBussiness;
import com.telei.wms.project.api.endpoint.lcRecommend.dto.LcRecommendAddRequest;
import com.telei.wms.project.api.endpoint.ro.dto.*;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.WmsIdInstantdirectiveBussiness;
import com.telei.wms.project.api.utils.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.telei.infrastructure.component.commons.utils.LockMapUtil.*;

/**
 * @Description: 入库任务
 * @Auther: Dean
 * @Date: 2020/8/19 17:05
 */
@Service
@Slf4j
public class RoBussiness {

    //删除
    private static final String DELETE_STATUS = "99";
    //部分入库
    private static final String PART_INVENTORY_STATUS = "40";
    //关闭
    private static final String CLOSE_STATUS = "98";

    @Autowired
    private Id idGenerator;

    @Autowired
    private WmsRoHeaderService wmsRoHeaderService;

    @Autowired
    private WmsRoLineService wmsRoLineService;

    @Autowired
    private WmsIdInstantdirectiveBussiness wmsIdInstantdirectiveBussiness;

    @Autowired
    private WmsOmsRecovicePlanCancelCallbackProducer wmsOmsRecovicePlanCancelCallbackProducer;

    @Autowired
    private WmsOmsRecovicePlanUpdateProducer wmsOmsRecovicePlanUpdateProducer;

    @Autowired
    private LcRecommendBussiness lcRecommendBussiness;

    /**
     * 新增
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public RoCudBaseResponse addRo(RoHeaderAddRequest request) {
        log.debug("**********addRo" + JSON.toJSONString(request));
        RoCudBaseResponse response = new RoCudBaseResponse();
        WmsRoHeader wmsRoHeaderEntity = new WmsRoHeader();
        wmsRoHeaderEntity.setRpId(request.getRpId());
        WmsRoHeader wmsRoHeaderIsExist = wmsRoHeaderService.selectOneByEntity(wmsRoHeaderEntity);
        if (! Objects.isNull(wmsRoHeaderIsExist)) {
            log.warn("采购单不能重复生成入库任务，RoHeaderAddRequest：" + JSON.toJSONString(request));
            return response;
        }
        WmsRoHeader wmsRoHeader = DataConvertUtil.parseDataAsObject(request, WmsRoHeader.class);
        List<WmsRoLine> wmsRoLines = DataConvertUtil.parseDataAsArray(request.getRoLines(), WmsRoLine.class);
        wmsRoHeader.setCreateTime(DateUtils.nowWithUTC());
        wmsRoHeaderService.insertSelective(wmsRoHeader);
        wmsRoLineService.insertBatch(wmsRoLines);
        //添加新品分配推荐库位
        List<Long> productIds = wmsRoLines.stream().map(WmsRoLine::getProductId).collect(Collectors.toList());
        LcRecommendAddRequest lcRecommendAddRequest = new LcRecommendAddRequest();
        lcRecommendAddRequest.setCompanyId(wmsRoHeader.getCompanyId());
        lcRecommendAddRequest.setWarehouseId(wmsRoHeader.getWarehouseId());
        lcRecommendAddRequest.setWarehouseCode(wmsRoHeader.getWarehouseCode());
        lcRecommendAddRequest.setEstArriveTime(wmsRoHeader.getEstArriveTime());
        lcRecommendAddRequest.setCreateUser(wmsRoHeader.getCreateUser());
        lcRecommendAddRequest.setProductIds(productIds);
        lcRecommendBussiness.addLcRecommend(lcRecommendAddRequest);
        return response;
    }

    /**
     * 订单详细
     * @param request
     * @return
     */
    public RoHeaderDetailResponse roDetail(RoDetailRequest request) {
        WmsRoHeader wmsRoHeader = wmsRoHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsRoHeader)) {
            //入库任务主单不存在
            ErrorCode.RO_NOT_EXIST_4001.throwError();
        }
        RoHeaderDetailResponse response = DataConvertUtil.parseDataAsObject(wmsRoHeader, RoHeaderDetailResponse.class);
        List<RoLinePageQueryResponseVo> roLinePageQueryResponseVos = wmsRoLineService.findAll(request.getId(), wmsRoHeader.getCompanyId());
        List<RoLineDetailResponse> roLineDetailResponses = DataConvertUtil.parseDataAsArray(roLinePageQueryResponseVos, RoLineDetailResponse.class);
        response.setRoLines(roLineDetailResponses);
        return response;
    }

    /**
     * 分页查询主单
     * @param request
     * @return
     */
    public RoHeaderPageQueryResponse pageQueryRoHeader(RoHeaderPageQueryRequest request) {
        RoHeaderPageQueryRequestVo requestVo = DataConvertUtil.parseDataAsObject(request, RoHeaderPageQueryRequestVo.class);
        requestVo.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        Pagination page = (Pagination) wmsRoHeaderService.findAll(pagination, requestVo);
        RoHeaderPageQueryResponse response = new RoHeaderPageQueryResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 更新主单信息
     * @param request
     * @return
     */
    public RoCudBaseResponse updateRoHeader(RoHeaderUpdateRequest request) {
        WmsRoHeader wmsRoHeaderEntity = new WmsRoHeader();
        wmsRoHeaderEntity.setId(request.getId());
        wmsRoHeaderEntity.setRpId(request.getRpId());
        WmsRoHeader wmsRoHeaderInfo = wmsRoHeaderService.selectOneByEntity(wmsRoHeaderEntity);
        if (Objects.isNull(wmsRoHeaderInfo)) {
            ErrorCode.RO_NOT_EXIST_4001.throwError();
        }
        WmsRoHeader wmsRoHeader = DataConvertUtil.parseDataAsObject(request, WmsRoHeader.class);
        wmsRoHeader.setId(wmsRoHeaderInfo.getId());
        if (Objects.nonNull(CustomRequestContext.getUserInfo())) {
            wmsRoHeader.setLastUpdateUser(CustomRequestContext.getUserInfo().getEmployeeName());
        }
        wmsRoHeader.setLastUpdateTime(DateUtils.nowWithUTC());
        int updateResult = wmsRoHeaderService.updateByPrimaryKeySelective(wmsRoHeader);
        RoCudBaseResponse response = new RoCudBaseResponse();
        response.setIsSuccess(updateResult > 0);
        if (response.getIsSuccess() && request.getIsSync()) {
            //需要同步到计划
            OmsRecovicePlan omsRecovicePlan = DataConvertUtil.parseDataAsObject(request, OmsRecovicePlan.class);
            omsRecovicePlan.setId(wmsRoHeaderInfo.getRpId());
            omsRecovicePlan.setMemo(null);
            //添加数据交互指令
            WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add("PUTON", "", omsRecovicePlan);
            //发送消息到队列
            wmsOmsRecovicePlanUpdateProducer.send(wmsIdInstantdirective);
        }
        return response;
    }

    /**
     * 取消入库任务
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public RoCudBaseResponse cancelRoHeader(RoHeaderCancelRequest request) {
        WmsRoHeader wmsRoHeaderEntity = DataConvertUtil.parseDataAsObject(request, WmsRoHeader.class);
        OmsRecovicePlan omsRecovicePlan = new OmsRecovicePlan();
        omsRecovicePlan.setId(request.getRpId());
        //默认撤销失败状态
        omsRecovicePlan.setOrderStatus("92");
        //获取锁标识
        String lockKey = String.valueOf(request.getRpId());
        Object lockValue = tryLock(lockKey);
        try {
            WmsRoHeader wmsRoHeader = wmsRoHeaderService.selectOneByEntity(wmsRoHeaderEntity);
            if (Objects.isNull(wmsRoHeader)) {
                //待同步状态
                omsRecovicePlan.setOrderStatus("90");
            } else if (DELETE_STATUS.equals(wmsRoHeader.getOrderStatus())) {
                //已撤销状态
                omsRecovicePlan.setOrderStatus("91");
            } else if (wmsRoHeader.getReceQty().intValue() <= 0) {
                //确认锁
                if (confirmLock(lockKey, lockValue)) {
                    //取消入库任务
                    WmsRoHeader updateWmsRoHeader = new WmsRoHeader();
                    updateWmsRoHeader.setId(wmsRoHeader.getId());
                    updateWmsRoHeader.setOrderStatus(DELETE_STATUS);
                    updateWmsRoHeader.setLastUpdateTime(DateUtils.nowWithUTC());
                    wmsRoHeaderService.updateByPrimaryKeySelective(updateWmsRoHeader);
                    //已撤销状态
                    omsRecovicePlan.setOrderStatus("91");
                }
            }
        } finally {
            //释放锁
            cancelLock(lockKey, lockValue);
        }
        //添加数据交互指令
        WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add("PUTON", "", omsRecovicePlan);
        //发送消息到队列
        wmsOmsRecovicePlanCancelCallbackProducer.send(wmsIdInstantdirective);
        RoCudBaseResponse response = new RoCudBaseResponse();
        return response;
    }

    /**
     * 获取强制收货状态
     * @param request
     * @return
     */
    public RoCudBaseResponse getRoHeaderEnforcement(RoHeaderGetEnforcementRequest request) {
        WmsRoHeader wmsRoHeaderEntity = DataConvertUtil.parseDataAsObject(request, WmsRoHeader.class);
        WmsRoHeader wmsRoHeader = wmsRoHeaderService.selectOneByEntity(wmsRoHeaderEntity);
        if (Objects.isNull(wmsRoHeader)) {
            ErrorCode.RO_NOT_EXIST_4001.throwError();
        }
        RoCudBaseResponse response = new RoCudBaseResponse();
        response.setIsSuccess(wmsRoHeader.getEnforcement() == 1);
        return response;
    }

    /**
     * 更新强制收货
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public RoCudBaseResponse updateRoHeaderEnforcement(RoHeaderUpdateEnforcementRequest request) {
        WmsRoHeader wmsRoHeaderEntity = DataConvertUtil.parseDataAsObject(request, WmsRoHeader.class);
        RoCudBaseResponse response = new RoCudBaseResponse();
        //获取锁标识
        String lockKey = String.valueOf(request.getRpId());
        Object lockValue = tryLock(lockKey);
        try {
            WmsRoHeader wmsRoHeader = wmsRoHeaderService.selectOneByEntity(wmsRoHeaderEntity);
            if (Objects.isNull(wmsRoHeader)) {
                ErrorCode.RO_NOT_EXIST_4001.throwError();
            }
            if (CLOSE_STATUS.equals(wmsRoHeader.getOrderStatus())) {
                return response;
            }
            if (! PART_INVENTORY_STATUS.equals(wmsRoHeader.getOrderStatus())) {
                //强制收货状态不正确
                ErrorCode.RO_ENFORCEMENT_ERROR_4002.throwError();
            }
            if (wmsRoHeader.getReceQty().compareTo(wmsRoHeader.getPutawayQty()) != 0) {
                //收货数和上架数不相等不能强制收货
                ErrorCode.RO_ENFORCEMENT_ERROR_4003.throwError();
            }
            WmsRoHeader updateWmsRoHeader = new WmsRoHeader();
            updateWmsRoHeader.setId(wmsRoHeader.getId());
            updateWmsRoHeader.setOrderStatus(CLOSE_STATUS);
            updateWmsRoHeader.setEnforcement(1);
            wmsRoHeader.setLastUpdateUser(CustomRequestContext.getUserInfo().getEmployeeName());
            wmsRoHeader.setLastUpdateTime(DateUtils.nowWithUTC());
            //确认锁
            if (confirmLock(lockKey, lockValue)) {
                int updateResult = wmsRoHeaderService.updateByPrimaryKeySelective(updateWmsRoHeader);
                response.setIsSuccess(updateResult > 0);
            } else {
                response.setIsSuccess(false);
            }
        } finally {
            //释放锁
            cancelLock(lockKey, lockValue);
        }
        return response;
    }
}
