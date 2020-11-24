package com.telei.wms.project.api.endpoint.backlc;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.autocoding.domain.condition.ConditionsBuilder;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.DateUtils;
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
import com.telei.wms.datasource.wms.model.WmsDoHeader;
import com.telei.wms.datasource.wms.model.WmsDoLine;
import com.telei.wms.datasource.wms.service.*;
import com.telei.wms.datasource.wms.vo.DoContainerDetailResponseVo;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.endpoint.backlc.dto.*;
import com.telei.wms.project.api.utils.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
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

    @Autowired
    private WmsDoContainerService wmsDoContainerService;

    @Autowired
    private WmsDoLineService wmsDoLineService;


    /**
     * 新增退库记录
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BacklcAddABussinessResponse addBacklc(BacklcAddABussinessRequest request) {
        Long dohId = request.getDohId();
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
        WmsDoHeader doHeader = wmsDoHeaderService.selectByPrimaryKey(dohId);
        if(Objects.isNull(doHeader)){
            ErrorCode.BACK_LC_ADD_FAILED_4010.throwError(JSON.toJSONString(dohId));
        }
        if(!StringUtils.equals(doHeader.getHadCheck(),"1")){
            ErrorCode.BACK_LC_ADD_FAILED_4011.throwError(JSON.toJSONString(dohId));
        }

        //退库数量 <= 已拣数量 - (已装柜数量 + 已退库数)
        BigDecimal backlcQty = Objects.isNull(doHeader.getBacklcQty())?BigDecimal.ZERO:doHeader.getBacklcQty();
        BigDecimal ploQty = Objects.isNull(doHeader.getPloQty())?BigDecimal.ZERO:doHeader.getPloQty();
        BigDecimal containerQty = Objects.isNull(doHeader.getContainerQty())?BigDecimal.ZERO:doHeader.getContainerQty();

        List<WmsBacklcList> wmsBacklcListList = DataConvertUtil.parseDataAsArray(requestList, WmsBacklcList.class);
        BigDecimal processQty = wmsBacklcListList.stream().map(WmsBacklcList::getBQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        if(processQty.compareTo(ploQty.subtract(containerQty.add(backlcQty))) > 0){
            ErrorCode.BACK_LC_ADD_FAILED_4014.throwError();
        }

        WmsDoLine doLineCondition = new WmsDoLine();
        doLineCondition.setDohId(dohId);

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


        WmsBacklc backlcCondtion = new WmsBacklc();
        backlcCondtion.setDohId(dohId);
        List<WmsBacklc> wmsBacklcCollection = wmsBacklcService.selectByEntity(backlcCondtion);
        if(Objects.isNull(wmsBacklcCollection) || wmsBacklcCollection.isEmpty()){
            WmsBacklc wmsBacklc = DataConvertUtil.parseDataAsObject(requestList.get(0), WmsBacklc.class);
            wmsBacklc.setBlId(blId);
            wmsBacklc.setCreateUser(createUser);
            wmsBacklc.setCreateTime(DateUtils.nowWithUTC());
            wmsBacklc.setBlCode(getBussinessNumber("TK"));
            wmsBacklc.setDetailedSpeciesQty(productIdSet.size());
            wmsBacklc.setSmallBagQty(totalSmallBagQty);
            wmsBacklc.setBQty(totalBQty);
            wmsBacklc.setMidBagQty(totalMidBagQty);
            wmsBacklc.setBigBagQty(totalBigBagQty);
            wmsBacklc.setVol(totalBVol);
            wmsBacklc.setWeight(totalBWeight);
            wmsBacklc.setDohId(dohId);

            int countForBackLc = wmsBacklcService.insertSelective(wmsBacklc);
            if(countForBackLc <= 0){
                ErrorCode.BACK_LC_ADD_FAILED_4004.throwError();
            }
        }else{
            if(wmsBacklcCollection.size() > 1){
                ErrorCode.BACK_LC_ADD_FAILED_4009.throwError();
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
        }

        /**退库数量回写到出库任务单头(wms_do_header)与出库任务明细(wms_do_line)*/
        Map<Long, BigDecimal> dolId2BqtyMap = wmsBacklcListList.stream().collect(Collectors.toMap(WmsBacklcList::getDolId, WmsBacklcList::getBQty));
        List<WmsDoLine> wmsDoLines = wmsDoLineService.selectByEntity(doLineCondition);
        for (WmsDoLine wmsDoLine : wmsDoLines) {
            wmsDoLine.setBacklcQty(dolId2BqtyMap.get(wmsDoLine.getId()));
        }
        doHeader.setBacklcQty(totalBQty);
        int countForDoHeader = wmsDoHeaderService.updateByPrimaryKeySelective(doHeader);
        if(countForDoHeader <= 0){
            ErrorCode.BACK_LC_ADD_FAILED_4012.throwError();
        }
        int countForDoLine = wmsDoLineService.updateBatch(wmsDoLines);
        if(countForDoLine <= 0){
            ErrorCode.BACK_LC_ADD_FAILED_4013.throwError();
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
            item.setMediaId(product.getImagePath());
            item.setBrand(product.getBrand());
            item.setBigBagWeight(product.getBigBagWeight());
            item.setBigBagVol(product.getBigBagVol());
            item.setBigBagRate(new BigDecimal(product.getBigBagQty()));
            item.setMidBagQty(new BigDecimal(product.getMidBagQty()));
        });
        BigDecimal totalBqty = list.stream().map(BacklcListBussinessResponse.BacklcListBussinessRecord::getBQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalBigBagQty = list.stream().map(BacklcListBussinessResponse.BacklcListBussinessRecord::getBigBagQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalBWeight = list.stream().map(BacklcListBussinessResponse.BacklcListBussinessRecord::getBWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalVol = list.stream().map(BacklcListBussinessResponse.BacklcListBussinessRecord::getBVol).reduce(BigDecimal.ZERO, BigDecimal::add);

        Long blId = wmsBacklcLists.get(0).getBlId();
        WmsBacklc wmsBacklc = wmsBacklcService.selectByPrimaryKey(blId);

        BacklcListBussinessResponse response = new BacklcListBussinessResponse();
        response.setCreateTime(wmsBacklc.getCreateTime());
        response.setCreateUser(wmsBacklc.getCreateUser());
        response.setBlCode(wmsBacklc.getBlCode());
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
        Long dohId = request.getDohId();
        WmsDoHeader wmsDoHeader = wmsDoHeaderService.selectByPrimaryKey(dohId);
        if(Objects.isNull(wmsDoHeader)){
            ErrorCode.BACK_LC_DO_HEADER_NOT_EXIST_4008.throwError(JSON.toJSONString(dohId));
        }
        ConditionsBuilder conditionsBuilder = ConditionsBuilder.create();
        conditionsBuilder.eq("companyId",request.getCompanyId());
        conditionsBuilder.eq("warehouseId",request.getWarehouseId());
        conditionsBuilder.eq("dohId",dohId);
        conditionsBuilder.eq("orderStatus","40");
        Map<String, Object> paramMap = conditionsBuilder.build();
        List<DoContainerDetailResponseVo> list = wmsDoContainerService.selectByCustomConditions(paramMap);
        BacklcOutStockContainerListBussinessResponse response = new BacklcOutStockContainerListBussinessResponse();
        response.setList(list);
        return response;
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
