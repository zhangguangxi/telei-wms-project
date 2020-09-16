package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsDoLine;
import org.springframework.stereotype.Repository;

@Repository
public interface WmsDoLineRepository extends BaseRepository<WmsDoLine,Long> {
}