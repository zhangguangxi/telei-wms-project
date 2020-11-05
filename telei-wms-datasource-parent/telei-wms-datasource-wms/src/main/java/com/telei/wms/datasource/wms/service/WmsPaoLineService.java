package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsPaoLine;
import com.telei.wms.datasource.wms.repository.WmsPaoLineRepository;
import com.telei.wms.datasource.wms.vo.PaoLinePageQueryResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsPaoLineService extends BaseService<WmsPaoLineRepository,WmsPaoLine,Long> {

    @Autowired
    private WmsPaoLineRepository wmsPaoLineRepository;

    public List<PaoLinePageQueryResponseVo> findAll(Long paoId, Long companyId) {
        return wmsPaoLineRepository.findAll(paoId, companyId);
    }
}