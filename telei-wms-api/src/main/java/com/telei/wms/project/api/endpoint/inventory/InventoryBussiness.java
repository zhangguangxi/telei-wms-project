package com.telei.wms.project.api.endpoint.inventory;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.app.api.ApiResponse;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.infrastructure.component.commons.utils.LockMapUtil;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.customer.amqp.inventoryWriteBack.OmsInventoryWriteBack;
import com.telei.wms.customer.product.ProductFeignClient;
import com.telei.wms.customer.product.dto.ProductDetailRequest;
import com.telei.wms.customer.product.dto.ProductDetailResponse;
import com.telei.wms.customer.product.dto.ProductListResponse;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.*;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.amqp.producer.WmsOmsInventoryWriteBackProducer;
import com.telei.wms.project.api.endpoint.inventory.dto.*;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.WmsIdInstantdirectiveBussiness;
import com.telei.wms.project.api.utils.DataConvertUtil;
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
@Service
public class InventoryBussiness {
    @Autowired
    private Id idGenerator;

    @Autowired
    private WmsRoHeaderService wmsRoHeaderService;

    @Autowired
    private WmsRoLineService wmsRoLineService;

    @Autowired
    private WmsRooHeaderService wmsRooHeaderService;

    @Autowired
    private WmsPaoHeaderService wmsPaoHeaderService;

    @Autowired
    private WmsPaoLineService wmsPaoLineService;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private WmsIvAttributebatchService  wmsIvAttributebatchService;

    @Autowired
    private WmsInventoryService wmsInventoryService;

    @Autowired
    private WmsInventoryHistoryService wmsInventoryHistoryService;

    @Autowired
    private WmsIdInstantdirectiveBussiness wmsIdInstantdirectiveBussiness;

    @Autowired
    private WmsOmsInventoryWriteBackProducer wmsOmsInventoryWriteBackProducer;

    /**
     * 入库(上架)
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryAddBussinessResponse addInventory(InventoryAddBussinessRequest request) {
        List<InventoryAddBussinessRequest.InventoryAddRequestCondition> requestList = request.getList();
        if(requestList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_RECORD_NOT_EXIST_4001.throwError();
        }
        //1.基本信息提取
        //1.1 基本信息提取-上架单单头id集合
        List<Long> requestPaoIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getPaoId).collect(Collectors.toList());
        if(Objects.isNull(requestPaoIdList) || requestPaoIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_PAO_ID_IS_NULL_4020.throwError();
        }
        //1.2 基本信息提取-上架单明细id集合
        List<Long> requestPaolIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getPaolId).collect(Collectors.toList());
        if(Objects.isNull(requestPaolIdList) || requestPaolIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_ID_IS_NULL_4006.throwError();
        }
        //1.3 基本信息提取-库存批次id集合
        List<Long> requestIabIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getIabId).collect(Collectors.toList());
        if(Objects.isNull(requestIabIdList) || requestIabIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_INVENTORY_ATTRIBUTE_BATCH_ID_IS_NULL_4004.throwError();
        }
        //1.4 基本信息提取-收货单单头id集合
        List<Long> requestRooIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRooId).collect(Collectors.toList());
        if(Objects.isNull(requestRooIdList) || requestRooIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_ROO_ID_IS_NULL_4007.throwError();
        }
        //1.5 基本信息提取-收货单明细集合
        List<Long> requestRooLIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRoolId).collect(Collectors.toList());
        if(Objects.isNull(requestRooLIdList) || requestRooLIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_ROOL_ID_IS_NULL_4021.throwError();
        }
        //1.6 基本信息提取-入库任务单头id集合
        List<Long> requestRoIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRoId).collect(Collectors.toList());
        if(Objects.isNull(requestRoIdList) || requestRoIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_RO_ID_IS_NULL_4010.throwError();
        }
        //1.8 基本信息提取-产品id集合
        List<Long> requestProductIdList = requestList.stream().map(InventoryAddBussinessRequest.InventoryAddRequestCondition::getProductId).collect(Collectors.toList());
        if(Objects.isNull(requestProductIdList) || requestProductIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_PRODUCT_ID_IS_NULL_4002.throwError();
        }

        //获取锁-相关单据单头作为参数(上架单单头、库存批次单头、收货单单头、入库单头);
        List<List<Long>> lockKey = Arrays.asList(requestPaoIdList, requestIabIdList, requestRooIdList, requestRoIdList);
        Object lockValue = LockMapUtil.tryLock(lockKey);
        try{
        List<WmsInventory> inventoryList = DataConvertUtil.parseDataAsArray(request, WmsInventory.class);
        //2公用信息提取
        //2.1 UTC时间
        Date nowWithUTC = DateUtils.nowWithUTC();
        //2.2 用户信息(请求上下文)
        UserInfo userInfo = CustomRequestContext.getUserInfo();

        //3、预处理
        //3.1 产品(商品)-预处理
        ProductDetailRequest productDetailRequest = new ProductDetailRequest();
        productDetailRequest.setIds(requestProductIdList);
        ApiResponse productDetailResponse = productFeignClient.getProductDetailByBarCode(productDetailRequest);
        ProductListResponse response = JSON.parseObject(JSON.toJSONString(productDetailResponse.getData()), ProductListResponse.class);
        List<ProductDetailResponse> productList = response.getProductList();
        if(Objects.isNull(productList) || productList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_PRODUCT_NOT_EXIST_4003.throwError();
        }
        Map<Long, ProductDetailResponse> productMap = productList.stream().collect(Collectors.toMap(ProductDetailResponse::getId, Function.identity()));

        //2.2 库存批次-预处理
        List<WmsIvAttributebatch> ivAttributebatches = wmsIvAttributebatchService.selectByPrimaryKeys(requestIabIdList);
        if(Objects.isNull(ivAttributebatches) || ivAttributebatches.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_INVENTORY_ATTRIBUTE_BATCH_NOT_EXIST_4005.throwError();
        }
        Map<Long, WmsIvAttributebatch> ivAttributeBatchMap = ivAttributebatches.stream().collect(Collectors.toMap(WmsIvAttributebatch::getId, Function.identity()));

        //2.3 上架单单头-预处理(上架单单头 上架时间、上架用户、已上架数量)
        Long paoId = inventoryList.get(0).getIvDocumentId();
        WmsPaoHeader wmsPaoHeader = wmsPaoHeaderService.selectByPrimaryKey(paoId);
        BigDecimal waitPutawayQty = inventoryList.stream().filter(item -> ((Objects.nonNull(item.getIvQty())) && item.getIvQty().intValue() > 0)).map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        wmsPaoHeader.setPutawayQty(wmsPaoHeader.getPutawayQty().add(waitPutawayQty));

        //2.4 上架单明细-预处理(上架时间、上架用户、上架数量)
        Map<Long, WmsInventory> paoLineMap = inventoryList.stream().collect(Collectors.toMap(WmsInventory::getIvDocumentlineId, Function.identity()));
        if(Objects.isNull(paoLineMap) || paoLineMap.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_PAO_LINE_RECORD_NOT_EXIST_4001.throwError();
        }
        List<WmsPaoLine> paoLineList = wmsPaoLineService.selectByPrimaryKeys(requestPaolIdList);
        paoLineList.stream().forEach(item ->{
            item.setPaolQty(item.getPaolQty().add(paoLineMap.get(item).getIvQty()));
            item.setPutawayTime(nowWithUTC);
            item.setPutawayUser(userInfo.getUserName());
        });

        //2.5 收货单单头-预处理(更新 putawayQty)
        Map<Long, BigDecimal> rooQtyMap = requestList.stream().collect(Collectors.toMap(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRooId, InventoryAddBussinessRequest.InventoryAddRequestCondition::getIvQty));
        if(Objects.isNull(rooQtyMap) || rooQtyMap.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_ROO_ID_IS_NULL_4007.throwError();
        }

        List<WmsRooHeader> rooHeaderList = wmsRooHeaderService.selectByPrimaryKeys(requestRooIdList);
        if(Objects.isNull(rooHeaderList) || rooHeaderList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_ROO_NOT_EXIST_4008.throwError();
        }
        rooHeaderList.forEach(item ->{
               item.setPutawayQty(item.getPutawayQty().add(rooQtyMap.get(item.getId())));
        });

        //2.6 入库任务单头-预处理(上架数量累加:putawayQty 上架完成时间:putawayAllTime(when=putawayQty>=totalQty)  总计划入库数量:totalQty)
        Map<Long, BigDecimal> roQtyMap = requestList.stream().collect(Collectors.toMap(InventoryAddBussinessRequest.InventoryAddRequestCondition::getRoId, InventoryAddBussinessRequest.InventoryAddRequestCondition::getIvQty));
        if(Objects.isNull(roQtyMap) || roQtyMap.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_RO_NOT_EXIST_4011.throwError();
        }
        List<Long> roIdList = requestRoIdList;
        List<WmsRoHeader> roHeaderList = wmsRoHeaderService.selectByPrimaryKeys(roIdList);
        if(Objects.isNull(roHeaderList) || roHeaderList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_RO_NOT_EXIST_4011.throwError();
        }

        roHeaderList.stream().forEach(item ->{
            item.setPutawayQty(item.getPutawayQty().add(roQtyMap.get(item.getId())));
            if(item.getTotalQty().compareTo(item.getPutawayQty()) >= 0){
                item.setPutawayAllTime(nowWithUTC);
            }
        });

        //2.7 oms-入库计划单(单头/明细)-预处理 oms-采购单(单头/明细)-预处理
        List<Long> rolIdList = paoLineList.stream().map(WmsPaoLine::getRolId).collect(Collectors.toList());
        if(Objects.isNull(rolIdList) || rolIdList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_ROL_ID_IS_NULL_4012.throwError();
        }
        List<WmsRoLine> roLineList = wmsRoLineService.selectByPrimaryKeys(rolIdList);
        if(Objects.isNull(roLineList) || roLineList.isEmpty()){
            ErrorCode.INVENTORY_ADD_ERROR_ROL_LINE_RECORD_NOT_EXIST_4013.throwError();
        }
        Map<Long, WmsPaoLine> paoLineRolIdToEntityMap = paoLineList.stream().collect(Collectors.toMap(WmsPaoLine::getRolId, Function.identity()));

        List<OmsInventoryWriteBack.OmsInventoryWriteBackCondition> omsInventoryWriteBackConditionList = new ArrayList<>();
        roLineList.stream().forEach(item ->{
            OmsInventoryWriteBack.OmsInventoryWriteBackCondition omsInventoryWriteBackCondition = new OmsInventoryWriteBack.OmsInventoryWriteBackCondition();
            omsInventoryWriteBackCondition.setPodId(item.getPodId());
            omsInventoryWriteBackCondition.setPoId(item.getPoId());
            omsInventoryWriteBackCondition.setRpdId(item.getRpdId());
            omsInventoryWriteBackCondition.setIvQty(paoLineRolIdToEntityMap.get(item.getId()).getPaolQty());
            omsInventoryWriteBackConditionList.add(omsInventoryWriteBackCondition);
        });
        OmsInventoryWriteBack omsInventoryWriteBack = new OmsInventoryWriteBack();
        omsInventoryWriteBack.setList(omsInventoryWriteBackConditionList);

        //2.8 库存记录-预处理
        inventoryList.stream().forEach(item -> {
            ProductDetailResponse productDetail = productMap.get(item.getProductId());//产品记录
            WmsIvAttributebatch ivAttributebatchDetail = ivAttributeBatchMap.get(item.getIabId());//批次记录
            BigDecimal bigBagRate = new BigDecimal(ivAttributebatchDetail.getBigBagRate());//大包转转换率(批次表记录)
            BigDecimal midBagRate = new BigDecimal(ivAttributebatchDetail.getMidBagRate());//中包转化数
            BigDecimal ivQty = item.getIvQty();//库存数量

            item.setId(idGenerator.getUnique());//主键id
            item.setIvFifoTime(ivAttributebatchDetail.getCreateTime());//先进先出时间(获取跑批次表的创建时间)
            item.setStockUnit(productDetail.getStockUnit());;//计量单位
            item.setBigBagRate(bigBagRate);//大包转换率  批次表中获取
            item.setBigBagQty(ivQty.multiply(bigBagRate));//大包数量
            item.setBigBagExtraQty(ivQty.divideAndRemainder(bigBagRate)[1]);//大包剩余数量
            item.setMidBagRate(midBagRate); // 中包转化数(批次表记录)
            item.setMidBagQty(ivQty.max(midBagRate));//中包数量
            item.setMidBagExtraQty(ivQty.divideAndRemainder(midBagRate)[1]);//中包剩余数量
            item.setLastupdateUser(userInfo.getAccountId());//最近库存更新用户(上下文用户)
            item.setIvTranstime(nowWithUTC);//最近库存更新时间
            item.setIvCreatetime(nowWithUTC);//创建时间
            item.setBizDate(nowWithUTC);//业务日期(与创建时间保持一致)
        });

        //3、处理(状态数据变更)
        if(LockMapUtil.confirmLock(lockKey,lockValue)){
            inventoryDataProcess(inventoryList, wmsPaoHeader, paoLineList, rooHeaderList, roHeaderList, omsInventoryWriteBack);
        }
        }catch (Exception e){
            throw e;
        }finally {
            LockMapUtil.cancelLock(lockKey,lockValue);
        }

        return new InventoryAddBussinessResponse();
    }

    /**
     *  入库相关数据处理
     * @param inventoryList
     * @param wmsPaoHeader
     * @param paoLineList
     * @param rooHeaderList
     * @param roHeaderList
     * @param omsInventoryWriteBack
     */
    private void inventoryDataProcess(List<WmsInventory> inventoryList, WmsPaoHeader wmsPaoHeader, List<WmsPaoLine> paoLineList, List<WmsRooHeader> rooHeaderList, List<WmsRoHeader> roHeaderList, OmsInventoryWriteBack omsInventoryWriteBack) {
        //3.1 库存/库存历史处理  插入wms_inventory wms_inventory_history
        int countForInventory = wmsInventoryService.insertBatch(inventoryList);
        if(countForInventory != inventoryList.size()){
            ErrorCode.INVENTORY_ADD_ERROR_4014.throwError();
        }
        List<WmsInventoryHistory> inventoryHistoryList = DataConvertUtil.parseDataAsArray(inventoryList, WmsInventoryHistory.class);
        int countForInventoryHistory = wmsInventoryHistoryService.insertBatch(inventoryHistoryList);
        if(countForInventoryHistory != inventoryHistoryList.size() ){
            ErrorCode.INVENTORY_ADD_ERROR_4015.throwError();
        }
        //3.2 更新上架单单头/上架单明细
        int countForPao = wmsPaoHeaderService.updateByPrimaryKey(wmsPaoHeader);
        if(countForPao <= 0){
            ErrorCode.INVENTORY_ADD_ERROR_4016.throwError();
        }
        int countForPaoLine = wmsPaoLineService.updateBatch(paoLineList);
        if(countForPaoLine != paoLineList.size()){
            ErrorCode.INVENTORY_ADD_ERROR_4017.throwError();
        }
        //2.2 更新收货单单头
        int countForRootHeader = wmsRooHeaderService.updateBatch(rooHeaderList);
        if(countForRootHeader != rooHeaderList.size()){
            ErrorCode.INVENTORY_ADD_ERROR_4018.throwError();
        }
        //2.3 更新入库任务单头
        int countForRoHeader = wmsRoHeaderService.updateBatch(roHeaderList);
        if(countForRoHeader != roHeaderList.size()){
            ErrorCode.INVENTORY_ADD_ERROR_4019.throwError();
        }

        //4、异步(MQ)回写oms单据(更新入库计划单头/入库计划明细 更新采购单单头/采购单明细)
        //4.1 指令入库
        WmsIdInstantdirective wmsIdInstantdirective = wmsIdInstantdirectiveBussiness.add("PUTON", "", omsInventoryWriteBack);
        //4.2 MQ发送指令
        wmsOmsInventoryWriteBackProducer.send(wmsIdInstantdirective);
    }

    /**
     * 库存调整
     * @param request
     * @return
     */
    public InventoryIncreaseBussinessResponse adjustInventory(InventoryIncreaseBussinessRequest request) {
        return null;
    }

    /***
     * 移库存在
     * @param request
     * @return
     */
    public InventoryReduceBussinessResponse reduceInventory(InventoryReduceBussinessRequest request) {
        return null;
    }

    /**
     * 库存模板下载
     * @param request
     * @return
     */
    public InventoryShiftBussinessResponse shiftInventory(InventoryShiftBussinessRequest request) {
        return null;
    }

    /**
     * 库存导入
     * @param request
     * @return
     */
    public InventoryImportBussinessResponse ImportInventory(InventoryImportBussinessRequest request) {
        return null;
    }


        public static void main(String[] args) {
            BigDecimal amt = new BigDecimal(1001);
            BigDecimal[] results = amt.divideAndRemainder(BigDecimal.valueOf(20));
            System.out.println(results[0]);
            System.out.println(results[1]);
        }
}
