package com.telei.wms.project.api.endpoint.doHeader;

import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.amqp.shipPlan.OmsShipPlan;
import com.telei.wms.datasource.wms.model.WmsDoHeader;
import com.telei.wms.datasource.wms.model.WmsDoLine;
import com.telei.wms.datasource.wms.model.WmsIdInstantdirective;
import com.telei.wms.datasource.wms.model.WmsPloHeader;
import com.telei.wms.datasource.wms.service.WmsDoHeaderService;
import com.telei.wms.datasource.wms.service.WmsDoLineService;
import com.telei.wms.datasource.wms.service.WmsPloHeaderService;
import com.telei.wms.datasource.wms.vo.DoLineResponseVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.amqp.producer.WmsOmsRecovicePlanAddByDoProducer;
import com.telei.wms.project.api.amqp.producer.WmsOmsShipPlanCancelCallbackProducer;
import com.telei.wms.project.api.endpoint.doHeader.dto.*;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.WmsIdInstantdirectiveBussiness;
import com.telei.wms.project.api.utils.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.telei.infrastructure.component.commons.utils.LockMapUtil.*;

/**
 * @Description: 出库任务
 * @Auther: Dean
 * @Date: 2020/8/19 17:05
 */
@Service
@Slf4j
public class DoHeaderBussiness {

    //删除
    private static final String DELETE_STATUS = "99";

    @Autowired
    private WmsDoHeaderService wmsDoHeaderService;

    @Autowired
    private WmsDoLineService wmsDoLineService;

    @Autowired
    private WmsPloHeaderService wmsPloHeaderService;

    @Autowired
    private WmsIdInstantdirectiveBussiness wmsIdInstantdirectiveBussiness;

    @Autowired
    private WmsOmsShipPlanCancelCallbackProducer wmsOmsShipPlanCancelCallbackProducer;

    @Autowired
    private WmsOmsRecovicePlanAddByDoProducer wmsOmsRecovicePlanAddByDoProducer;

    /**
     * 新增
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public DoCudBaseResponse addDo(DoHeaderAddRequest request) {
        WmsDoHeader wmsDoHeader = DataConvertUtil.parseDataAsObject(request, WmsDoHeader.class);
        List<WmsDoLine> wmsDoLines = DataConvertUtil.parseDataAsArray(request.getDoLines(), WmsDoLine.class);
        wmsDoHeader.setCreateTime(DateUtils.nowWithUTC());
        wmsDoHeaderService.insertSelective(wmsDoHeader);
        wmsDoLineService.insertBatch(wmsDoLines);
        DoCudBaseResponse response = new DoCudBaseResponse();
        return response;
    }

    /**
     * 订单详细
     *
     * @param request
     * @return
     */
    public DoHeaderDetailResponse doDetail(DoDetailRequest request) {
        WmsDoHeader wmsDoHeader = wmsDoHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsDoHeader)) {
            //出库任务主单不存在
            ErrorCode.DO_NOT_EXIST_4001.throwError();
        }
        DoHeaderDetailResponse response = DataConvertUtil.parseDataAsObject(wmsDoHeader, DoHeaderDetailResponse.class);
        List<DoLineResponseVo> wmsDoLineList = wmsDoLineService.findAll(request.getId(), wmsDoHeader.getCompanyId());
        List<DoLineDetailResponse> doLineDetailResponses = DataConvertUtil.parseDataAsArray(wmsDoLineList, DoLineDetailResponse.class);
        response.setDoLines(doLineDetailResponses);
        // 查询拣货单id
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        conditionsBuilder.eq("dohCode", wmsDoHeader.getDohCode());
        conditionsBuilder.eq("dohId", wmsDoHeader.getId());
        conditionsBuilder.ne("orderStatus", "98");
        Map<String, Object> paramMap = conditionsBuilder.build();
        WmsPloHeader ploHeader = wmsPloHeaderService.selectOneByConditions(paramMap);
        if (Objects.nonNull(ploHeader)) {
            response.setPloId(ploHeader.getId());
        }
        return response;
    }

    /**
     * 分页查询主单
     *
     * @param request
     * @return
     */
    public DoHeaderPageQueryResponse pageQueryDoHeader(DoHeaderPageQueryRequest request) {
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        List<String> orderStatuss = new ArrayList<>(Arrays.asList("98", "99"));
        if (null != request.getStartTime() && null != request.getEndTime()) {
            conditionsBuilder.between("createTime", request.getStartTime(), request.getEndTime());
        }
        if (StringUtils.isNoneBlank(request.getDohCode())) {
            conditionsBuilder.like("dohCode", request.getDohCode());
        }
        if (StringUtils.isNoneBlank(request.getOrderStatus())) {
            if ("01".equals(request.getOrderStatus())) {
                conditionsBuilder.eq("hasPlo", "0");
                conditionsBuilder.eq("hadCheck", "0");
            } else if ("02".equals(request.getOrderStatus())) {
                conditionsBuilder.eq("hadCheck", "1");
                conditionsBuilder.ne("orderStatus", "40");
            }  else if ("20".equals(request.getOrderStatus())) {
                conditionsBuilder.eq("orderStatus", "20");
            }  else if ("25".equals(request.getOrderStatus())) {
                conditionsBuilder.eq("orderStatus", "25");
            } else if ("30".equals(request.getOrderStatus())) {
                conditionsBuilder.eq("orderStatus", "30");
                conditionsBuilder.eq("hadCheck", "0");
            } else if ("40".equals(request.getOrderStatus())) {
                conditionsBuilder.eq("orderStatus", "40");
            }
        } else {
            conditionsBuilder.notIn("orderStatus", orderStatuss);
        }
        if (StringUtils.isNotNull(request.getCompanyId())) {
            conditionsBuilder.eq("companyId", request.getCompanyId());
        } else {
            conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());
        }
        if (StringUtils.isNotNull(request.getSpId())) {
            conditionsBuilder.like("spId", request.getSpId().toString());
        }
        if (StringUtils.isNotNull(request.getWarehouseId())) {
            conditionsBuilder.eq("warehouseId", request.getWarehouseId());
        }
        Map<String, Object> paramMap = conditionsBuilder.build();
        paramMap.put("orderBy", " doh_id DESC");
        Pagination page = (Pagination) wmsDoHeaderService.findAll(pagination, paramMap);
        DoHeaderPageQueryResponse response = new DoHeaderPageQueryResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 取消出库任务
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public DoCudBaseResponse cancelDoHeader(DoHeaderCancelRequest request) {
        WmsDoHeader wmsDoHeaderEntity = DataConvertUtil.parseDataAsObject(request, WmsDoHeader.class);
        OmsShipPlan omsShipPlan = new OmsShipPlan();
        omsShipPlan.setId(request.getSpId());
        //默认审核失败状态
        omsShipPlan.setOrderStatus("92");
        //获取锁标识
        String lockKey = String.valueOf(request.getSpId());
        Object lockValue = tryLock(lockKey);
        WmsDoHeader wmsDoHeader = wmsDoHeaderService.selectOneByEntity(wmsDoHeaderEntity);
        if (Objects.isNull(wmsDoHeader)) {
            //待同步状态
            omsShipPlan.setOrderStatus("90");
        }
        if (DELETE_STATUS.equals(wmsDoHeader.getOrderStatus())) {
            //已撤销状态
            omsShipPlan.setOrderStatus("91");
        }
        if ("0".equals(wmsDoHeader.getHasPlo())) {
            // 0 没有拣货单,1 已经生成拣货单
            //确认锁
            if (confirmLock(lockKey, lockValue)) {
                //取消出库任务
                WmsDoHeader updateWmsDoHeader = new WmsDoHeader();
                updateWmsDoHeader.setId(wmsDoHeader.getId());
                updateWmsDoHeader.setOrderStatus(DELETE_STATUS);
                wmsDoHeaderService.updateByPrimaryKeySelective(updateWmsDoHeader);
                //已撤销状态
                omsShipPlan.setOrderStatus("91");
            }
        }
        //释放锁
        cancelLock(lockKey, lockValue);
        //添加数据交互指令
        WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add(wmsOmsShipPlanCancelCallbackProducer.getQueueName(), "", omsShipPlan);
        //发送消息到队列
        wmsOmsShipPlanCancelCallbackProducer.send(wmsIdInstantdirective);
        DoCudBaseResponse response = new DoCudBaseResponse();
        return response;
    }

    /**
     * 核验
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public DoCudBaseResponse doVerification(DoHeaderUpdateRequest request) {
        WmsDoHeader wmsDoHeaderIsExist = wmsDoHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsDoHeaderIsExist)) {
            ErrorCode.DO_NOT_EXIST_4001.throwError();
        }
        WmsDoHeader wmsDoHeader = DataConvertUtil.parseDataAsObject(request, WmsDoHeader.class);
        wmsDoHeader.setHadCheck("1");
        wmsDoHeader.setLastupdateTime(DateUtils.nowWithUTC());
        int updateResult = wmsDoHeaderService.updateByPrimaryKeySelective(wmsDoHeader);
        DoCudBaseResponse response = new DoCudBaseResponse();
        response.setIsSuccess(updateResult > 0);
        return response;
    }

//    /**
//     * 出库
//     *
//     * @param request
//     * @return
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public DoCudBaseResponse doShip(DoHeaderUpdateRequest request) {
//        log.debug("*************doShip" + JSON.toJSONString(request));
//        WmsDoHeader wmsDoHeaderInfo = wmsDoHeaderService.selectByPrimaryKey(request.getId());
//        if (Objects.isNull(wmsDoHeaderInfo)) {
//            ErrorCode.DO_NOT_EXIST_4001.throwError();
//        }
//        if ("03".equals(wmsDoHeaderInfo.getOrderType())) {
//            //内部供应商自动创建的单据类型
//            String poId = wmsDoHeaderService.findPoId(wmsDoHeaderInfo.getId());
//            if (StringUtils.isEmpty(poId)) {
//                //未找到关联的单据id
//                ErrorCode.DO_ERROR_4002.throwError();
//            }
//            RecovicePlanAddByDoRequest recovicePlanAddByDoRequest = new RecovicePlanAddByDoRequest();
//            recovicePlanAddByDoRequest.setPoId(Long.valueOf(poId));
//            log.debug("*************recovicePlanAddByDoRequest" + JSON.toJSONString(recovicePlanAddByDoRequest));
//            //添加数据交互指令
//            WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add(wmsOmsRecovicePlanAddByDoProducer.getQueueName(), "", recovicePlanAddByDoRequest);
//            //发送消息到队列
//            wmsOmsRecovicePlanAddByDoProducer.send(wmsIdInstantdirective);
//        }
//        WmsDoHeader wmsDoHeader = DataConvertUtil.parseDataAsObject(request, WmsDoHeader.class);
//        wmsDoHeader.setLastupdateTime(DateUtils.nowWithUTC());
//        int updateResult = wmsDoHeaderService.updateByPrimaryKeySelective(wmsDoHeader);
//        /**
//         * 扣减库存，回写上游单据【前端调用另一个接口统一处理】
//         */
//        DoCudBaseResponse response = new DoCudBaseResponse();
//        response.setIsSuccess(updateResult > 0);
//        return response;
//    }

}
