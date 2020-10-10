package com.telei.wms.project.api.endpoint.nestPrint;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.nuochen.framework.app.api.ApiResponse;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.customer.supplier.SupplierFeignClient;
import com.telei.wms.customer.supplier.dto.SupplierDetailRequest;
import com.telei.wms.customer.supplier.dto.SupplierListResponse;
import com.telei.wms.datasource.wms.model.WmsRoHeader;
import com.telei.wms.datasource.wms.service.WmsRoHeaderService;
import com.telei.wms.datasource.wms.service.WmsRoLineService;
import com.telei.wms.datasource.wms.vo.RoLinePageQueryResponseVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.nestPrint.dto.NestRooPrintDetailBussinessRequest;
import com.telei.wms.project.api.endpoint.nestPrint.dto.NestRooPrintDetailBussinessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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
        SupplierListResponse supperResponse = JSON.parseObject(JSON.toJSONString(apiResponse.getData()), SupplierListResponse.class);

        List<NestRooPrintDetailBussinessResponse.NestRooPrintDetailLine> list = Lists.newArrayList();
        roLinePageQueryResponseVos.stream().forEach(item ->{
            NestRooPrintDetailBussinessResponse.NestRooPrintDetailLine line = new NestRooPrintDetailBussinessResponse.NestRooPrintDetailLine();
            line.setBigBagRate(item.getBigBagQty());
            line.setBigBagQty((item.getPlanQty().divideAndRemainder(new BigDecimal(item.getBigBagQty()))[0].intValue()));
            line.setProductNo(item.getProductNo());
            list.add(line);
        });
        //供应商名称
        String supplierName = supperResponse.getSupplierList().get(0).getSupplierName();
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
}
