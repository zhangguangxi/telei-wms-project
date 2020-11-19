package com.telei.wms.project.api.endpoint.plo;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.utils.DateUtils;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.businessNumber.BusinessNumberFeignClient;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberRequest;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberResponse;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.*;
import com.telei.wms.datasource.wms.vo.PloDetailPageQueryResponseVo;
import com.telei.wms.datasource.wms.vo.PloHeaderPageQueryRequestVo;
import com.telei.wms.datasource.wms.vo.PloLineLocationResponseVo;
import com.telei.wms.datasource.wms.vo.PloLinePageQueryResponseVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.ivOut.IvOutBussiness;
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

    //保存
    private static final String SAVE_STATUS = "01";
    //部分拣货
    private static final String PART_PICKING_STATUS = "20";
    //已拣货
    private static final String PICKING_FINISH_STATUS = "30";
    //取消
    private static final String CANCEL_STATUS = "98";

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

    @Autowired
    private WmsIvOutService wmsIvOutService;

    @Autowired
    private WmsInventoryService wmsInventoryService;

    @Autowired
    private IvOutBussiness ivOutBussiness;

    /**
     * 新增
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public PloAddResponse addPlo(PloHeaderAddRequest request) {
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
        businessNumberRequest.setType("JH");
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
        wmsPloHeader.setOrderStatus(SAVE_STATUS);
        wmsPloHeader.setTotalQty(wmsDoHeader.getTotalQty());
        wmsPloHeader.setTotalWeight(wmsDoHeader.getTotalWeight());
        wmsPloHeader.setTotalVol(wmsDoHeader.getTotalVol());
        wmsPloHeader.setDetailedSpeciesQty(wmsDoHeader.getDetailedSpeciesQty());
        wmsPloHeader.setPickedQty(BigDecimal.ZERO);
        wmsPloHeader.setPickingWeight(BigDecimal.ZERO);
        wmsPloHeader.setPickingVol(BigDecimal.ZERO);
        wmsPloHeader.setCreateUser(CustomRequestContext.getUserInfo().getEmployeeName());
        wmsPloHeader.setCreateTime(DateUtils.nowWithUTC());
        wmsPloHeader.setLastUpdateUser(CustomRequestContext.getUserInfo().getEmployeeName());
        wmsPloHeader.setLastUpdateTime(DateUtils.nowWithUTC());
        WmsDoLine wmsDoLineEntity = new WmsDoLine();
        wmsDoLineEntity.setDohId(wmsDoHeader.getId());
        List<WmsDoLine> wmsDoLines = wmsDoLineService.selectByEntity(wmsDoLineEntity);
        List<WmsPloLine> wmsPloLines = DataConvertUtil.parseDataAsArray(wmsDoLines, WmsPloLine.class);
        List<WmsIvOut> wmsIvOuts = new ArrayList<>();
        List<Long> exceptionProductIds = new ArrayList<>();
        for (WmsPloLine wmsPloLine : wmsPloLines) {
            BigDecimal ivOutQty = wmsIvOutService.selectQtySum(wmsPloLine.getProductId(), wmsPloHeader.getWarehouseId(), wmsPloHeader.getCompanyId());
            BigDecimal ivQty = wmsInventoryService.selectQtySum(wmsPloLine.getProductId(), wmsPloHeader.getWarehouseId(), wmsPloHeader.getCompanyId());
            if (wmsPloLine.getQty().add(ivOutQty).compareTo(ivQty) == 1) {
                //拣货数量加待出库数量大于库存数量
//                ErrorCode.PLO_ADD_ERROR_4010.throwError();
                exceptionProductIds.add(wmsPloLine.getProductId());
            }
            wmsPloLine.setDolId(wmsPloLine.getId());
            wmsPloLine.setId(idGenerator.getUnique());
            wmsPloLine.setPloId(wmsPloHeader.getId());
            wmsPloLine.setPloCode(wmsPloHeader.getPloCode());
            wmsPloLine.setPickedQty(BigDecimal.ZERO);
            wmsPloLine.setPickedWeight(BigDecimal.ZERO);
            wmsPloLine.setPickedVol(BigDecimal.ZERO);
            WmsIvOut wmsIvOut = new WmsIvOut();
            wmsIvOut.setId(idGenerator.getUnique());
            wmsIvOut.setCompanyId(wmsPloHeader.getCompanyId());
            wmsIvOut.setWarehouseId(wmsPloHeader.getWarehouseId());
            wmsIvOut.setWarehouseCode(wmsPloHeader.getWarehouseCode());
            wmsIvOut.setOrderId(wmsPloHeader.getDohId());
            wmsIvOut.setOrderCode(wmsPloHeader.getDohCode());
            wmsIvOut.setLineId(wmsPloLine.getDolId());
            wmsIvOut.setProductId(wmsPloLine.getProductId());
            wmsIvOut.setQty(wmsPloLine.getQty());
            wmsIvOuts.add(wmsIvOut);
        }
        if (! exceptionProductIds.isEmpty()) {
            //拣货有库存不够的商品
            ErrorCode.PLO_ADD_ERROR_4010.throwError(JSON.toJSONString(exceptionProductIds));
        }
        if (! wmsIvOuts.isEmpty()) {
            //新增待出库存
            ivOutBussiness.addIvOut(wmsIvOuts);
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
        updateWmsDoHeader.setId(wmsPloHeader.getDohId());
        updateWmsDoHeader.setOrderStatus("20");
        updateWmsDoHeader.setHasPlo("1");
        updateWmsDoHeader.setLastupdateTime(DateUtils.nowWithUTC());
        wmsDoHeaderService.updateByPrimaryKeySelective(updateWmsDoHeader);
        PloAddResponse response = new PloAddResponse();
        response.setId(wmsPloHeader.getId());
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
                //未找到库位
                ErrorCode.PLO_ADD_ERROR_4009.throwError();
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
        PloHeaderPageQueryRequestVo requestVo = DataConvertUtil.parseDataAsObject(request, PloHeaderPageQueryRequestVo.class);
        requestVo.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        Pagination page = (Pagination) wmsPloHeaderService.findAll(pagination, requestVo);
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
            if (Objects.isNull(ploId)) {
                ploId = ploDetailAddRequest.getPloId();
            }
            if (! ploId.equals(ploDetailAddRequest.getPloId())) {
                //出现不同的拣货单
                ErrorCode.PLO_ADD_ERROR_4016.throwError();
            }
            plolIds.add(ploDetailAddRequest.getPlolId());
            ploDetailAddRequestMap.put(ploDetailAddRequest.getPlolId(), ploDetailAddRequest);
        }
        WmsPloHeader wmsPloHeader = wmsPloHeaderService.selectByPrimaryKey(ploId);
        if (Objects.isNull(wmsPloHeader)) {
            //主单不存在
            ErrorCode.PLO_NOT_EXIST_4001.throwError();
        }
        if (! SAVE_STATUS.equals(wmsPloHeader.getOrderStatus()) && ! PART_PICKING_STATUS.equals(wmsPloHeader.getOrderStatus())) {
            //只有保存状态和部分拣货状态才能新增拣货详情
            ErrorCode.PLO_ADD_ERROR_4011.throwError();
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
        if (wmsPloLines.isEmpty()) {
            //拣货明细为空
            ErrorCode.PLO_ADD_ERROR_4017.throwError();
        }
        for (WmsPloLine wmsPloLine : wmsPloLines) {
            PloDetailAddRequest ploDetailAddRequest = ploDetailAddRequestMap.get(wmsPloLine.getId());
            if (wmsPloLine.getPickedQty().add(ploDetailAddRequest.getPickQty()).compareTo(wmsPloLine.getQty()) == 1) {
                //超过总数
                ErrorCode.PLO_ADD_DETAIL_ERROR_4005.throwError();
            }
            WmsPloDetail wmsPloDetail = DataConvertUtil.parseDataAsObject(ploDetailAddRequest, WmsPloDetail.class);
            wmsPloDetail.setId(idGenerator.getUnique());
            wmsPloDetail.setPloId(wmsPloLine.getPloId());
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
        //新增拣货详细
        wmsPloDetailService.insertBatch(insertWmsPloDetails);
        //更新拣货明细
        wmsPloLineService.updateBatch(updateWmsPloLines);
        //更新拣货单头
        WmsPloHeader updateWmsPloHeader = new WmsPloHeader();
        updateWmsPloHeader.setId(wmsPloHeader.getId());
        updateWmsPloHeader.setOrderStatus(PART_PICKING_STATUS);
        updateWmsPloHeader.setPickedQty(wmsPloHeader.getPickedQty().add(totalQty));
        updateWmsPloHeader.setPickingWeight(wmsPloHeader.getPickingWeight().add(totalWeight));
        updateWmsPloHeader.setPickingVol(wmsPloHeader.getPickingVol().add(totalVol));
        wmsPloHeaderService.updateByPrimaryKeySelective(updateWmsPloHeader);
        //更新出库任务
        WmsDoHeader updateWmsDoHeader = new WmsDoHeader();
        updateWmsDoHeader.setId(wmsPloHeader.getDohId());
        updateWmsDoHeader.setOrderStatus("25");
        updateWmsDoHeader.setLastupdateTime(DateUtils.nowWithUTC());
        wmsDoHeaderService.updateByPrimaryKeySelective(updateWmsDoHeader);
        PloCudBaseResponse response = new PloCudBaseResponse();
        return response;
    }

    /**
     * 分页查询拣货详情
     * @param request
     * @return
     */
    public List<PloDetailPageQueryResponse> pageQueryPloDetail(PloDetailPageQueryRequest request) {
        WmsPloHeader wmsPloHeader = wmsPloHeaderService.selectByPrimaryKey(request.getPloId());
        if (Objects.isNull(wmsPloHeader)) {
            //主单不存在
            ErrorCode.PLO_NOT_EXIST_4001.throwError();
        }
        List<PloDetailPageQueryResponseVo> ploDetailPageQueryResponseVos = wmsPloDetailService.findAll(wmsPloHeader.getId());
        return DataConvertUtil.parseDataAsArray(ploDetailPageQueryResponseVos, PloDetailPageQueryResponse.class);
    }

    /**
     * 取消拣货记录
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public PloCudBaseResponse cancelPloDetail(PloDetailCancelRequest request) {
        if (request.getIds().isEmpty()) {
            //拣货记录为空
            ErrorCode.PLO_CANCEL_ERROR_4012.throwError();
        }
        List<WmsPloDetail> wmsPloDetails = wmsPloDetailService.selectByPrimaryKeys(request.getIds());
        if (wmsPloDetails.isEmpty()) {
            //拣货记录为空
            ErrorCode.PLO_CANCEL_ERROR_4012.throwError();
        }
        Long ploId = null;
        List<Long> deleteIds = new ArrayList<>();
        Map<Long, WmsPloDetail> wmsPloDetailMap = new HashMap<>();
        for (WmsPloDetail wmsPloDetail : wmsPloDetails) {
            if (Objects.isNull(ploId)) {
                ploId = wmsPloDetail.getPloId();
            }
            if (! ploId.equals(wmsPloDetail.getPloId())) {
                //出现不同的拣货单
                ErrorCode.PLO_CANCEL_ERROR_4013.throwError();
            }
            deleteIds.add(wmsPloDetail.getId());
            WmsPloDetail updateWmsPloDetail = wmsPloDetailMap.get(wmsPloDetail.getPlolId());
            if (Objects.isNull(updateWmsPloDetail)) {
                updateWmsPloDetail = new WmsPloDetail();
                updateWmsPloDetail.setPickQty(BigDecimal.ZERO);
                updateWmsPloDetail.setPickWeight(BigDecimal.ZERO);
                updateWmsPloDetail.setPickVol(BigDecimal.ZERO);
            }
            updateWmsPloDetail.setPickQty(updateWmsPloDetail.getPickQty().add(wmsPloDetail.getPickQty()));
            updateWmsPloDetail.setPickWeight(updateWmsPloDetail.getPickWeight().add(wmsPloDetail.getPickWeight()));
            updateWmsPloDetail.setPickVol(updateWmsPloDetail.getPickVol().add(wmsPloDetail.getPickVol()));
            wmsPloDetailMap.put(wmsPloDetail.getPlolId(), updateWmsPloDetail);
        }
        WmsPloHeader wmsPloHeader = wmsPloHeaderService.selectByPrimaryKey(ploId);
        if (PICKING_FINISH_STATUS.equals(wmsPloHeader.getOrderStatus())) {
            //已经拣货完成不能取消
            ErrorCode.PLO_CANCEL_ERROR_4018.throwError();
        }
        //总数量
        BigDecimal totalQty = BigDecimal.ZERO;
        //总重量
        BigDecimal totalWeight = BigDecimal.ZERO;
        //总体积
        BigDecimal totalVol = BigDecimal.ZERO;
        //拣货明细更新集合
        List<WmsPloLine> updateWmsPloLines = new ArrayList<>();
        for (Map.Entry<Long, WmsPloDetail> entry : wmsPloDetailMap.entrySet()) {
            Long plolId = entry.getKey();
            WmsPloDetail wmsPloDetail = entry.getValue();
            WmsPloLine wmsPloLine = wmsPloLineService.selectByPrimaryKey(plolId);
            WmsPloLine updateWmsPloLine = new WmsPloLine();
            updateWmsPloLine.setId(wmsPloLine.getId());
            updateWmsPloLine.setPickedQty(wmsPloLine.getPickedQty().subtract(wmsPloDetail.getPickQty()));
            updateWmsPloLine.setPickedWeight(wmsPloLine.getPickedWeight().subtract(wmsPloDetail.getPickWeight()));
            updateWmsPloLine.setPickedVol(wmsPloLine.getPickedVol().subtract(wmsPloDetail.getPickVol()));
            updateWmsPloLines.add(updateWmsPloLine);
            totalQty = totalQty.add(wmsPloDetail.getPickQty());
            totalWeight = totalWeight.add(wmsPloDetail.getPickWeight());
            totalVol = totalVol.add(wmsPloDetail.getPickVol());
        }
        //更新拣货明细
        wmsPloLineService.updateBatch(updateWmsPloLines);
        int deleteResult = wmsPloDetailService.deleteByPrimaryKeys(deleteIds);
        WmsPloDetail wmsPloDetailEntity = new WmsPloDetail();
        wmsPloDetailEntity.setPloId(ploId);
        WmsPloDetail wmsPloDetailIsExist = wmsPloDetailService.selectOneByEntity(wmsPloDetailEntity);
        //更新拣货主单
        WmsPloHeader updateWmsPloHeader = new WmsPloHeader();
        updateWmsPloHeader.setId(wmsPloHeader.getId());
        updateWmsPloHeader.setPickedQty(wmsPloHeader.getPickedQty().subtract(totalQty));
        updateWmsPloHeader.setPickingWeight(wmsPloHeader.getPickingWeight().subtract(totalWeight));
        updateWmsPloHeader.setPickingVol(wmsPloHeader.getPickingVol().subtract(totalVol));
        if (Objects.isNull(wmsPloDetailIsExist)) {
            updateWmsPloHeader.setOrderStatus(SAVE_STATUS);
            //更新出库任务
            WmsDoHeader updateWmsDoHeader = new WmsDoHeader();
            updateWmsDoHeader.setId(wmsPloHeader.getDohId());
            updateWmsDoHeader.setOrderStatus("20");
            updateWmsDoHeader.setLastupdateTime(DateUtils.nowWithUTC());
            wmsDoHeaderService.updateByPrimaryKeySelective(updateWmsDoHeader);
        }
        if (Objects.nonNull(CustomRequestContext.getUserInfo())) {
            updateWmsPloHeader.setLastUpdateUser(CustomRequestContext.getUserInfo().getEmployeeName());
        }
        updateWmsPloHeader.setLastUpdateTime(DateUtils.nowWithUTC());
        wmsPloHeaderService.updateByPrimaryKeySelective(updateWmsPloHeader);
        PloCudBaseResponse response = new PloCudBaseResponse();
        response.setIsSuccess(deleteResult > 0);
        return response;
    }

    /**
     * 取消拣货单
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public PloCudBaseResponse cancelPloHeader(PloHeaderCancelRequest request) {
        WmsPloHeader wmsPloHeader = wmsPloHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsPloHeader)) {
            //主单不存在
            ErrorCode.PLO_NOT_EXIST_4001.throwError();
        }
        if (CANCEL_STATUS.equals(wmsPloHeader.getOrderStatus())) {
            //不能重复取消拣货单
            ErrorCode.PLO_CANCEL_ERROR_4014.throwError();
        }
        if (PICKING_FINISH_STATUS.equals(wmsPloHeader.getOrderStatus())) {
            //已经拣货完成不能取消
            ErrorCode.PLO_CANCEL_ERROR_4018.throwError();
        }
        //查询是否有拣货记录
        WmsPloDetail wmsPloDetailEntity = new WmsPloDetail();
        wmsPloDetailEntity.setPloId(wmsPloHeader.getId());
        WmsPloDetail wmsPloDetail = wmsPloDetailService.selectOneByEntity(wmsPloDetailEntity);
        if (! Objects.isNull(wmsPloDetail)) {
            //有拣货记录不能取消拣货单
            ErrorCode.PLO_CANCEL_ERROR_4015.throwError();
        }
        //更新拣货单状态
        WmsPloHeader updateWmsPloHeader = new WmsPloHeader();
        updateWmsPloHeader.setId(wmsPloHeader.getId());
        updateWmsPloHeader.setOrderStatus(CANCEL_STATUS);
        if (Objects.nonNull(CustomRequestContext.getUserInfo())) {
            updateWmsPloHeader.setLastUpdateUser(CustomRequestContext.getUserInfo().getEmployeeName());
        }
        updateWmsPloHeader.setLastUpdateTime(DateUtils.nowWithUTC());
        int updateResult = wmsPloHeaderService.updateByPrimaryKeySelective(updateWmsPloHeader);
        //更新出库任务单状态
        WmsDoHeader updateWmsDoHeader = new WmsDoHeader();
        updateWmsDoHeader.setId(wmsPloHeader.getDohId());
        updateWmsDoHeader.setOrderStatus("10");
        updateWmsDoHeader.setHasPlo("0");
        updateWmsDoHeader.setLastupdateTime(DateUtils.nowWithUTC());
        wmsDoHeaderService.updateByPrimaryKeySelective(updateWmsDoHeader);
        PloCudBaseResponse response = new PloCudBaseResponse();
        response.setIsSuccess(updateResult > 0);
        return response;
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
        if (PICKING_FINISH_STATUS.equals(wmsPloHeader.getOrderStatus())) {
            //不能重复提交拣货完毕
            ErrorCode.PLO_FINISH_ERROR_4006.throwError();
        }
        if (! PART_PICKING_STATUS.equals(wmsPloHeader.getOrderStatus())) {
            //只有部分拣货才能操作拣货完毕
            ErrorCode.PLO_FINISH_ERROR_4007.throwError();
        }
        if (wmsPloHeader.getPickedQty().compareTo(wmsPloHeader.getTotalQty()) == 1) {
            //拣货数不能大于拣货总数
            ErrorCode.PLO_FINISH_ERROR_4008.throwError();
        }
        //更新拣货单状态
        WmsPloHeader updateWmsPloHeader = new WmsPloHeader();
        updateWmsPloHeader.setId(wmsPloHeader.getId());
        updateWmsPloHeader.setOrderStatus(PICKING_FINISH_STATUS);
        updateWmsPloHeader.setPloTime(DateUtils.nowWithUTC());
        updateWmsPloHeader.setLastUpdateUser(CustomRequestContext.getUserInfo().getEmployeeName());
        updateWmsPloHeader.setLastUpdateTime(DateUtils.nowWithUTC());
        wmsPloHeaderService.updateByPrimaryKeySelective(updateWmsPloHeader);
        //更新出库任务拣货信息
        WmsDoHeader updateWmsDoHeader = new WmsDoHeader();
        updateWmsDoHeader.setId(wmsPloHeader.getDohId());
        updateWmsDoHeader.setPloQty(wmsPloHeader.getPickedQty());
        updateWmsDoHeader.setOrderStatus("30");
        updateWmsDoHeader.setLastupdateTime(DateUtils.nowWithUTC());
        wmsDoHeaderService.updateByPrimaryKeySelective(updateWmsDoHeader);
        //更新任务明细拣货数量
        WmsPloLine wmsPloLineEntity = new WmsPloLine();
        wmsPloLineEntity.setPloId(wmsPloHeader.getId());
        List<WmsPloLine> wmsPloLines = wmsPloLineService.selectByEntity(wmsPloLineEntity);
        List<WmsDoLine> updateWmsDoLines = new ArrayList<>();
        for (WmsPloLine wmsPloLine : wmsPloLines) {
            WmsDoLine updateWmsDoLine = new WmsDoLine();
            updateWmsDoLine.setId(wmsPloLine.getDolId());
            updateWmsDoLine.setPloQty(wmsPloLine.getPickedQty());
            updateWmsDoLines.add(updateWmsDoLine);
        }
        wmsDoLineService.updateBatch(updateWmsDoLines);
        PloCudBaseResponse response = new PloCudBaseResponse();
        return response;
    }
}
