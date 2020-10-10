package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsRooLine;
import com.telei.wms.datasource.wms.repository.WmsRooLineRepository;
import com.telei.wms.datasource.wms.vo.RooLineResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WmsRooLineService extends BaseService<WmsRooLineRepository,WmsRooLine,Long> {

    @Autowired
    private WmsRooLineRepository wmsRooLineRepository;

    public List<RooLineResponseVo> findAll(Long rooId, Long companyId) {
        return wmsRooLineRepository.findAll(rooId, companyId);
    }

}