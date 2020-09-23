package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.vo.RoLinePageQueryResponseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.telei.wms.datasource.wms.model.WmsRoLine;

import java.util.List;

@Repository
public interface WmsRoLineRepository extends BaseRepository<WmsRoLine,Long> {

    List<RoLinePageQueryResponseVo> findAll(@Param("roId") Long roId, @Param("companyId") Long companyId);
}