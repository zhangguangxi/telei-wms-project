package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotDailyKnot;
import com.telei.wms.datasource.wms.repository.WmsIvSnapshotDailyKnotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WmsIvSnapshotDailyKnotService extends BaseService<WmsIvSnapshotDailyKnotRepository,WmsIvSnapshotDailyKnot,Long> {

    @Autowired
    private WmsIvSnapshotDailyKnotRepository wmsIvSnapshotDailyKnotRepository;

    public WmsIvSnapshotDailyKnot selectMaxId(){
        return wmsIvSnapshotDailyKnotRepository.selectMaxId();
    }

}