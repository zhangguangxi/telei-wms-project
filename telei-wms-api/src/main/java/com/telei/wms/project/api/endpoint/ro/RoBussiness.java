package com.telei.wms.project.api.endpoint.ro;

import com.nuochen.framework.autocoding.domain.Pagination;
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
import com.telei.wms.project.api.endpoint.ro.dto.*;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.WmsIdInstantdirectiveBussiness;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.telei.infrastructure.component.commons.utils.LockMapUtil.*;

/**
 * @Description: 入库任务
 * @Auther: Dean
 * @Date: 2020/8/19 17:05
 */
@Service
public class RoBussiness {

    //删除
    private static final String DELETE_STATUS = "99";

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

    /**
     * 新增
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public RoCudBaseResponse addRo(RoHeaderAddRequest request) {
        WmsRoHeader wmsRoHeader = DataConvertUtil.parseDataAsObject(request, WmsRoHeader.class);
        List<WmsRoLine> wmsRoLines = DataConvertUtil.parseDataAsArray(request.getRoLines(), WmsRoLine.class);
        wmsRoHeader.setCreateTime(DateUtils.nowWithUTC());
        wmsRoHeaderService.insertSelective(wmsRoHeader);
        wmsRoLineService.insertBatch(wmsRoLines);
        RoCudBaseResponse response = new RoCudBaseResponse();
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
        List<RoLinePageQueryResponseVo> roLinePageQueryResponseVos = wmsRoLineService.findAll(request.getId());
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
        RoHeaderPageQueryRequestVo roHeaderPageQueryRequestVo = DataConvertUtil.parseDataAsObject(request, RoHeaderPageQueryRequestVo.class);
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        Pagination page = (Pagination) wmsRoHeaderService.findAll(pagination, roHeaderPageQueryRequestVo);
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
        WmsRoHeader wmsRoHeaderIsExist = wmsRoHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsRoHeaderIsExist)) {
            ErrorCode.RO_NOT_EXIST_4001.throwError();
        }
        WmsRoHeader wmsRoHeader = DataConvertUtil.parseDataAsObject(request, WmsRoHeader.class);
        int updateResult = wmsRoHeaderService.updateByPrimaryKeySelective(wmsRoHeader);
        RoCudBaseResponse response = new RoCudBaseResponse();
        response.setIsSuccess(updateResult > 0);
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
        //默认审核状态
        omsRecovicePlan.setOrderStatus("10");
        //获取锁标识
        String lockKey = String.valueOf(request.getRpId());
        Object lockValue = tryLock(lockKey);
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
                wmsRoHeaderService.updateByPrimaryKey(updateWmsRoHeader);
                //已撤销状态
                omsRecovicePlan.setOrderStatus("91");
            }
        }
        //释放锁
        cancelLock(lockKey, lockValue);
        //添加数据交互指令
        WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add("PUTON", "", omsRecovicePlan);
        //发送消息到队列
        wmsOmsRecovicePlanCancelCallbackProducer.send(wmsIdInstantdirective);
        RoCudBaseResponse response = new RoCudBaseResponse();
        return response;
    }

    /**
     * 导出入库任务明细
     */
    public void exportRoLine() {

    }
}
