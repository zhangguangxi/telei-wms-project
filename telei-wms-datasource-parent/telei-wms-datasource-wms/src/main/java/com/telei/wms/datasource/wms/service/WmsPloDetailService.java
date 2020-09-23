package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsPloDetail;
import com.telei.wms.datasource.wms.repository.WmsPloDetailRepository;
import com.telei.wms.datasource.wms.vo.PloDetailPageQueryResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsPloDetailService extends BaseService<WmsPloDetailRepository,WmsPloDetail,Long> {

    @Autowired
    private WmsPloDetailRepository wmsPloDetailRepository;

    public List<PloDetailPageQueryResponseVo> findAll(Long paoId) {
        return wmsPloDetailRepository.findAll(paoId);
    }

    public List<WmsPloDetail> selectByDolIdList(List<Long> dolIdList) {
        return wmsPloDetailRepository.selectByDolIdList(dolIdList);
    }
}