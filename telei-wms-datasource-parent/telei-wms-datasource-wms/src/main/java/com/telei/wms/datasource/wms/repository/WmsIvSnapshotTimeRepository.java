package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotTime;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsIvSnapshotTimeRepository extends BaseRepository<WmsIvSnapshotTime, Long> {

    WmsIvSnapshotTime selectNewEntity();

    List<WmsIvSnapshotTime> selectEntityByTime(@Param("leftSnapshotTime") String leftSnapshotTime, @Param("rightSnapshotTime") String rightSnapshotTime);

}