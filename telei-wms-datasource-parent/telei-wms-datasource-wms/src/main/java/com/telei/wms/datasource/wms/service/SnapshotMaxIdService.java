package com.telei.wms.datasource.wms.service;

import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.SnapshotMaxId;
import com.telei.wms.datasource.wms.repository.SnapshotMaxIdRepository;
import org.springframework.stereotype.Service;

@Service
public class SnapshotMaxIdService extends BaseService<SnapshotMaxIdRepository,SnapshotMaxId,Long> {
}