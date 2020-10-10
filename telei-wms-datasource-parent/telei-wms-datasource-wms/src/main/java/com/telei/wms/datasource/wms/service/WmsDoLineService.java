package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsDoLine;
import com.telei.wms.datasource.wms.repository.WmsDoLineRepository;
import com.telei.wms.datasource.wms.vo.DoLineResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsDoLineService extends BaseService<WmsDoLineRepository,WmsDoLine,Long> {

    @Autowired
    private WmsDoLineRepository wmsDoLineRepository;

    public List<DoLineResponseVo> findAll(Long dohId, Long companyId) {
        return wmsDoLineRepository.findAll(dohId, companyId);
    }

}