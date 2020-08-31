package com.telei.wms.project.api.endpoint.wmsIdInstantdirective;

import com.alibaba.fastjson.JSON;
import com.telei.infrastructure.component.commons.utils.DateUtils;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.datasource.wms.model.WmsIdBak;
import com.telei.wms.datasource.wms.model.WmsIdException;
import com.telei.wms.datasource.wms.model.WmsIdInstantdirective;
import com.telei.wms.datasource.wms.service.WmsIdBakService;
import com.telei.wms.datasource.wms.service.WmsIdExceptionService;
import com.telei.wms.datasource.wms.service.WmsIdInstantdirectiveService;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.dto.WmsIdInstantdirectiveAddRequest;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.dto.WmsIdInstantdirectiveCallbackRequest;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.dto.WmsIdInstantdirectiveCudBaseResponse;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @Auther: Dean
 * @Date: 2020/7/28 14:45
 */
@Service
public class WmsIdInstantdirectiveBussiness {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Id idGenerator;

    @Autowired
    private WmsIdInstantdirectiveService idInstantdirectiveService;

    @Autowired
    private WmsIdBakService idBakService;

    @Autowired
    private WmsIdExceptionService idExceptionService;

    /**
     * 添加指令
     * @param idtCode
     * @param orderContext
     * @return
     */
    public WmsIdInstantdirective add(String idtCode, Object orderContext) {
        return add(idtCode, null, orderContext, null);
    }

    /**
     * 添加指令
     * @param idtCode
     * @param idRefCode
     * @param orderContext
     * @return
     */
    public WmsIdInstantdirective add(String idtCode, String idRefCode, Object orderContext) {
        return add(idtCode, idRefCode, orderContext, null);
    }

    /**
     * 添加指令
     * @param idtCode
     * @param idRefCode
     * @param orderContext
     * @param idNote
     * @return
     */
    public WmsIdInstantdirective add(String idtCode, String idRefCode, Object orderContext, String idNote) {
        WmsIdInstantdirectiveAddRequest request = new WmsIdInstantdirectiveAddRequest();
        request.setIdtCode(idtCode);
        request.setIdRefCode(idRefCode);
        request.setOrderContext(orderContext);
        request.setIdNote(idNote);
        return add(request);
    }

    /**
     * 添加指令
     * @param request
     * @return
     */
    public WmsIdInstantdirective add(WmsIdInstantdirectiveAddRequest request) {
        WmsIdInstantdirective idInstantdirective = DataConvertUtil.parseDataAsObject(request, WmsIdInstantdirective.class);
        idInstantdirective.setId(idGenerator.getUnique());
        idInstantdirective.setOrderContext(JSON.toJSONString(request.getOrderContext()));
        idInstantdirective.setIsCode("O");
        idInstantdirective.setCreateTime(DateUtils.nowWithUTC());
        idInstantdirectiveService.insertSelective(idInstantdirective);
        return idInstantdirective;
    }

    /**
     * 数据同步回调
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public WmsIdInstantdirectiveCudBaseResponse callback(WmsIdInstantdirectiveCallbackRequest request) {
        WmsIdInstantdirective idInstantdirective = idInstantdirectiveService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(idInstantdirective)) {
            logger.info("指令不存在:" + JSON.toJSONString(request));
        } else {
            idInstantdirective.setProcessCount(idInstantdirective.getProcessCount() + 1);
            if (Objects.isNull(request.getIdExceptionAddRequest())) {
                idInstantdirective.setFinishTime(DateUtils.nowWithUTC());
                idInstantdirective.setIsCode("Y");
                WmsIdBak idBak = DataConvertUtil.parseDataAsObject(idInstantdirective, WmsIdBak.class);
                //添加到备份
                idBak.setIdId(idInstantdirective.getId());
                idBak.setBakTime(DateUtils.nowWithUTC());
                idBakService.insertSelective(idBak);
                idInstantdirectiveService.deleteByPrimaryKey(request.getId());
            } else {
                //添加异常
                WmsIdException idException = DataConvertUtil.parseDataAsObject(request.getIdExceptionAddRequest(), WmsIdException.class);
                idExceptionService.insertSelective(idException);
                WmsIdInstantdirective updateIdInstantdirective = DataConvertUtil.parseDataAsObject(idInstantdirective, WmsIdInstantdirective.class);
                updateIdInstantdirective.setIsCode("F");
                idInstantdirectiveService.updateByPrimaryKeySelective(updateIdInstantdirective);
            }
        }
        WmsIdInstantdirectiveCudBaseResponse response = new WmsIdInstantdirectiveCudBaseResponse();
        return response;
    }
}
