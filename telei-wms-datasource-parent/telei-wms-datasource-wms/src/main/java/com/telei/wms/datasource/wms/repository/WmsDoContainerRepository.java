package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsDoContainer;
import com.telei.wms.datasource.wms.vo.DoContainerDetailResponseVo;
import com.telei.wms.datasource.wms.vo.DoContainerPageQueryRequestVo;
import com.telei.wms.datasource.wms.vo.DoContainerResponseVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WmsDoContainerRepository extends BaseRepository<WmsDoContainer,Long> {

    List<DoContainerResponseVo> findAll(Long cId);

    List<DoContainerResponseVo> findAllByDohId(Long dohId);

    List<DoContainerPageQueryRequestVo> findAllDoContainer(Map<String, Object> paramMap);

    List<DoContainerPageQueryRequestVo> queryContainerDetailList(Map<String, Object> paramMap);

    List<DoContainerDetailResponseVo> selectByCustomConditions(Map<String, Object> paramMap);
}