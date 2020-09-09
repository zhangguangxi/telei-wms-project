package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsIvSnapshot;
import com.telei.wms.datasource.wms.vo.WmsIvSnapshotDailyKnotVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsIvSnapshotRepository extends BaseRepository<WmsIvSnapshot,Long> {

    List<WmsIvSnapshotDailyKnotVO> selectByStatistics(Long ivstId);

}