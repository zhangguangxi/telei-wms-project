package com.telei.wms.datasource.wms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsAdjtLine;
import com.telei.wms.datasource.wms.repository.WmsAdjtLineRepository;
import com.telei.wms.datasource.wms.vo.WmsInventoryPageQueryResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WmsAdjtLineService extends BaseService<WmsAdjtLineRepository,WmsAdjtLine,Long> {


}