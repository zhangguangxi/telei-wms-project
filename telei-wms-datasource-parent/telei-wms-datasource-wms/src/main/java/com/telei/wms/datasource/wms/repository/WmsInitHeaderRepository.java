package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsInitHeader;
import org.springframework.stereotype.Repository;

@Repository
public interface WmsInitHeaderRepository extends BaseRepository<WmsInitHeader,Long> {
}