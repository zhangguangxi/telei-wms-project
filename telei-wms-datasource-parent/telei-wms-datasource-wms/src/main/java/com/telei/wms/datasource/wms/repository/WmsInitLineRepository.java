package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsInitLine;
import com.telei.wms.datasource.wms.vo.WmsInitLineVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsInitLineRepository extends BaseRepository<WmsInitLine, Long> {

    List<WmsInitLineVO> selectInitLinesByEntity(@Param("ivihId") Long ivihId, @Param("companyId") Long companyId);

}