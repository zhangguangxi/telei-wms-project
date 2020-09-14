package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotDailyKnot;
import com.telei.wms.datasource.wms.vo.WmsIvSnapshotDailyKnotVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsIvSnapshotDailyKnotRepository extends BaseRepository<WmsIvSnapshotDailyKnot,Long> {

    WmsIvSnapshotDailyKnot selectMaxId();

    List<WmsIvSnapshotDailyKnotVO> selectAllByIvstId(Long ivstId);

}