package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsPloDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsPloDetailRepository extends BaseRepository<WmsPloDetail,Long> {

    List<WmsPloDetail> selectByDolIdList(List<Long> list);
}