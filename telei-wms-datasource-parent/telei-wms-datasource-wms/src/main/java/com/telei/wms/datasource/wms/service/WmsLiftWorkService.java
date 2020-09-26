package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsLiftWork;
import com.telei.wms.datasource.wms.repository.WmsLiftWorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class WmsLiftWorkService extends BaseService<WmsLiftWorkRepository,WmsLiftWork,Long> {

    @Autowired
    private WmsLiftWorkRepository wmsLiftWorkRepository;

    public int revokeLiftWork(Collection<Long> ids){
        return wmsLiftWorkRepository.revokeLiftWork(ids);
    }

}