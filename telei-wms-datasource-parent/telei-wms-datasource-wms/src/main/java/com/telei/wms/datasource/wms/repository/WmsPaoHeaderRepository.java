package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsPaoHeader;
import com.telei.wms.datasource.wms.vo.PaoHeaderPageQueryRequestVo;
import com.telei.wms.datasource.wms.vo.PaoHeaderPageQueryResponseVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsPaoHeaderRepository extends BaseRepository<WmsPaoHeader,Long> {

    List<PaoHeaderPageQueryResponseVo> findAll(PaoHeaderPageQueryRequestVo requestVo);
}