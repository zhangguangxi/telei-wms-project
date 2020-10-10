package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsRooLine;
import com.telei.wms.datasource.wms.vo.RooLineResponseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsRooLineRepository extends BaseRepository<WmsRooLine,Long> {

    List<RooLineResponseVo> findAll(@Param("rooId") Long rooId, @Param("companyId") Long companyId);
}