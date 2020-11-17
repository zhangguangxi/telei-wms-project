package com.telei.wms.project.api.endpoint.backlc;

import com.telei.wms.datasource.wms.service.WmsBacklcListService;
import com.telei.wms.datasource.wms.service.WmsBacklcService;
import com.telei.wms.project.api.endpoint.backlc.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: leo
 * @date: 2020/8/26 09:35
 */
@Slf4j
@Service
public class BaclcBussiness {

    @Autowired
    private WmsBacklcService wmsBacklcService;

    @Autowired
    private WmsBacklcListService wmsBacklcListService;

    /**
     * 新增退库记录
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BacklcAddABussinessResponse addBacklc(BacklcAddABussinessRequest request) {

        return new BacklcAddABussinessResponse();
    }


    /**
     * 退库记录列表
     *
     * @param request
     * @return
     */
    public BacklcListBussinessResponse listBacklc(BacklcListBussinessRequest request) {

        return new BacklcListBussinessResponse();
    }


    /***
     * 已出库-柜记录列表
     *
     * @param request
     * @return
     */
    public BacklcOutStockContainerListBussinessResponse outStockContainerListBacklc(BacklcOutStockContainerListBussinessRequest request) {

        return new BacklcOutStockContainerListBussinessResponse();
    }

}
