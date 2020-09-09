package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotDailyKnot;
import org.springframework.stereotype.Repository;

@Repository
public interface WmsIvSnapshotDailyKnotRepository extends BaseRepository<WmsIvSnapshotDailyKnot,Long> {

    WmsIvSnapshotDailyKnot selectMaxId();

}