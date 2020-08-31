package com.telei.wms.project.api.endpoint.idInstantdirective;

import com.alibaba.fastjson.JSON;
import com.telei.infrastructure.component.commons.utils.DateUtils;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.datasource.wms.model.IdInstantdirective;
import com.telei.wms.datasource.wms.service.IdInstantdirectiveService;
import com.telei.wms.project.api.configuration.IdInstantdirectiveConfig;
import com.telei.wms.project.api.endpoint.idInstantdirective.dto.IdInstantdirectiveAddRequest;
import com.telei.wms.project.api.endpoint.idInstantdirective.dto.IdInstantdirectiveCudBaseResponse;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: Dean
 * @Date: 2020/7/7 17:45
 */
@Service
public class IdInstantdirectiveBussiness {

    @Autowired
    private Id idGenerator;

    @Autowired
    private IdInstantdirectiveConfig idInstantdirectiveConfig;

    @Autowired
    private IdInstantdirectiveService idInstantdirectiveService;

    /**
     * 加数据同步指令
     * @param idTable
     * @param operationCode
     * @param orderContext
     * @return
     */
    public boolean add(String idTable, String operationCode, Object orderContext) {
        return add(idTable, operationCode, orderContext, null, null);
    }

    /**
     * 加数据同步指令
     * @param idTable
     * @param operationCode
     * @param orderContext
     * @param idRowId
     * @return
     */
    public boolean add(String idTable, String operationCode, Object orderContext, Long idRowId) {
        return add(idTable, operationCode, orderContext, idRowId, null);
    }

    /**
     * 添加数据同步指令
     * @param idTable
     * @param operationCode
     * @param orderContext
     * @param idNote
     * @return
     */
    public boolean add(String idTable, String operationCode, Object orderContext, String idNote) {
        return add(idTable, operationCode, orderContext, null, idNote);
    }

    /**
     * 添加数据同步指令
     * @param idTable
     * @param operationCode
     * @param orderContext
     * @param idRowId
     * @param idNote
     * @return
     */
    public boolean add(String idTable, String operationCode, Object orderContext, Long idRowId, String idNote) {
        IdInstantdirectiveAddRequest request = new IdInstantdirectiveAddRequest();
        request.setIdTable(idTable);
        request.setOperationCode(operationCode);
        request.setOrderContext(orderContext);
        request.setIdRowId(idRowId);
        request.setIdNote(idNote);
        IdInstantdirectiveCudBaseResponse response = add(request);
        return response.getIsSuccess();
    }

    /**
     * 添加数据同步指令
     * @param request
     * @return
     */
    public IdInstantdirectiveCudBaseResponse add(IdInstantdirectiveAddRequest request) {
        IdInstantdirective idInstantdirective = DataConvertUtil.parseDataAsObject(request, IdInstantdirective.class);
        idInstantdirective.setId(idGenerator.getUnique());
        idInstantdirective.setFromNode(idInstantdirectiveConfig.getServerName());
        idInstantdirective.setIdSystem(idInstantdirectiveConfig.getSystem());
        idInstantdirective.setOrderContext(JSON.toJSONString(request.getOrderContext()));
        idInstantdirective.setIsCode("O");
        idInstantdirective.setCreateTime(DateUtils.nowWithUTC());
        int insertResult = idInstantdirectiveService.insertSelective(idInstantdirective);
        IdInstantdirectiveCudBaseResponse response = new IdInstantdirectiveCudBaseResponse();
        response.setIsSuccess(insertResult > 0);
        return response;
    }
}
