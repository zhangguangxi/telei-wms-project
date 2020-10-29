package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvSnapshot;
import com.telei.wms.datasource.wms.repository.WmsIvSnapshotRepository;
import com.telei.wms.datasource.wms.vo.WmsIvSnapshotDailyKnotVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WmsIvSnapshotService extends BaseService<WmsIvSnapshotRepository, WmsIvSnapshot, Long> {

    @Autowired
    WmsIvSnapshotRepository wmsIvSnapshotRepository;

    public List<WmsIvSnapshot> findAll(Long ivstId, Long spMaxId) {
        return wmsIvSnapshotRepository.findAll(ivstId, spMaxId);
    }

    public List<WmsIvSnapshotDailyKnotVO> selectByStatistics(Long ivstId){
        return wmsIvSnapshotRepository.selectByStatistics(ivstId);
    }

}