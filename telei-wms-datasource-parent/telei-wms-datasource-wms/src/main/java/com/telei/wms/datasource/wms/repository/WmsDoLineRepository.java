package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsDoLine;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsDoLineRepository extends BaseRepository<WmsDoLine,Long> {

    List<WmsDoLine> findAll(Long dohId);

}