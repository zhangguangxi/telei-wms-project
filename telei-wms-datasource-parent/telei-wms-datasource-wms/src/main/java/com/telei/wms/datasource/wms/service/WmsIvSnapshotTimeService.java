package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotTime;
import com.telei.wms.datasource.wms.repository.WmsIvSnapshotTimeRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsIvSnapshotTimeService extends BaseService<WmsIvSnapshotTimeRepository,WmsIvSnapshotTime,Long> {
}