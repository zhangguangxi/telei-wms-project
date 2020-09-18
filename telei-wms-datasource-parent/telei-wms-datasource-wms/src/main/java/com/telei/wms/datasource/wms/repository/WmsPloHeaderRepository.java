package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsPloHeader;
import com.telei.wms.datasource.wms.vo.PloHeaderPageQueryResponseVo;
import com.telei.wms.datasource.wms.vo.PloHeaderPageQueryRequestVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsPloHeaderRepository extends BaseRepository<WmsPloHeader,Long> {

    List<PloHeaderPageQueryResponseVo> findAll(PloHeaderPageQueryRequestVo requestVo);
}