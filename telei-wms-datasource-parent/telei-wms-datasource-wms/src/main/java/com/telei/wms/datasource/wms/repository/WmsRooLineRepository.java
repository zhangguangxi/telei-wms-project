package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsRooLine;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsRooLineRepository extends BaseRepository<WmsRooLine,Long> {

//    List<RooLineResponseVo> findAll(Long rooId);
    List findAll(Long rooId);
}