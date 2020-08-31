package com.telei.wms.project.api.endpoint.inventory;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.app.api.ApiResponse;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.customer.product.ProductFeignClient;
import com.telei.wms.customer.product.dto.ProductDetailRequest;
import com.telei.wms.customer.product.dto.ProductDetailResponse;
import com.telei.wms.customer.product.dto.ProductListResponse;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.*;
import com.telei.wms.project.api.endpoint.inventory.dto.*;
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
    private WmsRooLineService wmsRooLineService;

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


    /**
     * 入库
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryAddBussinessResponse addInventory(InventoryAddBussinessRequest request) {
        List<WmsInventory> inventoryList = DataConvertUtil.parseDataAsArray(request, WmsInventory.class);
        if(inventoryList.isEmpty()){
            // TODO: 2020/8/31
        }

        //UTC时间
        Date nowWithUTC = DateUtils.nowWithUTC();
        //用户信息(请求上下文)
        UserInfo userInfo = CustomRequestContext.getUserInfo();


        //产品(商品)-预处理
        List<Long> productIds = inventoryList.stream().map(WmsInventory::getProductId).collect(Collectors.toList());
        if(Objects.isNull(productIds) || productIds.isEmpty()){
            // TODO: 2020/8/31
        }
        ProductDetailRequest productDetailRequest = new ProductDetailRequest();
        productDetailRequest.setIds(productIds);
        ApiResponse productDetailResponse = productFeignClient.getProductDetailByBarCode(productDetailRequest);
        ProductListResponse response = JSON.parseObject(JSON.toJSONString(productDetailResponse.getData()), ProductListResponse.class);
        List<ProductDetailResponse> productList = response.getProductList();
        if(Objects.isNull(productList) || productList.isEmpty()){
            // TODO: 2020/8/31
        }
        Map<Long, ProductDetailResponse> productMap = productList.stream().collect(Collectors.toMap(ProductDetailResponse::getId, Function.identity()));



        //库存批次-预处理
        List<Long> iabIds = inventoryList.stream().map(WmsInventory::getIabId).collect(Collectors.toList());
        if(Objects.isNull(iabIds) || iabIds.isEmpty()){
            // TODO: 2020/8/31
        }
        List<WmsIvAttributebatch> ivAttributebatches = wmsIvAttributebatchService.selectByPrimaryKeys(iabIds);
        if(Objects.isNull(ivAttributebatches) || ivAttributebatches.isEmpty()){
            // TODO: 2020/8/31
        }
        Map<Long, WmsIvAttributebatch> ivAttributeBatchMap = ivAttributebatches.stream().collect(Collectors.toMap(WmsIvAttributebatch::getId, Function.identity()));


        //上架单单头-预处理(上架单单头 上架时间、上架用户、已上架数量)
        Long paoId = inventoryList.get(0).getIvDocumentId();
        WmsPaoHeader wmsPaoHeader = wmsPaoHeaderService.selectByPrimaryKey(paoId);
        BigDecimal waitPutawayQty = inventoryList.stream().filter(item -> ((Objects.nonNull(item.getIvQty())) && item.getIvQty().intValue() > 0)).map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        wmsPaoHeader.setPutawayQty(wmsPaoHeader.getPutawayQty().add(waitPutawayQty));

        //上架单明细-预处理(上架时间、上架用户、上架数量)
        Map<Long, WmsInventory> paoLineMap = inventoryList.stream().collect(Collectors.toMap(WmsInventory::getIvDocumentlineId, Function.identity()));
        List<WmsPaoLine> paoLineList = new ArrayList<>();
        paoLineMap.forEach((k,v)->{
            WmsPaoLine wmsPaoLine = new WmsPaoLine();
            wmsPaoLine.setId(k);
            wmsPaoLine.setPaolQty(v.getIvQty());
            wmsPaoLine.setPutawayTime(nowWithUTC);
            wmsPaoLine.setPutawayUser(userInfo.getUserName());
            paoLineList.add(wmsPaoLine);
        });

        //收货单单头-预处理(更新 putawayQty)



        //入库任务单头-预处理(上架数量累加:putawayQty 上架完成时间:putawayAllTime(when=putawayQty>=totalQty)  总计划入库数量:totalQty)


        //TODO: 2020/8/31 oms-入库计划单(单头/明细)-预处理

        //TODO: 2020/8/31 oms-采购单(单头/明细)-预处理



        inventoryList.stream().forEach(item -> {
            ProductDetailResponse productDetail = productMap.get(item.getProductId());//产品记录
            WmsIvAttributebatch ivAttributebatchDetail = ivAttributeBatchMap.get(item.getIabId());//批次记录
            BigDecimal bigBagRate = new BigDecimal(ivAttributebatchDetail.getBigBagQty());//大包转转换率(批次表记录)
            BigDecimal midBagRate = new BigDecimal(ivAttributebatchDetail.getMidBagQty());//中包转化数
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

        //插入wms_inventory wms_inventory_history
        int countForInventory = wmsInventoryService.insertBatch(inventoryList);
        if(countForInventory != inventoryList.size()){
            // TODO: 2020/8/31
        }
        List<WmsInventoryHistory> inventoryHistoryList = DataConvertUtil.parseDataAsArray(inventoryList, WmsInventoryHistory.class);
        int countForInventoryHistory = wmsInventoryHistoryService.insertBatch(inventoryHistoryList);
        if(countForInventoryHistory != inventoryHistoryList.size() ){
            // TODO: 2020/8/31
        }

        //2、回写上游单据
            //2.1 更新上架单单头/上架单明细
        int countForPao = wmsPaoHeaderService.updateByPrimaryKey(wmsPaoHeader);
        if(countForPao <= 0){
            // TODO: 2020/8/31
        }

        int countForPaoLine = wmsPaoLineService.updateBatch(paoLineList);
        if(countForPaoLine != paoLineList.size()){
            // TODO: 2020/8/31
        }


        //2.2 更新收货单单头
            //2.3 更新入库任务单头/入库任务明细



        //3、异步(MQ)回写oms单据
            //3.1 更新入库计划单头/入库计划明细
            //3.2 更新采购单单头/采购单明细


        return null;
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
