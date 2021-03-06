package com.telei.wms.datasource.wms.repository;

import com.nuochen.framework.autocoding.domain.mybatis.BaseRepository;
import com.telei.wms.datasource.wms.model.WmsIvOutConfirm;
import com.telei.wms.datasource.wms.vo.WmsDeductIvOutConfirmResponseVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WmsIvOutConfirmRepository extends BaseRepository<WmsIvOutConfirm,Long> {
    List<WmsDeductIvOutConfirmResponseVo> selectIvIdIndex();
}