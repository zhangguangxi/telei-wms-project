package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsPloDetail;
import org.springframework.stereotype.Repository;

@Repository
public interface WmsPloDetailRepository extends BaseRepository<WmsPloDetail,Long> {
}