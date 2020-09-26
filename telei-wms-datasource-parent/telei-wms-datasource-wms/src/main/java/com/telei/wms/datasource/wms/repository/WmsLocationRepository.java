package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsLocation;
import org.springframework.stereotype.Repository;

@Repository
public interface WmsLocationRepository extends BaseRepository<WmsLocation,Long> {

    String getLcCodeByLocation(Integer lcCode);

}