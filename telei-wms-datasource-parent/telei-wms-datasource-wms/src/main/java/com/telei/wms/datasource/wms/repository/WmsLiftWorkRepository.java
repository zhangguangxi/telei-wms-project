package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsLiftWork;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface WmsLiftWorkRepository extends BaseRepository<WmsLiftWork,Long> {

    int revokeLiftWork(Collection<Long> ids);

}