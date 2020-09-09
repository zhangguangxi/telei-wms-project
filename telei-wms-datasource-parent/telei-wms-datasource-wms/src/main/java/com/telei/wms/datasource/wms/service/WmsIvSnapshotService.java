package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsIvSnapshot;
import com.telei.wms.datasource.wms.repository.WmsIvSnapshotRepository;
import org.springframework.stereotype.Service;

@Service
public class WmsIvSnapshotService extends BaseService<WmsIvSnapshotRepository,WmsIvSnapshot,Long> {
}