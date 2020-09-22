package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvOutConfirm;
import com.telei.wms.datasource.wms.repository.WmsIvOutConfirmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsIvOutConfirmService extends BaseService<WmsIvOutConfirmRepository,WmsIvOutConfirm,Long> {
    @Autowired
    private WmsIvOutConfirmRepository repository;

    public List<WmsIvOutConfirm> selectIvIdIndex() {
            return repository.selectIvIdIndex();
    }
}