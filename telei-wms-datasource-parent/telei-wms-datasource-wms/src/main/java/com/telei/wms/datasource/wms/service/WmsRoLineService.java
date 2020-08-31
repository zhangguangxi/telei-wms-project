package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.vo.RoLinePageQueryResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.telei.wms.datasource.wms.model.WmsRoLine;
import com.telei.wms.datasource.wms.repository.WmsRoLineRepository;

import java.util.List;

@Service
public class WmsRoLineService extends BaseService<WmsRoLineRepository,WmsRoLine,Long> {

    @Autowired
    private WmsRoLineRepository wmsRoLineRepository;

    public List<RoLinePageQueryResponseVo> findAll(Long roId) {
        return wmsRoLineRepository.findAll(roId);
    }
}