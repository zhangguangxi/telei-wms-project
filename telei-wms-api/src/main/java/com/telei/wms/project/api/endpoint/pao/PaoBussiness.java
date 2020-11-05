package com.telei.wms.project.api.endpoint.pao;

import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.businessNumber.BusinessNumberFeignClient;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberRequest;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberResponse;
import com.telei.wms.datasource.wms.model.WmsPaoHeader;
import com.telei.wms.datasource.wms.model.WmsPaoLine;
import com.telei.wms.datasource.wms.model.WmsRooHeader;
import com.telei.wms.datasource.wms.model.WmsRooLine;
import com.telei.wms.datasource.wms.service.*;
import com.telei.wms.datasource.wms.vo.InventoryLocationResponseVo;
import com.telei.wms.datasource.wms.vo.PaoHeaderPageQueryRequestVo;
import com.telei.wms.datasource.wms.vo.PaoLinePageQueryResponseVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.pao.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.telei.infrastructure.component.commons.utils.LockMapUtil.*;

/**
 * @Description: 上架
 * @Auther: Dean
 * @Date: 2020/8/31 9:25
 */
@Service
public class PaoBussiness {

    //删除
    private static final String DELETE_STATUS = "99";
    //保存
    private static final String SAVE_STATUS = "01";
    //上架
    private static final String PUTAWAY_STATUS = "20";

    @Autowired
    private Id idGenerator;

    @Autowired
    private WmsPaoHeaderService wmsPaoHeaderService;

    @Autowired
    private WmsPaoLineService wmsPaoLineService;

    @Autowired
    private WmsRooHeaderService wmsRooHeaderService;

    @Autowired
    private WmsRooLineService wmsRooLineService;

    @Autowired
    private BusinessNumberFeignClient businessNumberFeignClient;

    @Autowired
    private WmsInventoryService wmsInventoryService;

    /**
     * 新增
     * @param request
     * @return   */
    @Transactional(rollbackFor = Exception.class)
    public PaoCudBaseResponse addPao(PaoHeaderAddRequest request) {
        WmsRooHeader wmsRooHeaderInfo = wmsRooHeaderService.selectByPrimaryKey(request.getRooId());
        if (Objects.isNull(wmsRooHeaderInfo)) {
            //收货单不存在
            ErrorCode.PAO_NOT_EXIST_4004.throwError();
        }
        WmsPaoHeader wmsPaoHeader = DataConvertUtil.parseDataAsObject(request, WmsPaoHeader.class);
        List<WmsPaoLine> wmsPaoLines = DataConvertUtil.parseDataAsArray(request.getPaoLines(), WmsPaoLine.class);
        //收货单明细数量
        Map<Long, BigDecimal> rooLineQty = new HashMap<>();
        wmsPaoHeader.setId(idGenerator.getUnique());
        wmsPaoHeader.setRoId(wmsRooHeaderInfo.getRoId());
        wmsPaoHeader.setRoCode(wmsRooHeaderInfo.getRoCode());
        wmsPaoHeader.setRooId(wmsRooHeaderInfo.getId());
        wmsPaoHeader.setRooCode(wmsRooHeaderInfo.getRooCode());
        wmsPaoHeader.setWarehouseId(wmsRooHeaderInfo.getWarehouseId());
        wmsPaoHeader.setWarehouseCode(wmsRooHeaderInfo.getWarehouseCode());
        wmsPaoHeader.setRecvTime(wmsRooHeaderInfo.getRecvTime());
        wmsPaoHeader.setRecvUser(wmsRooHeaderInfo.getRecvUser());
        wmsPaoHeader.setOrderType("20");
        wmsPaoHeader.setPaoStatus(SAVE_STATUS);
        wmsPaoHeader.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        wmsPaoHeader.setCreateUser(CustomRequestContext.getUserInfo().getUserName());
        wmsPaoHeader.setCreateTime(DateUtils.nowWithUTC());
        wmsPaoHeader.setLastupdateUser(CustomRequestContext.getUserInfo().getUserName());
        wmsPaoHeader.setLastupdateTime(DateUtils.nowWithUTC());
        //总数量
        BigDecimal totalQty = BigDecimal.ZERO;
        //总重量
        BigDecimal totalWeight = BigDecimal.ZERO;
        //总净重
        BigDecimal totalNetWeight = BigDecimal.ZERO;
        //总体积
        BigDecimal totalVol = BigDecimal.ZERO;
        for (WmsPaoLine wmsPaoLine : wmsPaoLines) {
            wmsPaoLine.setId(idGenerator.getUnique());
            wmsPaoLine.setPaoId(wmsPaoHeader.getId());
            wmsPaoLine.setRoId(wmsPaoHeader.getRoId());
            wmsPaoLine.setRooId(wmsPaoHeader.getRooId());
            wmsPaoLine.setRooCode(wmsPaoHeader.getRooCode());
            wmsPaoLine.setPaoStatus(SAVE_STATUS);
            wmsPaoLine.setPrepLcCode("");
            wmsPaoLine.setLcCode("");
            wmsPaoLine.setPaolFifoTime(DateUtils.nowWithUTC());
            //累加总数
            totalQty = totalQty.add(wmsPaoLine.getPaolQty());
            totalWeight = totalWeight.add(wmsPaoLine.getLineTotalWeight());
            totalNetWeight = totalNetWeight.add(wmsPaoLine.getLineNetWeight());
            totalVol = totalVol.add(wmsPaoLine.getLineTotalVol());
            rooLineQty.put(wmsPaoLine.getRoolId(), wmsPaoLine.getPaolQty());
        }
        wmsPaoHeader.setTotalQty(totalQty);
        wmsPaoHeader.setTotalWeight(totalWeight);
        wmsPaoHeader.setTotalNetWeight(totalNetWeight);
        wmsPaoHeader.setTotalVol(totalVol);
        //获取锁标识
        String lockKey = String.valueOf(request.getRooId());
        Object lockValue = tryLock(lockKey);
        try {
            //获取收货单信息
            WmsRooHeader wmsRooHeader = wmsRooHeaderInfo;
            WmsRooHeader updateWmsRooHeader = new WmsRooHeader();
            updateWmsRooHeader.setId(wmsRooHeader.getId());
            updateWmsRooHeader.setRoStatus("30");
            updateWmsRooHeader.setTmpPutawayQty(wmsRooHeader.getTmpPutawayQty().add(totalQty));
            if (updateWmsRooHeader.getTmpPutawayQty().compareTo(wmsRooHeader.getTotalQty()) == 1) {
                //上架数大于收货数
                ErrorCode.PAO_ADD_ERROR_4002.throwError();
            }
            //获取收货单明细
            WmsRooLine wmsRooLineEntity = new WmsRooLine();
            wmsRooLineEntity.setRooId(request.getRooId());
            List<WmsRooLine> wmsRooLines = wmsRooLineService.selectByEntity(wmsRooLineEntity);
            List<WmsRooLine> updateWmsRooLines = new ArrayList<>();
            for (WmsRooLine wmsRooLine : wmsRooLines) {
                BigDecimal temPutawayQty = rooLineQty.get(wmsRooLine.getId());
                if (Objects.isNull(temPutawayQty)) {
                    continue;
                }
                WmsRooLine updateWmsRooLine = new WmsRooLine();
                updateWmsRooLine.setId(wmsRooLine.getId());
                updateWmsRooLine.setTmpPutawayQty(wmsRooLine.getTmpPutawayQty().add(temPutawayQty));
                updateWmsRooLines.add(updateWmsRooLine);
            }
            //确认锁
            if (confirmLock(lockKey, lockValue)) {
                //获取业务单号
                BusinessNumberRequest businessNumberRequest = new BusinessNumberRequest();
                businessNumberRequest.setType("SJD");
                ApiResponse apiResponse = businessNumberFeignClient.get(businessNumberRequest);
                BusinessNumberResponse businessNumberResponse = apiResponse.convertDataToObject(BusinessNumberResponse.class);
                if (StringUtils.isEmpty(businessNumberResponse.getBusinessNumber())) {
                    //未获取到业务单号
                    ErrorCode.PAO_ADD_ERROR_4002.throwError();
                }
                wmsPaoHeader.setPaoCode(businessNumberResponse.getBusinessNumber());
                //保存上架单
                wmsPaoHeaderService.insertSelective(wmsPaoHeader);
                if (! wmsPaoLines.isEmpty()) {
                    //生成推荐库位
                    makeLineLocation(wmsPaoHeader.getCompanyId(), wmsPaoHeader.getWarehouseId(), wmsPaoLines);
                    //保存上架单明细
                    wmsPaoLineService.insertBatch(wmsPaoLines);
                }
                //更新收货单收货数
                wmsRooHeaderService.updateByPrimaryKeySelective(updateWmsRooHeader);
                if (! updateWmsRooLines.isEmpty()) {
                    //更新收货单明细
                    wmsRooLineService.updateBatch(updateWmsRooLines);
                }
            } else {
                ErrorCode.PAO_ADD_ERROR_4002.throwError();
            }
        } finally {
            //释放锁
            cancelLock(lockKey, lockValue);
        }
        PaoCudBaseResponse response = new PaoCudBaseResponse();
        return response;
    }

    /**
     * 生成推荐库位
     * @param warehouseId
     * @param wmsPaoLines
     */
    private void makeLineLocation(Long companyId, Long warehouseId, List<WmsPaoLine> wmsPaoLines) {
        List<Long> productIds = wmsPaoLines.stream().map(WmsPaoLine::getProductId).collect(Collectors.toList());
        //从库存获取
        List<InventoryLocationResponseVo> locationResponseVos = wmsInventoryService.findLocationAll(companyId, warehouseId, productIds);
        Map<Long, String> paoLineLocation = new HashMap<>();
        for (InventoryLocationResponseVo locationResponseVo : locationResponseVos) {
            paoLineLocation.put(locationResponseVo.getProductId(), locationResponseVo.getLcCode());
        }
        //需要去历史库存获取的产品
        List<Long> historyProductIds = new ArrayList<>();
        for (WmsPaoLine wmsPaoLine : wmsPaoLines) {
            String lcCode = paoLineLocation.get(wmsPaoLine.getProductId());
            if (Objects.isNull(lcCode)) {
                historyProductIds.add(wmsPaoLine.getProductId());
            } else {
                wmsPaoLine.setPrepLcCode(lcCode);
            }
        }
        if (! historyProductIds.isEmpty()) {
            //从历史库存获取
            List<InventoryLocationResponseVo> historyLocationResponseVos = wmsInventoryService.findHistoryLocationAll(companyId, warehouseId, historyProductIds);
            if (historyLocationResponseVos.isEmpty()) {
                return;
            }
            Map<Long, List<String>> historyLocationMap = new HashMap<>();
            List<String> lcCodeAll = new ArrayList<>();
            for (InventoryLocationResponseVo locationResponseVo : historyLocationResponseVos) {
                List<String> lcCodes = historyLocationMap.get(locationResponseVo.getProductId());
                if (Objects.isNull(lcCodes)) {
                    lcCodes = new ArrayList<>();
                }
                lcCodes.add(locationResponseVo.getLcCode());
                lcCodeAll.add(locationResponseVo.getLcCode());
                historyLocationMap.put(locationResponseVo.getProductId(), lcCodes);
            }
            Map<String, Long> existLocationMap = new HashMap<>();
            //获取已经存在的库位
            List<InventoryLocationResponseVo> existLocationAll = wmsInventoryService.findExistLocationByLcCode(lcCodeAll);
            for (InventoryLocationResponseVo locationResponseVo : existLocationAll) {
                existLocationMap.put(locationResponseVo.getLcCode(), locationResponseVo.getProductId());
            }
            for (WmsPaoLine wmsPaoLine : wmsPaoLines) {
                if (! Objects.isNull(wmsPaoLine.getLcCode())) {
                    continue;
                }
                List<String> lcCodes = historyLocationMap.get(wmsPaoLine.getProductId());
                for (String lcCode : lcCodes) {
                    if (Objects.isNull(existLocationMap.get(lcCode))) {
                        wmsPaoLine.setPrepLcCode(lcCode);
                        existLocationMap.put(lcCode, wmsPaoLine.getProductId());
                        break;
                    }
                }
            }
        }
    }

    /**
     * 订单详细
     * @param request
     * @return
     */
    public PaoHeaderDetailResponse paoDetail(PaoDetailRequest request) {
        WmsPaoHeader wmsPaoHeader = wmsPaoHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsPaoHeader)) {
            //入库任务主单不存在
            ErrorCode.PAO_NOT_EXIST_4001.throwError();
        }
        PaoHeaderDetailResponse response = DataConvertUtil.parseDataAsObject(wmsPaoHeader, PaoHeaderDetailResponse.class);
        List<PaoLinePageQueryResponseVo> paoLinePageQueryResponseVos = wmsPaoLineService.findAll(request.getId(), wmsPaoHeader.getCompanyId());
        List<PaoLineDetailResponse> paoLineDetailResponses = DataConvertUtil.parseDataAsArray(paoLinePageQueryResponseVos, PaoLineDetailResponse.class);
        response.setPaoLines(paoLineDetailResponses);
        return response;
    }

    /**
     * 分页查询主单
     * @param request
     * @return
     */
    public PaoHeaderPageQueryResponse pageQueryRoHeader(PaoHeaderPageQueryRequest request) {
        PaoHeaderPageQueryRequestVo requestVo = DataConvertUtil.parseDataAsObject(request, PaoHeaderPageQueryRequestVo.class);
        requestVo.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        Pagination page = (Pagination) wmsPaoHeaderService.findAll(pagination, requestVo);
        PaoHeaderPageQueryResponse response = new PaoHeaderPageQueryResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 更新主单信息
     * @param request
     * @return
     */
    public PaoCudBaseResponse updateRoHeader(PaoHeaderUpdateRequest request) {
        WmsPaoHeader wmsPaoHeaderIsExist = wmsPaoHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsPaoHeaderIsExist)) {
            ErrorCode.PAO_NOT_EXIST_4001.throwError();
        }
        WmsPaoHeader wmsPaoHeader = DataConvertUtil.parseDataAsObject(request, WmsPaoHeader.class);
        int updateResult = wmsPaoHeaderService.updateByPrimaryKeySelective(wmsPaoHeader);
        PaoCudBaseResponse response = new PaoCudBaseResponse();
        response.setIsSuccess(updateResult > 0);
        return response;
    }

    /**
     * 取消上架
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public PaoCudBaseResponse cancelPaoHeader(PaoHeaderCancelRequest request) {
        WmsPaoHeader wmsPaoHeaderInfo = wmsPaoHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsPaoHeaderInfo)) {
            ErrorCode.PAO_NOT_EXIST_4001.throwError();
        }
        PaoCudBaseResponse response = new PaoCudBaseResponse();
        if (! SAVE_STATUS.equals(wmsPaoHeaderInfo.getPaoStatus())) {
            response.setIsSuccess(false);
            return response;
        }
        //获取锁标识
        String lockKey = String.valueOf(wmsPaoHeaderInfo.getRooId());
        Object lockValue = tryLock(lockKey);
        try {
            //获取上架单明细
            WmsPaoLine wmsPaoLineEntity = new WmsPaoLine();
            wmsPaoLineEntity.setPaoId(request.getId());
            List<WmsPaoLine> wmsPaoLines = wmsPaoLineService.selectByEntity(wmsPaoLineEntity);
            //上架单明细数量
            Map<Long, BigDecimal> paoLineQty = new HashMap<>();
            for (WmsPaoLine wmsPaoLine : wmsPaoLines) {
                paoLineQty.put(wmsPaoLine.getRoolId(), wmsPaoLine.getPaolQty());
            }
            //获取收货单信息
            WmsRooHeader wmsRooHeader = wmsRooHeaderService.selectByPrimaryKey(wmsPaoHeaderInfo.getRooId());
            WmsRooHeader updateWmsRooHeader = new WmsRooHeader();
            updateWmsRooHeader.setId(wmsRooHeader.getId());
            updateWmsRooHeader.setTmpPutawayQty(wmsRooHeader.getTmpPutawayQty().subtract(wmsPaoHeaderInfo.getTotalQty()));
            updateWmsRooHeader.setRoStatus("20");
            //获取收货单明细
            WmsRooLine wmsRooLineEntity = new WmsRooLine();
            wmsRooLineEntity.setRooId(wmsPaoHeaderInfo.getRooId());
            List<WmsRooLine> wmsRooLines = wmsRooLineService.selectByEntity(wmsRooLineEntity);
            List<WmsRooLine> updateWmsRooLines = new ArrayList<>();
            for (WmsRooLine wmsRooLine : wmsRooLines) {
                BigDecimal temPutawayQty = paoLineQty.get(wmsRooLine.getId());
                if (Objects.isNull(temPutawayQty)) {
                    continue;
                }
                WmsRooLine updateWmsRooLine = new WmsRooLine();
                updateWmsRooLine.setId(wmsRooLine.getId());
                updateWmsRooLine.setTmpPutawayQty(wmsRooLine.getTmpPutawayQty().subtract(temPutawayQty));
                updateWmsRooLines.add(updateWmsRooLine);
            }
            //获取上架单撤销状态
            WmsPaoHeader wmsPaoHeader = DataConvertUtil.parseDataAsObject(request, WmsPaoHeader.class);
            wmsPaoHeader.setPaoStatus(DELETE_STATUS);
            wmsPaoHeader.setLastupdateUser(CustomRequestContext.getUserInfo().getUserName());
            wmsPaoHeader.setLastupdateTime(DateUtils.nowWithUTC());
            //确认锁
            if (confirmLock(lockKey, lockValue)) {
                //更新收货单收货数
                wmsRooHeaderService.updateByPrimaryKeySelective(updateWmsRooHeader);
                if (! updateWmsRooLines.isEmpty()) {
                    //更新收货单明细
                    wmsRooLineService.updateBatch(updateWmsRooLines);
                }
                //更新上架单撤销状态
                int updateResult = wmsPaoHeaderService.updateByPrimaryKeySelective(wmsPaoHeader);
                response.setIsSuccess(updateResult > 0);
            } else {
                ErrorCode.PAO_CANCEL_ERROR_4003.throwError();
            }
        } finally {
            //释放锁
            cancelLock(lockKey, lockValue);
        }
        return response;
    }
}
