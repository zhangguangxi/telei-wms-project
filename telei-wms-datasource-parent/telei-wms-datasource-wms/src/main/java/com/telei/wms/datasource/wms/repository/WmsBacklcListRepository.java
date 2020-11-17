package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsBacklcList;
import org.springframework.stereotype.Repository;

@Repository
public interface WmsBacklcListRepository extends BaseRepository<WmsBacklcList,Long> {
}