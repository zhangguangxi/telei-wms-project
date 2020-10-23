package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsIvSnapshot;
import com.telei.wms.datasource.wms.vo.WmsIvSnapshotDailyKnotVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsIvSnapshotRepository extends BaseRepository<WmsIvSnapshot,Long> {

    List<WmsIvSnapshotDailyKnotVO> selectByStatistics(Long ivstId);

    List<WmsIvSnapshot> findAll(@Param("ivstId") Long ivstId, @Param("spMaxId") Long spMaxId);

}