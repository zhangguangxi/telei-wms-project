package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsAdjtLine;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface WmsAdjtLineRepository extends BaseRepository<WmsAdjtLine,Long> {

}