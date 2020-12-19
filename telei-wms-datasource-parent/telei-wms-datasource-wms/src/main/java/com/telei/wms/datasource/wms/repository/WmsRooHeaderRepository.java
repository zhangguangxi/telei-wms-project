package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsRooHeader;
import com.telei.wms.datasource.wms.vo.ReportVo;
import com.telei.wms.datasource.wms.vo.RooHeaderResponseVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WmsRooHeaderRepository extends BaseRepository<WmsRooHeader,Long> {

    List<RooHeaderResponseVo> findAll(Map<String, Object> paramMap);

    RooHeaderResponseVo selectRooHeaderDetail(Long id);

    List<ReportVo> rooReportQuery(Map conditions);

}