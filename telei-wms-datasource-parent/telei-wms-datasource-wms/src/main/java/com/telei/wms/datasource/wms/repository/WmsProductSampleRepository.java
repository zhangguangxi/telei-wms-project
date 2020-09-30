package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsProductSample;
import org.springframework.stereotype.Repository;

@Repository
public interface WmsProductSampleRepository extends BaseRepository<WmsProductSample,Long> {
}