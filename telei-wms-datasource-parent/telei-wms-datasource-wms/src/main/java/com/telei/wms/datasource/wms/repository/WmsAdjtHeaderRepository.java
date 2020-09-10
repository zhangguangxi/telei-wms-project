package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsAdjtHeader;
import com.telei.wms.datasource.wms.vo.WmsAdjustHeaderPageQueryResponseVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WmsAdjtHeaderRepository extends BaseRepository<WmsAdjtHeader,Long> {

    List<WmsAdjustHeaderPageQueryResponseVo> selectCustomPage (Map<String, Object> paramMap);
}