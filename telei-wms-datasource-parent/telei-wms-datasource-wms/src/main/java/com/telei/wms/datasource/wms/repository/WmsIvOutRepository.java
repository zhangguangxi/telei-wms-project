package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsIvOut;
import org.springframework.stereotype.Repository;

@Repository
public interface WmsIvOutRepository extends BaseRepository<WmsIvOut,Long> {
}