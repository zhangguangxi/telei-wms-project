package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotTime;
import com.telei.wms.datasource.wms.repository.WmsIvSnapshotTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsIvSnapshotTimeService extends BaseService<WmsIvSnapshotTimeRepository,WmsIvSnapshotTime,Long> {

    @Autowired
    private WmsIvSnapshotTimeRepository wmsIvSnapshotTimeRepository;

    public WmsIvSnapshotTime selectNewEntity(){
        return wmsIvSnapshotTimeRepository.selectNewEntity();
    }

    public List<WmsIvSnapshotTime> selectEntityByTime(String leftSnapshotTime, String rightSnapshotTime){
        return wmsIvSnapshotTimeRepository.selectEntityByTime(leftSnapshotTime, rightSnapshotTime);
    }

}