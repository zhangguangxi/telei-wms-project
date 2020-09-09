package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotTime;
import org.springframework.stereotype.Repository;

@Repository
public interface WmsIvSnapshotTimeRepository extends BaseRepository<WmsIvSnapshotTime,Long> {

    WmsIvSnapshotTime selectNewEntity();

}