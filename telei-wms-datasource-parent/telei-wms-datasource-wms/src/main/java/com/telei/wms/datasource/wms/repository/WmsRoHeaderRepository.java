package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsRoHeader;
import com.telei.wms.datasource.wms.vo.RoHeaderPageQueryRequestVo;
import com.telei.wms.datasource.wms.vo.RoHeaderPageQueryResponseVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsRoHeaderRepository extends BaseRepository<WmsRoHeader,Long> {

    List<RoHeaderPageQueryResponseVo> findAll(RoHeaderPageQueryRequestVo requestVo);
}