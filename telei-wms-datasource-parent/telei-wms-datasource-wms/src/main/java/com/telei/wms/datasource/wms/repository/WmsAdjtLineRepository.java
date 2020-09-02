package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsAdjtLine;
import org.springframework.stereotype.Repository;

@Repository
public interface WmsAdjtLineRepository extends BaseRepository<WmsAdjtLine,Long> {
}