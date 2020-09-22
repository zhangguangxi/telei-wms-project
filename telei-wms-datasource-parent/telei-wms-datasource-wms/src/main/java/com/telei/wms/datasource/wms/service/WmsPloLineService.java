package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsPloLine;
import com.telei.wms.datasource.wms.repository.WmsPloLineRepository;
import com.telei.wms.datasource.wms.vo.PloLineLocationResponseVo;
import com.telei.wms.datasource.wms.vo.PloLinePageQueryResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsPloLineService extends BaseService<WmsPloLineRepository,WmsPloLine,Long> {

    @Autowired
    private WmsPloLineRepository wmsPloLineRepository;

    public List<PloLinePageQueryResponseVo> findAll(Long paoId) {
        return wmsPloLineRepository.findAll(paoId);
    }

    public List<PloLineLocationResponseVo> findLocationAll(Long warehouseId, List<Long> productIds) {
        return wmsPloLineRepository.findLocationAll(warehouseId, productIds);
    }

    public List<WmsPloLine> selectByDolIdList(List<Long> dolIdList) {
        return wmsPloLineRepository.selectByDolIdList(dolIdList);
    }
}