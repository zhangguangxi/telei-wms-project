package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsDoHeader;
import com.telei.wms.datasource.wms.vo.DoHeaderResponseVo;
import com.telei.wms.datasource.wms.vo.PullReplenishmentPageQueryResponseVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WmsDoHeaderRepository extends BaseRepository<WmsDoHeader,Long> {

    List<DoHeaderResponseVo> findAll(Map<String, Object> paramMap);

    List<PullReplenishmentPageQueryResponseVo> pullReplenishmentPageQuery(Map<String, Object> paramMap);

    String findPoId(Long dohId);
}