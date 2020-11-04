package com.telei.wms.project.api.endpoint.nestPrint;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.supplier.SupplierFeignClient;
import com.telei.wms.customer.supplier.dto.SupplierDetailRequest;
import com.telei.wms.customer.supplier.dto.SupplierResponse;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.*;
import com.telei.wms.datasource.wms.vo.DoLineResponseVo;
import com.telei.wms.datasource.wms.vo.PaoLinePageQueryResponseVo;
import com.telei.wms.datasource.wms.vo.PloLinePageQueryResponseVo;
import com.telei.wms.datasource.wms.vo.RoLinePageQueryResponseVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.nestPrint.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: leo
 * @date: 2020/10/10 14:07
 */
@Service
public class NestPrintBussiness {
    @Autowired
    private WmsRoHeaderService wmsRoHeaderService;
    @Autowired
    private WmsRoLineService wmsRoLineService;
    @Autowired
    private WmsPaoHeaderService wmsPaoHeaderService;
    @Autowired
    private WmsPaoLineService wmsPaoLineService;
    @Autowired
    private WmsLiftWorkService wmsLiftWorkService;
    @Autowired
    private WmsPloHeaderService wmsPloHeaderService;
    @Autowired
    private WmsPloLineService wmsPloLineService;
    @Autowired
    private WmsDoHeaderService wmsDoHeaderService;
    @Autowired
    private WmsDoLineService wmsDoLineService;

    @Autowired
    private Id idGenerator;
    @Autowired
    private SupplierFeignClient supplierFeignClient;

    /**
     * 收货-套打详情
     * @param request
     * @return
     */
    public NestRooPrintDetailBussinessResponse nestRooPrintDetail(NestRooPrintDetailBussinessRequest request) {
        WmsRoHeader wmsRoHeader = wmsRoHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsRoHeader)) {
            //入库任务主单不存在
            ErrorCode.RO_NOT_EXIST_4001.throwError();
        }
        List<RoLinePageQueryResponseVo> roLinePageQueryResponseVos = wmsRoLineService.findAll(request.getId(), wmsRoHeader.getCompanyId());

        //供应商
        SupplierDetailRequest supplierDetailRequest = new SupplierDetailRequest();
        supplierDetailRequest.setId(wmsRoHeader.getSupplierId());
        ApiResponse apiResponse = supplierFeignClient.getSupplierDetailById(supplierDetailRequest);
        SupplierResponse supperResponse = JSON.parseObject(JSON.toJSONString(apiResponse.getData()), SupplierResponse.class);

        List<NestRooPrintDetailBussinessResponse.NestRooPrintDetailLine> list = Lists.newArrayList();
        roLinePageQueryResponseVos.stream().forEach(item ->{
            NestRooPrintDetailBussinessResponse.NestRooPrintDetailLine line = new NestRooPrintDetailBussinessResponse.NestRooPrintDetailLine();
            line.setBigBagRate(item.getBigBagQty());
            line.setBigBagQty((item.getPlanQty().divideAndRemainder(new BigDecimal(item.getBigBagQty()))[0].intValue()));
            line.setProductNo(item.getProductNo());
            list.add(line);
        });
        //供应商名称
        String supplierName = supperResponse.getSupplierName();
        //生成打印编号
        long printNo = idGenerator.getUnique();
        //入库单据号
        String roCode = wmsRoHeader.getRoCode();
        NestRooPrintDetailBussinessResponse response = new NestRooPrintDetailBussinessResponse();
        response.setPrintNo(printNo);
        response.setRoCode(roCode);
        response.setSupplierName(supplierName);
        response.setList(list);
        return response;
    }

    /**
     * 上架-套打详情
     *
     * @param request
     * @return
     */
    public NestPaoPrintDetailBussinessResponse nestPaoPrintDetail(NestPaoPrintDetailBussinessRequest request) {
        WmsPaoHeader wmsPaoHeader = wmsPaoHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsPaoHeader)) {
            ErrorCode.PAO_NOT_EXIST_4001.throwError();
        }
        List<PaoLinePageQueryResponseVo> paoLinePageQueryResponseVos = wmsPaoLineService.findAll(request.getId(), wmsPaoHeader.getCompanyId());
        List<NestPaoPrintDetailBussinessResponse.NestPaoPrintDetailLine> list = Lists.newArrayList();
        paoLinePageQueryResponseVos.stream().forEach(item ->{
            NestPaoPrintDetailBussinessResponse.NestPaoPrintDetailLine line = new NestPaoPrintDetailBussinessResponse.NestPaoPrintDetailLine();
            line.setProductNo(item.getProductNo());
            line.setBigBagQty(item.getPaolQty().divideAndRemainder(item.getBigBagQty())[0].intValue());
            line.setBigBagRate(item.getBigBagQty().intValue());
            line.setPrepLcCode(item.getPrepLcCode());
            list.add(line);
        });
        NestPaoPrintDetailBussinessResponse response = new NestPaoPrintDetailBussinessResponse();
        //打印号
        response.setPrintNo(idGenerator.getUnique());
        //上架单单据编号
        response.setPaoCode(wmsPaoHeader.getPaoCode());
        //收货单单据编号
        response.setRooCode(wmsPaoHeader.getRooCode());
        response.setList(list);
        return response;
    }

    /**
     * 升降-套打详情
     * @param request
     * @return
     */
    public NestLiftWorkPrintDetailBussinessResponse nestLiftWorkPrintDetail(NestLiftWorkPrintDetailBussinessRequest request) {
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        if (null != request.getStartTime() && null != request.getEndTime()) {
            conditionsBuilder.between("createTime", request.getStartTime(), request.getEndTime());
        }
        if (StringUtils.isNoneBlank(request.getProductNo())) {
            conditionsBuilder.like("productNo", request.getProductNo());
        }
        if (StringUtils.isNoneBlank(request.getProductName())) {
            conditionsBuilder.like("productName", request.getProductName());
        }
        if (StringUtils.isNoneBlank(request.getProductBarcode())) {
            conditionsBuilder.like("productBarcode", request.getProductBarcode());
        }
        if (StringUtils.isNoneBlank(request.getSampleLcCode())) {
            conditionsBuilder.like("sampleLcCode", request.getSampleLcCode());
        }
        if (StringUtils.isNoneBlank(request.getLiftType())) {
            conditionsBuilder.eq("liftType", request.getLiftType());
        }
        if (StringUtils.isNoneBlank(request.getLiftStatus())) {
            conditionsBuilder.eq("liftStatus", request.getLiftStatus());
        }
        if (StringUtils.isNoneBlank(request.getOperateUser())) {
            conditionsBuilder.like("operateUser", request.getOperateUser());
        }
        conditionsBuilder.orderBy("lift_id DESC");
        conditionsBuilder.eq("companyId", CustomRequestContext.getUserInfo().getCompanyId());
        Map<String, Object> paramMap = conditionsBuilder.build();
        Pagination page = (Pagination) wmsLiftWorkService.selectPage(pagination, paramMap);
        List<WmsLiftWork> recoreds = (List<WmsLiftWork>)page.getContent();
        if(Objects.isNull(recoreds) || recoreds.isEmpty()){
            ErrorCode.NEST_LIFT_WORK_PRINT_4001.throwError();
        }
        List<NestLiftWorkPrintDetailBussinessResponse.NestLiftWorkPrintDetailLine> list = Lists.newArrayList();
        recoreds.stream().forEach(item->{
            NestLiftWorkPrintDetailBussinessResponse.NestLiftWorkPrintDetailLine line = new NestLiftWorkPrintDetailBussinessResponse.NestLiftWorkPrintDetailLine();
            line.setLiftType(item.getLiftType());
            line.setBigBagQty(Objects.isNull(item.getBigBagQty())?0:item.getBigBagQty().intValue());
            line.setProductNo(item.getProductNo());
            line.setPrepLcCode(item.getPrepLcCode());
            line.setSampleLcCode(item.getSampleLcCode());
            line.setOrderId(item.getId());
            line.setOrderCode(item.getLiftCode());
            list.add(line);
        });
        NestLiftWorkPrintDetailBussinessResponse response = new NestLiftWorkPrintDetailBussinessResponse();
        response.setList(list);
        response.setPrintNo(idGenerator.getUnique());
        return response;
    }


    /**
     * 拣货单-套打详情
     * @param request
     * @return
     */
    public NestPloPrintDetailBussinessResponse nestPloPrintDetail(NestPloPrintDetailBussinessRequest request) {
        WmsPloHeader wmsPloHeader = wmsPloHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsPloHeader)) {
            ErrorCode.PLO_NOT_EXIST_4001.throwError();
        }
        List<PloLinePageQueryResponseVo> ploLinePageQueryResponseVos = wmsPloLineService.findAll(request.getId());
        List<NestPloPrintDetailBussinessResponse.NestPloPrintDetailLine> list = Lists.newArrayList();
        ploLinePageQueryResponseVos.stream().forEach(item ->{
            NestPloPrintDetailBussinessResponse.NestPloPrintDetailLine line = new NestPloPrintDetailBussinessResponse.NestPloPrintDetailLine();
            line.setProductNo(item.getProductNo());
            line.setBigBagRate(item.getBigBagRate().intValue());
            line.setBigBagQty(item.getBigBagQty().intValue());
            line.setPrepLcCode(item.getLcCode());
            list.add(line);
        });
        NestPloPrintDetailBussinessResponse response = new NestPloPrintDetailBussinessResponse();
        response.setPrintNo(idGenerator.getUnique());
        response.setPloCode(wmsPloHeader.getPloCode());
        response.setDoCode(wmsPloHeader.getDohCode());
        response.setList(list);
        return response;
    }

    /**
     * 核验单-套打详情(出库任务详情)
     * @param request
     * @return
     */
    public NestCheckPrintDetailBussinessResponse nestCheckPrintDetail(NestCheckPrintDetailBussinessRequest request) {
        WmsDoHeader wmsDoHeader = wmsDoHeaderService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsDoHeader)) {
            ErrorCode.DO_NOT_EXIST_4001.throwError();
        }
        List<DoLineResponseVo> wmsDoLineList = wmsDoLineService.findAll(request.getId(), wmsDoHeader.getCompanyId());
        List<NestCheckPrintDetailBussinessResponse.NestDoPrintDetailLine> list = Lists.newArrayList();
        wmsDoLineList.stream().forEach(item->{
            NestCheckPrintDetailBussinessResponse.NestDoPrintDetailLine line = new NestCheckPrintDetailBussinessResponse.NestDoPrintDetailLine();
            line.setProductNo(item.getProductNo());
            line.setBigBagQty(item.getBigBagQty().intValue());
            line.setBigBagRate(item.getBigBagRate().intValue());
            list.add(line);
        });
        NestCheckPrintDetailBussinessResponse response = new NestCheckPrintDetailBussinessResponse();
        response.setPrintNo(idGenerator.getUnique());
        response.setDoCode(wmsDoHeader.getDohCode());
        response.setList(list);
        return response;
    }
}
