package com.telei.wms.project.api.endpoint.ivOut;

import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.datasource.wms.model.WmsIvOut;
import com.telei.wms.datasource.wms.model.WmsIvOutHistory;
import com.telei.wms.datasource.wms.service.WmsIvOutHistoryService;
import com.telei.wms.datasource.wms.service.WmsIvOutService;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 待出库存
 * @Auther: Dean
 * @Date: 2020/9/23 11:20
 */
@Service
public class IvOutBussiness {

    @Autowired
    private Id idGenerator;

    @Autowired
    private WmsIvOutService wmsIvOutService;

    @Autowired
    private WmsIvOutHistoryService wmsIvOutHistoryService;

    /**
     * 新增
     * @param wmsIvOuts
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void addIvOut(List<WmsIvOut> wmsIvOuts) {
        wmsIvOutService.insertBatch(wmsIvOuts);
        List<WmsIvOutHistory> wmsIvOutHistories = DataConvertUtil.parseDataAsArray(wmsIvOuts, WmsIvOutHistory.class);
        wmsIvOutHistoryService.insertBatch(wmsIvOutHistories);
    }
}
