package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsIdBak;
import org.springframework.stereotype.Repository;

@Repository
public interface WmsIdBakRepository extends BaseRepository<WmsIdBak,Long> {
}