package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsInitLine;
import com.telei.wms.datasource.wms.repository.WmsInitLineRepository;
import com.telei.wms.datasource.wms.vo.WmsInitLineVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsInitLineService extends BaseService<WmsInitLineRepository,WmsInitLine,Long> {

    @Autowired
    private WmsInitLineRepository wmsInitLineRepository;

    public List<WmsInitLineVO>  selectInitLinesByEntity(WmsInitLine wmsInitLine){
        return wmsInitLineRepository.selectInitLinesByEntity(wmsInitLine);
    }

}