package com.telei.wms.datasource.wms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nuochen.framework.autocoding.domain.Pageable;
import com.nuochen.framework.autocoding.domain.mybatis.BaseService;
import com.telei.wms.datasource.wms.model.WmsDoContainer;
import com.telei.wms.datasource.wms.repository.WmsDoContainerRepository;
import com.telei.wms.datasource.wms.vo.DoContainerDetailResponseVo;
import com.telei.wms.datasource.wms.vo.DoContainerGroupResponseVo;
import com.telei.wms.datasource.wms.vo.DoContainerResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WmsDoContainerService extends BaseService<WmsDoContainerRepository, WmsDoContainer, Long> {

    @Autowired
    private WmsDoContainerRepository wmsDoContainerRepository;

    public List<DoContainerResponseVo> findAll(Long cId) {
        return wmsDoContainerRepository.findAll(cId);
    }

    public List<DoContainerResponseVo> findAllByDohId(Long dohId) {
        return wmsDoContainerRepository.findAllByDohId(dohId);
    }

    public Pageable findAllDoContainer(Pageable page, Map<String, Object> paramMap) {
        PageInfo pageInfo = PageHelper.offsetPage(page.getOffset(), page.getPageSize()).doSelectPageInfo(() -> {
            wmsDoContainerRepository.findAllDoContainer(paramMap);
        });
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }

    public Pageable queryContainerDetailList(Pageable page, Map<String, Object> paramMap) {
        PageInfo pageInfo = PageHelper.offsetPage(page.getOffset(), page.getPageSize()).doSelectPageInfo(() -> {
            wmsDoContainerRepository.queryContainerDetailList(paramMap);
        });
        page.setTotalRecords(pageInfo.getTotal());
        page.setContent(pageInfo.getList());
        return page;
    }

    public List<DoContainerDetailResponseVo> selectByCustomConditions(Map<String, Object> paramMap) {
        return wmsDoContainerRepository.selectByCustomConditions(paramMap);
    }

    public List<DoContainerGroupResponseVo> selectByDohId(Long dohId) {
        return wmsDoContainerRepository.selectByDohId(dohId);
    }
}