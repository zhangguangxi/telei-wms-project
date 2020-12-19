package com.telei.wms.project.api.endpoint.lcRecommend;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.autocoding.domain.Pagination;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.utils.DateUtils;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.datasource.wms.model.WmsInventory;
import com.telei.wms.datasource.wms.model.WmsLcRecommend;
import com.telei.wms.datasource.wms.model.WmsLcRecommendBak;
import com.telei.wms.datasource.wms.model.WmsLocation;
import com.telei.wms.datasource.wms.service.WmsInventoryService;
import com.telei.wms.datasource.wms.service.WmsLcRecommendBakService;
import com.telei.wms.datasource.wms.service.WmsLcRecommendService;
import com.telei.wms.datasource.wms.service.WmsLocationService;
import com.telei.wms.datasource.wms.vo.InventoryLocationResponseVo;
import com.telei.wms.datasource.wms.vo.LcRecommendPageQueryRequestVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.lcRecommend.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 新品分配推荐库位
 * @Auther: Dean
 * @Date: 2020/11/3 10:20
 */
@Service
@Slf4j
public class LcRecommendBussiness {

    @Autowired
    private Id idGenerator;

    @Autowired
    private WmsLcRecommendService wmsLcRecommendService;

    @Autowired
    private WmsLcRecommendBakService wmsLcRecommendBakService;

    @Autowired
    private WmsInventoryService wmsInventoryService;

    @Autowired
    private WmsLocationService wmsLocationService;

    /**
     * 添加分配库存
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public LcRecommendCudBaseResponse addLcRecommend(LcRecommendAddRequest request) {
        log.debug("**********addLcRecommend" + JSON.toJSONString(request));
        LcRecommendCudBaseResponse response = new LcRecommendCudBaseResponse();
        List<Long> productIds = request.getProductIds();
        //从库存获取
        List<InventoryLocationResponseVo> locationResponseVos = wmsInventoryService.findLocationAll(request.getCompanyId(), request.getWarehouseId(), productIds);
        List<Long> existProductIds = locationResponseVos.stream().map(InventoryLocationResponseVo::getProductId).collect(Collectors.toList());
        productIds.removeAll(existProductIds);
        if (productIds.isEmpty()) {
            return response;
        }
        log.debug("**********addLcRecommend*******productIds" + JSON.toJSONString(productIds));
        //从历史库存获取
        List<InventoryLocationResponseVo> historyLocationResponseVos = wmsInventoryService.findHistoryLocationAll(request.getCompanyId(), request.getWarehouseId(), productIds);
        if (! historyLocationResponseVos.isEmpty()) {
            Map<Long, List<String>> historyLocationMap = new HashMap<>();
            List<String> lcCodeAll = new ArrayList<>();
            historyLocationResponseVos.stream().forEach(locationResponseVo -> {
                List<String> lcCodes = historyLocationMap.get(locationResponseVo.getProductId());
                if (Objects.isNull(lcCodes)) {
                    lcCodes = new ArrayList<>();
                }
                lcCodes.add(locationResponseVo.getLcCode());
                lcCodeAll.add(locationResponseVo.getLcCode());
                historyLocationMap.put(locationResponseVo.getProductId(), lcCodes);
            });
            //获取已经在使用的库位
            Map<String, Long> existLocationMap = new HashMap<>();
            List<InventoryLocationResponseVo> existLocationAll = wmsInventoryService.findExistLocationByLcCode(lcCodeAll);
            log.debug("**********addLcRecommend*******existLocationAll" + JSON.toJSONString(existLocationAll));
            existLocationAll.stream().forEach(locationResponseVo -> {
                existLocationMap.put(locationResponseVo.getLcCode(), locationResponseVo.getProductId());
            });
            List<Long> removeProductIds = new ArrayList<>();
            productIds.stream().forEach(productId -> {
                List<String> historyLcCodes = historyLocationMap.get(productId);
                for (String lcCode : historyLcCodes) {
                    if (Objects.isNull(existLocationMap.get(lcCode))) {
                        removeProductIds.add(productId);
                        existLocationMap.put(lcCode, productId);
                        break;
                    }
                }
            });
            productIds.removeAll(removeProductIds);
        }
        if (productIds.isEmpty()) {
            return response;
        }
        Map<Long, WmsLcRecommend> wmsLcRecommendMap = new HashMap<>();
        List<WmsLcRecommend> wmsLcRecommends = wmsLcRecommendService.findByProductId(request.getCompanyId(), request.getWarehouseId(), productIds);
        log.debug("**********addLcRecommend*******wmsLcRecommends" + JSON.toJSONString(wmsLcRecommends));
        wmsLcRecommends.stream().forEach(item -> {
            wmsLcRecommendMap.put(item.getProductId(), item);
        });
        List<WmsLcRecommend> insertWmsLcRecommends = new ArrayList<>();
        List<WmsLcRecommend> updateWmsLcRecommends = new ArrayList<>();
        productIds.stream().forEach(productId -> {
            WmsLcRecommend wmsLcRecommend = wmsLcRecommendMap.get(productId);
            if (Objects.isNull(wmsLcRecommend)) {
                WmsLcRecommend insertWmsLcRecommend = new WmsLcRecommend();
                insertWmsLcRecommend.setId(idGenerator.getUnique());
                insertWmsLcRecommend.setCompanyId(request.getCompanyId());
                insertWmsLcRecommend.setWarehouseId(request.getWarehouseId());
                insertWmsLcRecommend.setWarehouseCode(request.getWarehouseCode());
                insertWmsLcRecommend.setEstArriveTime(request.getEstArriveTime());
                insertWmsLcRecommend.setProductId(productId);
                insertWmsLcRecommend.setLcCode("");
                insertWmsLcRecommend.setCreateTime(DateUtils.nowWithUTC());
                insertWmsLcRecommend.setCreateUser(request.getCreateUser());
                insertWmsLcRecommends.add(insertWmsLcRecommend);
            } else {
                if (! Objects.isNull(request.getEstArriveTime()) && request.getEstArriveTime().before(wmsLcRecommend.getEstArriveTime())) {
                    WmsLcRecommend updateWmsLcRecommend = new WmsLcRecommend();
                    updateWmsLcRecommend.setId(wmsLcRecommend.getId());
                    updateWmsLcRecommend.setEstArriveTime(wmsLcRecommend.getEstArriveTime());
                    updateWmsLcRecommend.setLastUpdateUser(request.getCreateUser());
                    updateWmsLcRecommend.setLastUpdateTime(DateUtils.nowWithUTC());
                    updateWmsLcRecommends.add(updateWmsLcRecommend);
                }
            }
        });
        //新增推荐库位
        if (! insertWmsLcRecommends.isEmpty()) {
            wmsLcRecommendService.insertBatch(insertWmsLcRecommends);
        }
        //更新推荐库位
        if (! updateWmsLcRecommends.isEmpty()) {
            wmsLcRecommendService.updateBatch(updateWmsLcRecommends);
        }
        return response;
    }

    /**
     * 分页查询主单
     * @param request
     * @return
     */
    public LcRecommendPageQueryResponse pageQueryLcRecommend(LcRecommendPageQueryRequest request) {
        LcRecommendPageQueryRequestVo requestVo = DataConvertUtil.parseDataAsObject(request, LcRecommendPageQueryRequestVo.class);
        requestVo.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        Pagination pagination = new Pagination(request.getPageNumber(), request.getPageSize());
        Pagination page = (Pagination) wmsLcRecommendService.findAll(pagination, requestVo);
        LcRecommendPageQueryResponse response = new LcRecommendPageQueryResponse();
        response.setPage(page);
        return response;
    }

    /**
     * 更新推荐库位
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public LcRecommendCudBaseResponse updateLcRecommend(LcRecommendUpdateRequest request) {
        if (! request.getLcCode().contains("S")) {
            //推荐库位不是样品库位
            ErrorCode.LC_RECOMMEND_CODE_IS_SAMPLE_4003.throwError();
        }
        WmsLcRecommend wmsLcRecommend = wmsLcRecommendService.selectByPrimaryKey(request.getId());
        if (Objects.isNull(wmsLcRecommend)) {
            //推荐库位不存在
            ErrorCode.LC_RECOMMEND_NOT_EXIST_4001.throwError();
        }
        WmsLocation wmsLocationEntity = new WmsLocation();
        wmsLocationEntity.setLcCode(request.getLcCode());
        WmsLocation wmsLocation = wmsLocationService.selectOneByEntity(wmsLocationEntity);
        if (Objects.isNull(wmsLocation)) {
            //库位不存在
            ErrorCode.LC_RECOMMEND_NOT_EXIST_4004.throwError();
        }
        WmsLcRecommend wmsLcRecommendEntity = new WmsLcRecommend();
        wmsLcRecommendEntity.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        wmsLcRecommendEntity.setLcCode(request.getLcCode());
        WmsLcRecommend wmsLcRecommendIsExist = wmsLcRecommendService.selectOneByEntity(wmsLcRecommendEntity);
        if (! Objects.isNull(wmsLcRecommendIsExist)) {
            //推荐库位已经被占用
            ErrorCode.LC_RECOMMEND_UPDATE_ERROR_4002.throwError();
        }
        WmsInventory wmsInventoryEntity = new WmsInventory();
        wmsInventoryEntity.setCompanyId(CustomRequestContext.getUserInfo().getCompanyId());
        wmsInventoryEntity.setLcCode(request.getLcCode());
        WmsInventory wmsInventoryIsExist = wmsInventoryService.selectOneByEntity(wmsInventoryEntity);
        if (! Objects.isNull(wmsInventoryIsExist)) {
            //推荐库位已经被占用
            ErrorCode.LC_RECOMMEND_UPDATE_ERROR_4002.throwError();
        }
        WmsLcRecommend updateWmsLcRecommend = DataConvertUtil.parseDataAsObject(request, WmsLcRecommend.class);
        updateWmsLcRecommend.setLastUpdateUser(CustomRequestContext.getUserInfo().getEmployeeName());
        updateWmsLcRecommend.setLastUpdateTime(DateUtils.nowWithUTC());
        int updateResult = wmsLcRecommendService.updateByPrimaryKeySelective(updateWmsLcRecommend);
        LcRecommendCudBaseResponse response = new LcRecommendCudBaseResponse();
        response.setIsSuccess(updateResult > 0);
        return response;
    }

    /**
     * 删除推荐库位
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public LcRecommendCudBaseResponse deleteLcRecommend(LcRecommendDeleteRequest request) {
        log.debug("**********deleteLcRecommend" + JSON.toJSONString(request));
        List<Long> productIds = request.getProductIds();
        if (Objects.isNull(productIds) || productIds.isEmpty()) {
            return null;
        }
        List<InventoryLocationResponseVo> inventoryLocationResponseVos = wmsInventoryService.findExistLocationByProductId(request.getCompanyId(), request.getWarehouseId(), productIds);
        log.debug("**********deleteLcRecommend*********inventoryLocationResponseVos" + JSON.toJSONString(inventoryLocationResponseVos));
        if (! inventoryLocationResponseVos.isEmpty()) {
            List<Long> existProductIds = new ArrayList<>();
            HashMap<Long, InventoryLocationResponseVo> existInventoryLocationMap = new HashMap<>();
            inventoryLocationResponseVos.stream().forEach(item -> {
                existProductIds.add(item.getProductId());
                existInventoryLocationMap.put(item.getProductId(), item);
            });
            List<WmsLcRecommend> wmsLcRecommends = wmsLcRecommendService.findByProductId(request.getCompanyId(), request.getWarehouseId(), existProductIds);
            if (! wmsLcRecommends.isEmpty()) {
                List<WmsLcRecommendBak> wmsLcRecommendBaks = DataConvertUtil.parseDataAsArray(wmsLcRecommends, WmsLcRecommendBak.class);
                wmsLcRecommendBaks.stream().forEach(item -> {
                    InventoryLocationResponseVo inventoryLocationResponseVo = existInventoryLocationMap.get(item.getProductId());
                    item.setDelLcCode(inventoryLocationResponseVo.getLcCode());
                    item.setDelTime(DateUtils.nowWithUTC());
                    item.setDelRemark(request.getOrderCode());
                });
                //保存备份
                wmsLcRecommendBakService.insertBatch(wmsLcRecommendBaks);
                List<Long> lrIds = wmsLcRecommends.stream().map(WmsLcRecommend::getId).collect(Collectors.toList());
                wmsLcRecommendService.deleteByPrimaryKeys(lrIds);
            }
        }
        LcRecommendCudBaseResponse response = new LcRecommendCudBaseResponse();
        return response;
    }
}
