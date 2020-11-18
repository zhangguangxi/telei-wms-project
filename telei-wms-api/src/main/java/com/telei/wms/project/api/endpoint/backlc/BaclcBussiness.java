package com.telei.wms.project.api.endpoint.backlc;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.app.api.ApiResponse;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.customer.businessNumber.BusinessNumberFeignClient;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberRequest;
import com.telei.wms.customer.businessNumber.dto.BusinessNumberResponse;
import com.telei.wms.customer.product.ProductFeignClient;
import com.telei.wms.customer.product.dto.ProductDetailResponse;
import com.telei.wms.customer.product.dto.ProductListResponse;
import com.telei.wms.customer.product.dto.ProductRequest;
import com.telei.wms.datasource.wms.model.WmsBacklc;
import com.telei.wms.datasource.wms.model.WmsBacklcList;
import com.telei.wms.datasource.wms.service.WmsBacklcListService;
import com.telei.wms.datasource.wms.service.WmsBacklcService;
import com.telei.wms.datasource.wms.service.WmsDoHeaderService;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.backlc.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: leo
 * @date: 2020/8/26 09:35
 */
@Slf4j
@Service
public class BaclcBussiness {

    @Autowired
    private WmsBacklcService wmsBacklcService;

    @Autowired
    private WmsBacklcListService wmsBacklcListService;

    @Autowired
    private Id ideGenerator;

    @Autowired
    private WmsDoHeaderService wmsDoHeaderService;

    @Autowired
    private BusinessNumberFeignClient businessNumberFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;


    /**
     * 新增退库记录
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BacklcAddABussinessResponse addBacklc(BacklcAddABussinessRequest request) {
        List<BacklcAddRequest.BacklcAddRequestCondition> requestList = request.getList();
        if(Objects.isNull(requestList) || requestList.isEmpty()){
            ErrorCode.BACK_LC_RECORD_IS_NULL_4001.throwError();
        }
        List<Long> requestProductIdList = requestList.stream().map(BacklcAddRequest.BacklcAddRequestCondition::getProductId).collect(Collectors.toList());
        if(Objects.isNull(requestProductIdList) || requestProductIdList.isEmpty() || requestProductIdList.size() != requestList.size()){
            ErrorCode.BACK_LC_PRODUCT_ID_IS_NULL_4002.throwError();
        }

        /**单头id*/
        long blId = ideGenerator.getUnique();
        /**判断是否已经创建退库单头*/
        Long dohId = request.getDohId();
        WmsBacklc backlcCondtion = new WmsBacklc();
        backlcCondtion.setDohId(dohId);
        List<WmsBacklc> wmsBacklcCollection = wmsBacklcService.selectByEntity(backlcCondtion);

        List<WmsBacklcList> wmsBacklcListList = DataConvertUtil.parseDataAsArray(requestList, WmsBacklcList.class);
        wmsBacklcListList.forEach(item ->{
            item.setDohId(dohId);//出库任务单头id
            item.setBlId(blId);//退库单头id
            item.setBllId(ideGenerator.getUnique());//退库明细id
        });
        int countForBackLcList = wmsBacklcListService.insertBatch(wmsBacklcListList);
        if(countForBackLcList <= 0){
            ErrorCode.BACK_LC_LIST_ADD_FAILED_4003.throwError();
        }

        /**小包总数量*/
        BigDecimal totalSmallBagQty = wmsBacklcListList.stream().map(WmsBacklcList::getSmallBagQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        /**退库数量*/
        BigDecimal totalBQty = wmsBacklcListList.stream().map(WmsBacklcList::getBQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        /**中包总数量*/
        BigDecimal totalMidBagQty = wmsBacklcListList.stream().map(WmsBacklcList::getMidBagQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        /**大包总数量*/
        BigDecimal totalBigBagQty = wmsBacklcListList.stream().map(WmsBacklcList::getBigBagQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        /**总重量*/
        BigDecimal totalBWeight = wmsBacklcListList.stream().map(WmsBacklcList::getBWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
        /**总体积*/
        BigDecimal totalBVol = wmsBacklcListList.stream().map(WmsBacklcList::getBVol).reduce(BigDecimal.ZERO, BigDecimal::add);
        /**(前端)货品种类*/
        Set<Long> productIdSet = requestList.stream().map(BacklcAddRequest.BacklcAddRequestCondition::getProductId).collect(Collectors.toSet());
        /**创建用户*/
        String createUser = CustomRequestContext.getUserInfo().getEmployeeName();

        if(Objects.isNull(wmsBacklcCollection) || wmsBacklcCollection.isEmpty()){
            WmsBacklc wmsBacklc = DataConvertUtil.parseDataAsObject(requestList.get(0), WmsBacklc.class);
            wmsBacklc.setBlId(blId);
            wmsBacklc.setCreateUser(createUser);
            wmsBacklc.setBlCode(getBussinessNumber("TK"));
            wmsBacklc.setDetailedSpeciesQty(productIdSet.size());
            wmsBacklc.setSmallBagQty(totalSmallBagQty);
            wmsBacklc.setBQty(totalBQty);
            wmsBacklc.setMidBagQty(totalMidBagQty);
            wmsBacklc.setBigBagQty(totalBigBagQty);
            wmsBacklc.setVol(totalBVol);
            wmsBacklc.setWeight(totalBWeight);
            int countForBackLc = wmsBacklcService.insertSelective(wmsBacklc);
            if(countForBackLc < 0){
                ErrorCode.BACK_LC_ADD_FAILED_4004.throwError();
            }
            return new BacklcAddABussinessResponse();
        }
         if(wmsBacklcCollection.size() > 1){

            }
            WmsBacklcList wmsBacklcList = new WmsBacklcList();
            wmsBacklcList.setDohId(dohId);
            List<WmsBacklcList> wmsBacklcLists = wmsBacklcListService.selectByEntity(wmsBacklcList);
            Set<Long> dbProductIdSet = wmsBacklcLists.stream().map(WmsBacklcList::getProductId).collect(Collectors.toSet());
            dbProductIdSet.addAll(productIdSet);
            WmsBacklc wmsBacklc = wmsBacklcCollection.get(0);
            wmsBacklc.setSmallBagQty(wmsBacklc.getSmallBagQty().add(totalSmallBagQty));
            wmsBacklc.setBQty(wmsBacklc.getBQty().add(totalBQty));
            wmsBacklc.setMidBagQty(wmsBacklc.getMidBagQty().add(totalMidBagQty));
            wmsBacklc.setBigBagQty(wmsBacklc.getBigBagQty().add(totalBigBagQty));
            wmsBacklc.setVol(wmsBacklc.getVol().add(totalBVol));
            wmsBacklc.setWeight(wmsBacklc.getWeight().add(totalBWeight));
            wmsBacklc.setDetailedSpeciesQty(dbProductIdSet.size());
        int updateForBackLc = wmsBacklcService.updateByPrimaryKeySelective(wmsBacklc);

        if(updateForBackLc <= 0){
            ErrorCode.BACK_LC_UPDATE_FAILED_4005.throwError();
        }
        return new BacklcAddABussinessResponse();
    }


    /**
     * 退库记录(明细)列表
     *
     * @param request
     * @return
     */
    public BacklcListBussinessResponse listBacklc(BacklcListBussinessRequest request) {
        Long dohId = request.getDohId();
        WmsBacklcList wmsBacklcList = new WmsBacklcList();
        wmsBacklcList.setDohId(dohId);
        List<WmsBacklcList> wmsBacklcLists = wmsBacklcListService.selectByEntity(wmsBacklcList);
        if(Objects.isNull(wmsBacklcLists) || wmsBacklcLists.isEmpty()){
            return new BacklcListBussinessResponse();
        }
        UserInfo userInfo = CustomRequestContext.getUserInfo();
        Set<Long> productIdSet = wmsBacklcLists.stream().map(WmsBacklcList::getProductId).collect(Collectors.toSet());
        List<Long> requestProductIdList = productIdSet.stream().collect(Collectors.toList());
        ProductRequest productDetailRequest = new ProductRequest();
        productDetailRequest.setProductIds(requestProductIdList);
        productDetailRequest.setCompanyId(userInfo.getCompanyId());
        ApiResponse productListResponse = productFeignClient.getProductList(productDetailRequest);
        ProductListResponse productResponse = JSON.parseObject(JSON.toJSONString(productListResponse.getData()), ProductListResponse.class);
        List<ProductDetailResponse> productList = productResponse.getProductList();
        if (Objects.isNull(productList) || productList.isEmpty()) {
            ErrorCode.BACK_LC_LIST_PRODUCT_NOT_EXIST_4007.throwError(JSON.toJSONString(requestProductIdList));
        }
        Map<Long, ProductDetailResponse> productId2EntityMap = productList.stream().collect(Collectors.toMap(ProductDetailResponse::getProductId, Function.identity()));
        List<BacklcListBussinessResponse.BacklcListBussinessRecord> list = DataConvertUtil.parseDataAsArray(wmsBacklcLists, BacklcListBussinessResponse.BacklcListBussinessRecord.class);
        list.forEach(item ->{
            ProductDetailResponse product = productId2EntityMap.get(item.getProductId());
            item.setProductName(product.getProductName());
            item.setProductBarcode(product.getProductBarcode());
            item.setProductNo(product.getProductNo());
            item.setBrand(product.getBrand());
            item.setUnitGrossWeight(product.getUnitGrossWeight());
            item.setUnitVol(product.getUnitVol());
            item.setBoxQty(new BigDecimal(product.getBigBagQty()));
        });
        BigDecimal totalBqty = list.stream().map(BacklcListBussinessResponse.BacklcListBussinessRecord::getBQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalBigBagQty = list.stream().map(BacklcListBussinessResponse.BacklcListBussinessRecord::getBigBagQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalBWeight = list.stream().map(BacklcListBussinessResponse.BacklcListBussinessRecord::getBWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalVol = list.stream().map(BacklcListBussinessResponse.BacklcListBussinessRecord::getBVol).reduce(BigDecimal.ZERO, BigDecimal::add);
        BacklcListBussinessResponse response = new BacklcListBussinessResponse();
        response.setTotalBqty(totalBqty);
        response.setToatalVol(totalVol);
        response.setTotalBigBagqty(totalBigBagQty);
        response.setTotalWeight(totalBWeight);
        response.setList(list);
        return response;
    }


    /***
     * 已出库-柜记录列表
     *
     * @param request
     * @return
     */
    public BacklcOutStockContainerListBussinessResponse outStockContainerListBacklc(BacklcOutStockContainerListBussinessRequest request) {
        String dohId = request.getDohId();

        return new BacklcOutStockContainerListBussinessResponse();
    }


    /***
     * 获取业务单号
     * @param systemType
     * @return
     */
    private String getBussinessNumber(String systemType) {
        BusinessNumberRequest businessNumberRequest = new BusinessNumberRequest();
        businessNumberRequest.setType(systemType);
        ApiResponse apiResponse = businessNumberFeignClient.get(businessNumberRequest);
        BusinessNumberResponse businessNumberResponse = apiResponse.convertDataToObject(BusinessNumberResponse.class);
        String businessNumber = businessNumberResponse.getBusinessNumber();
        if (StringUtils.isEmpty(businessNumber)) {
            ErrorCode.BUSINESS_NUMBER_ERROR_4001.throwError();
        }
        log.info("\n +++++++++++++++++++++ 退库操作::获取业务单据号 -> {} ++++++++++++++++++++ \n ", businessNumber);
        return businessNumber;
    }
}
