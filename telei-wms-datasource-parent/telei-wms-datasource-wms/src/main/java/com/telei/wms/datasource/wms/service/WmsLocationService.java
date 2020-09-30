package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsLocation;
import com.telei.wms.datasource.wms.repository.WmsLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsLocationService extends BaseService<WmsLocationRepository,WmsLocation,Long> {

    @Autowired
    private WmsLocationRepository wmsLocationRepository;

    public String getLcCodeByLocation(Integer lcCode){
        return wmsLocationRepository.getLcCodeByLocation(lcCode);
    }

    public List<WmsLocation> selectByLcCodes(List<String> list) {
        return wmsLocationRepository.selectByLcCodes(list);
    }
}