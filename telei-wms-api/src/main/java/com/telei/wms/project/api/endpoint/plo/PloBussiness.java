package com.telei.wms.project.api.endpoint.plo;

import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.telei.infrastructure.component.commons.utils.DateUtils;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.businessNumber.BusinessNumberFeignClient;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberRequest;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberResponse;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.*;
import com.telei.wms.datasource.wms.vo.PloHeaderPageQueryRequestVo;
import com.telei.wms.datasource.wms.vo.PloLineLocationResponseVo;
import com.telei.wms.datasource.wms.vo.PloLinePageQueryResponseVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.plo.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 拣货单
 * @Auther: Dean
 * @Date: 2020/9/15 10:20
 */
@Service
public class PloBussiness {

    @Autowired
    private Id idGenerator;

    @Autowired
    private BusinessNumberFeignClient businessNumberFeignClient;

    @Autowired
    private WmsPloHeaderService wmsPloHeaderService;

    @Autowired
    private WmsPloLineService wmsPloLineService;

    @Autowired
    private WmsPloDetailService wmsPloDetailService;

    @Autowired
    private WmsDoHeaderService wmsDoHeaderService;

    @Autowired
    private WmsDoLineService wmsDoLineService;

    /**
     * 新增
     * @param request
     * @return   */
    @Transactional(rollbackFor = Exception.class)
    public PloCudBaseResponse addPlo(PloHeaderAddRequest request) {
        WmsDoHeader wmsDoHeader = wmsDoHeaderService.selectByPrimaryKey(request.getDohId());
        if (Objects.isNull(wmsDoHeader)) {
            //出库任务不存在
            ErrorCode.PLO_NOT_EXIST_4002.throwError();
        }
        if ("1".equals(wmsDoHeader.getHasPlo())) {
            //不能重复生成拣货单
            ErrorCode.PLO_ADD_ERROR_4003.throwError();
        }
        WmsPloHeader wmsPloHeader = new WmsPloHeader();
        wmsPloHeader.setId(idGenerator.getUnique());
        //获取业务单号
        BusinessNumberRequest businessNumberRequest = new BusinessNumberRequest();
        businessNumberRequest.setType("WMS");
        ApiResponse apiResponse = businessNumberFeignClient.get(businessNumberRequest);
        BusinessNumberResponse businessNumberResponse = apiResponse.convertDataToObject(BusinessNumberResponse.class);
        if (StringUtils.isEmpty(businessNumberResponse.getBusinessNumber())) {
            //未获取到业务单号
            ErrorCode.PLO_ADD_ERROR_4004.throwError();
        }
        wmsPloHeader.setPloCode(businessNumberResponse.getBusinessNumber());
        wmsPloHeader.setDohId(wmsDoHeader.getId());
        wmsPloHeader.setDohCode(wmsDoHeader.getDohCode());
        wmsPloHeader.setCompanyId(wmsDoHeader.getCompanyId());
        wmsPloHeader.setWarehouseId(wmsDoHeader.getWarehouseId());
        wmsPloHeader.setWarehouseCode(wmsDoHeader.getWarehouseCode());
        wmsPloHeader.setCustOrderNo(wmsDoHeader.getCustOrderNo());
        wmsPloHeader.setSupplierId(wmsDoHeader.getSupplierId());
        wmsPloHeader.setCustomerId(wmsDoHeader.getCustomerId());
        wmsPloHeader.setOrderStatus("01");
        wmsPloHeader.setTotalQty(wmsDoHeader.getTotalQty());
        wmsPloHeader.setTotalWeight(wmsDoHeader.getTotalWeight());
        wmsPloHeader.setTotalVol(wmsDoHeader.getTotalVol());
        wmsPloHeader.setDetailedSpeciesQty(wmsDoHeader.getDetailedSpeciesQty());
        wmsPloHeader.setCreateUser("");
        wmsPloHeader.setCreateTime(DateUtils.nowWithUTC());
        wmsPloHeader.setLastUpdateUser("");
        wmsPloHeader.setLastUpdateTime(DateUtils.nowWithUTC());
        WmsDoLine wmsDoLineEntity = new WmsDoLine();
        wmsDoLineEntity.setDohId(wmsDoHeader.getId());
        List<WmsDoLine> wmsDoLines = wmsDoLineService.selectByEntity(wmsDoLineEntity);
        List<WmsPloLine> wmsPloLines = DataConvertUtil.parseDataAsArray(wmsDoLines, WmsPloLine.class);
        for (WmsPloLine wmsPloLine : wmsPloLines) {
            wmsPloLine.setDolId(wmsPloLine.getId());
            wmsPloLine.setId(idGenerator.getUnique());
            wmsPloLine.setPloId(wmsPloHeader.getId());
            wmsPloLine.setPloCode(wmsPloHeader.getPloCode());
        }
        //新增拣货单
        wmsPloHeaderService.insertSelective(wmsPloHeader);
        if (! wmsPloLines.isEmpty()) {
            //获取库位
            getLocation(wmsPloHeader.getWarehouseId(), wmsPloLines);
            //新增拣货单详情
            wmsPloLineService.insertBatch(wmsPloLines);
        }
        //更新出库任务
        WmsDoHeader updateWmsDoHeader = new WmsDoHeader();
        updateWmsDoHeader.setId(wmsPloHeader.getId());
        updateWmsDoHeader.setOrderStatus("20");
        updateWmsDoHeader.setHasPlo("1");
        wmsDoHeaderService.updateByPrimaryKey(updateWmsDoHeader);
        PloCudBaseResponse response = new PloCudBaseResponse();
        return response;
    }

    /**
     * 获取库位
     * @param warehouseId
     * @param wmsPloLines
     */
    private void getLocation(Long warehouseId, List<WmsPloLine> wmsPloLines) {
        List<Long> productIds = wmsPloLines.stream().map(WmsPloLine::getProductId).collect(Collectors.toList());
        //获取货位集合
        List<PloLineLocationResponseVo> ploLineLocationResponseVos = wmsPloLineService.findLocationAll(warehouseId, productIds);
        Map<Long, PloLineLocationResponseVo> paoLineLocation = new HashMap<>();
        for (PloLineLocationResponseVo ploLineLocationResponseVo : ploLineLocationResponseVos) {
            paoLineLocation.put(ploLineLocationResponseVo.getProductId(), ploLineLocationResponseVo);
        }
        for (WmsPloLine wmsPloLine : wmsPloLines) {
            PloLineLocationResponseVo ploLineLocationResponseVo = paoLineLocation.get(wmsPloLine.getProductId());
            if (Objects.isNull(ploLineLocationResponseVo)) {
                continue;
            }
            wmsPloLine.setLcCode(ploLineLocationResponseVo.getLcCode());
            wmsPloLine.setLcAisle(ploLineLocationResponseVo.getLcAisle());
            wmsPloLine.setLcX(ploLineLocationResponseVo.getLcX());
            wmsPloLine.setLcY(ploLineLocationResponseVo.getLcY());
            wmsPloLine.setLcZ(ploLineLocationResponseVo.getLcZ());
        }
    }

    /**
     * 订单详情
     * @param request
     * @return
     */
    public PloHeaderResponse ploHeaderDetail(PloLineRequest request) {
        WmsPloHeader wmsPloHeader = wmsPloHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsPloHeader)) {
            //主单不存在
            ErrorCode.PLO_NOT_EXIST_4001.throwError();
        }
        PloHeaderResponse response = DataConvertUtil.parseDataAsObject(wmsPloHeader, PloHeaderResponse.class);
        List<PloLinePageQueryResponseVo> ploLinePageQueryResponseVos = wmsPloLineService.findAll(request.getId());
        List<PloLineResponse> ploLineResponses = DataConvertUtil.parseDataAsArray(ploLinePageQueryResponseVos, PloLineResponse.class);
        response.setPloLines(ploLineResponses);
        return response;
    }

    /**
     * 分页查询主单
     * @param request
     * @return
     */
    public PloHeaderPageQueryResponse pageQueryPloHeader(PloHeaderPageQueryRequest request) {
        PloHeaderPageQueryRequestVo ploHeaderPageQueryRequestVo = DataConvertUtil.parseDataAsObject(request, PloHeaderPageQueryRequestVo.class);
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        Pagination page = (Pagination) wmsPloHeaderService.findAll(pagination, ploHeaderPageQueryRequestVo);
        PloHeaderPageQueryResponse response = new PloHeaderPageQueryResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 新增拣货详情
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public PloCudBaseResponse addPloDetail(List<PloDetailAddRequest> request) {
        List<Long> plolIds = new ArrayList<>();
        Long ploId = null;
        Map<Long, PloDetailAddRequest> ploDetailAddRequestMap = new HashMap<>();
        for (PloDetailAddRequest ploDetailAddRequest : request) {
            ploId = ploDetailAddRequest.getPloId();
            plolIds.add(ploDetailAddRequest.getPlolId());
            ploDetailAddRequestMap.put(ploDetailAddRequest.getPlolId(), ploDetailAddRequest);
        }
        WmsPloHeader wmsPloHeader = wmsPloHeaderService.selectByPrimaryKey(ploId);
        if (Objects.isNull(wmsPloHeader)) {
            //主单不存在
            ErrorCode.PLO_NOT_EXIST_4001.throwError();
        }
        //新增拣货详情集合
        List<WmsPloDetail> insertWmsPloDetails = new ArrayList<>();
        //更新拣货明细集合
        List<WmsPloLine> updateWmsPloLines = new ArrayList<>();
        //总数量
        BigDecimal totalQty = BigDecimal.ZERO;
        //总重量
        BigDecimal totalWeight = BigDecimal.ZERO;
        //总体积
        BigDecimal totalVol = BigDecimal.ZERO;
        List<WmsPloLine> wmsPloLines = wmsPloLineService.selectByPrimaryKeys(plolIds);
        for (WmsPloLine wmsPloLine : wmsPloLines) {
            PloDetailAddRequest ploDetailAddRequest = ploDetailAddRequestMap.get(wmsPloLine.getId());
            if (wmsPloLine.getPickedQty().add(ploDetailAddRequest.getPickQty()).compareTo(wmsPloLine.getQty()) == 1) {
                //超过总数
                ErrorCode.PLO_ADD_DETAIL_ERROR_4005.throwError();
            }
            WmsPloDetail wmsPloDetail = DataConvertUtil.parseDataAsObject(ploDetailAddRequest, WmsPloDetail.class);
            wmsPloDetail.setId(idGenerator.getUnique());
//            wmsPloDetail.setPloId(wmsPloLine.getPloId());
            wmsPloDetail.setPloCode(wmsPloLine.getPloCode());
            wmsPloDetail.setPlolId(wmsPloLine.getId());
            wmsPloDetail.setDolId(wmsPloLine.getDolId());
            wmsPloDetail.setProductId(wmsPloLine.getProductId());
            wmsPloDetail.setProductBarcode(wmsPloLine.getProductBarcode());
            wmsPloDetail.setMediaId(wmsPloLine.getMediaId());
            wmsPloDetail.setProductNo(wmsPloLine.getProductNo());
            wmsPloDetail.setProductName(wmsPloLine.getProductName());
            wmsPloDetail.setProductNameLocal(wmsPloLine.getProductNameLocal());
            wmsPloDetail.setProductUpcCode(wmsPloLine.getProductUpcCode());
            wmsPloDetail.setBrand(wmsPloLine.getBrand());
            wmsPloDetail.setCreateTime(DateUtils.nowWithUTC());
            insertWmsPloDetails.add(wmsPloDetail);
            WmsPloLine updateWmsPloLine = new WmsPloLine();
            updateWmsPloLine.setId(wmsPloLine.getId());
            updateWmsPloLine.setPickedQty(wmsPloLine.getPickedQty().add(ploDetailAddRequest.getPickQty()));
            updateWmsPloLine.setPickedWeight(wmsPloLine.getPickedWeight().add(ploDetailAddRequest.getPickWeight()));
            updateWmsPloLine.setPickedVol(wmsPloLine.getPickedVol().add(ploDetailAddRequest.getPickVol()));
            updateWmsPloLines.add(updateWmsPloLine);
            totalQty = totalQty.add(ploDetailAddRequest.getPickQty());
            totalWeight = totalWeight.add(ploDetailAddRequest.getPickWeight());
            totalVol = totalVol.add(ploDetailAddRequest.getPickVol());
        }
        if (! insertWmsPloDetails.isEmpty()) {
            //新增拣货详细
            wmsPloDetailService.insertBatch(insertWmsPloDetails);
        }
        if (! updateWmsPloLines.isEmpty()) {
            //更新拣货明细
            wmsPloLineService.updateBatch(updateWmsPloLines);
        }
        //更新拣货单头
        WmsPloHeader updateWmsPloHeader = new WmsPloHeader();
        updateWmsPloHeader.setId(ploId);
        updateWmsPloHeader.setOrderStatus("20");
        updateWmsPloHeader.setPickedQty(wmsPloHeader.getPickedQty().add(totalQty));
        updateWmsPloHeader.setPickingWeight(wmsPloHeader.getPickingWeight().add(totalWeight));
        updateWmsPloHeader.setPickingVol(wmsPloHeader.getPickingVol().add(totalVol));
        wmsPloHeaderService.updateByPrimaryKeySelective(updateWmsPloHeader);
        PloCudBaseResponse response = new PloCudBaseResponse();
        return response;
    }

    /**
     * 分页查询拣货详情
     * @param request
     * @return
     */
    public List<PloDetailPageQueryResponse> pageQueryPloDetail(PloDetailPageQueryRequest request) {
        WmsPloDetail wmsPloDetail = new WmsPloDetail();
        wmsPloDetail.setPlolId(request.getPloId());
        List<WmsPloDetail> wmsPloDetails = wmsPloDetailService.selectByEntity(wmsPloDetail);
        return DataConvertUtil.parseDataAsArray(wmsPloDetails, PloDetailPageQueryResponse.class);
    }

    /**
     * 拣货完成
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public PloCudBaseResponse ploFinish(PloFinishRequest request) {
        WmsPloHeader wmsPloHeader = wmsPloHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsPloHeader)) {
            //主单不存在
            ErrorCode.PLO_NOT_EXIST_4001.throwError();
        }
        if ("30".equals(wmsPloHeader.getOrderStatus())) {
            //不能重复提交拣货完毕
            ErrorCode.PLO_FINISH_ERROR_4006.throwError();
        }
        if ("20".equals(wmsPloHeader.getOrderStatus())) {
            //只有提交拣货才能操作拣货完毕
            ErrorCode.PLO_FINISH_ERROR_4007.throwError();
        }
        if (wmsPloHeader.getPickedQty().compareTo(wmsPloHeader.getPickedQty()) == -1) {
            //已拣数小于总数
            ErrorCode.PLO_FINISH_ERROR_4008.throwError();
        }
        //更新拣货单状态
        WmsPloHeader updateWmsPloHeader = new WmsPloHeader();
        updateWmsPloHeader.setId(wmsPloHeader.getId());
        updateWmsPloHeader.setOrderStatus("30");
        updateWmsPloHeader.setLastUpdateUser("");
        updateWmsPloHeader.setLastUpdateTime(DateUtils.nowWithUTC());
        wmsPloHeaderService.updateByPrimaryKeySelective(updateWmsPloHeader);
        //更新出库任务单状态
        WmsDoHeader updateWmsDoHeader = new WmsDoHeader();
        updateWmsDoHeader.setId(wmsPloHeader.getDohId());
        updateWmsDoHeader.setOrderStatus("30");
        updateWmsDoHeader.setLastupdateTime(DateUtils.nowWithUTC());
        wmsDoHeaderService.updateByPrimaryKeySelective(updateWmsDoHeader);
        PloCudBaseResponse response = new PloCudBaseResponse();
        return response;
    }
}
