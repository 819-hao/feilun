package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.goods.IStockFacade;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.request.StockPrintRequest;
import com.seeease.flywheel.goods.request.StockQueryRequest;
import com.seeease.flywheel.goods.result.StockPrintResult;
import com.seeease.flywheel.rfid.IRfidFacade;
import com.seeease.flywheel.rfid.request.RfidDeliveryRequest;
import com.seeease.flywheel.rfid.result.*;
import com.seeease.flywheel.sale.request.SaleReturnOrderExpressNumberUploadRequest;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.IStoreWorkQueryFacade;
import com.seeease.flywheel.storework.request.*;
import com.seeease.flywheel.storework.result.*;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.executor.QueryCmdExe;
import com.seeease.flywheel.web.common.work.executor.SubmitCmdExe;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.result.QueryListResult;
import com.seeease.flywheel.web.common.work.result.QueryPageResult;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
public class RfidController {

    private List<String> NO_PERMITTED_BRAND = Arrays.asList("配件", "礼品");

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkFacade storeWorkFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkQueryFacade storeWorkQueryFacade;
    @DubboReference(check = false, version = "1.0.0")
    private IRfidFacade rfidFacade;
    @Resource
    private QueryCmdExe workDetailsCmdExe;
    @DubboReference(check = false, version = "1.0.0")
    private IStockFacade stockFacade;


    /**
     * rfid 3号楼 打单发货 - 待发货
     * @return
     */
    @GetMapping("/rfid/wmsCollect/list")
    public SingleResponse<List<RfidWmsWorkListResult>> wmsCollectList(){
        QueryCmd<WmsWorkListRequest> cmd = new QueryCmd<>();
        cmd.setQueryTask(false);
        cmd.setBizCode(BizCode.WMS_COLLECT);
        cmd.setUseCase(UseCase.QUERY_LIST);

        WmsWorkListRequest req = new WmsWorkListRequest();
        req.setUseScenario(WmsWorkListRequest.UseScenario.WAIT_DELIVERY);
        req.setLimit(Integer.MAX_VALUE);
        req.setPage(1);
        cmd.setRequest(req);

        //待发货map
        Map<String, List<WmsWorkListResult>> waitReceiveMap = ((QueryPageResult) workDetailsCmdExe.query(cmd))
                .getResultList()
                .stream()
                .map(v -> ((WmsWorkListResult) v.getResult()))
                .filter(f-> !NO_PERMITTED_BRAND.contains(f.getBrandName()))
                .collect(Collectors.groupingBy(WmsWorkListResult::getOriginSerialNo));

        log.info("rfid waitReceiveMap ------ {}",JSONObject.toJSONString(waitReceiveMap));

        List<RfidWmsWorkListResult> ret = Collections.emptyList();
        if (! waitReceiveMap.isEmpty()){
            //已发货map
            req.setUseScenario(WmsWorkListRequest.UseScenario.COMPLETE);
            cmd.setRequest(req);
            Map<String, List<WmsWorkListResult>> completeMap = ((QueryPageResult) workDetailsCmdExe.query(cmd))
                    .getResultList()
                    .stream()
                    .map(v -> ((WmsWorkListResult) v.getResult()))
                    .filter(f-> !NO_PERMITTED_BRAND.contains(f.getBrandName()))
                    .collect(Collectors.groupingBy(WmsWorkListResult::getOriginSerialNo));

            log.info("rfid completeMap ------ {}",JSONObject.toJSONString(completeMap));
            ret = waitReceiveMap.entrySet().stream().map(e -> {
                return RfidWmsWorkListResult.builder()
                        .no(e.getKey())
                        .waitNo(e.getValue().size())
                        .outNo(completeMap.getOrDefault(e.getKey(), Collections.emptyList()).size())
                        .createTime(e.getValue().get(0).getCreatedTime())
                        .createUser(e.getValue().get(0).getCreatedBy())
                        .lgsCode(e.getValue().get(0).getDeliveryExpressNumber())
                        .build();
            }).collect(Collectors.toList());
        }

        return SingleResponse.of(ret);
    }

    /**
     * rfid 3号楼 打单发货 - 待发货 - 详情
     * @return
     */
    @GetMapping("/rfid/wmsCollect/{no}")
    public SingleResponse<List<RfidWmsCollectDetailResult>> wmsCollectDetail(@PathVariable String no){
        QueryCmd<WmsWorkListRequest> cmd = new QueryCmd<>();
        cmd.setQueryTask(true);
        cmd.setBizCode(BizCode.WMS_COLLECT);
        cmd.setUseCase(UseCase.QUERY_LIST);

        WmsWorkListRequest req = new WmsWorkListRequest();
        req.setUseScenario(WmsWorkListRequest.UseScenario.WAIT_DELIVERY);
        req.setLimit(Integer.MAX_VALUE);
        req.setPage(1);
        req.setOriginSerialNo(no);
        cmd.setRequest(req);

        List<RfidWmsCollectDetailResult> ret = ((QueryPageResult) workDetailsCmdExe.query(cmd))
                .getResultList()
                .stream()
                .map(v -> {
                    WmsWorkListResult source = (WmsWorkListResult) v.getResult();
                    RfidWmsCollectDetailResult target = RfidWmsCollectDetailResult.builder().build();
                    BeanUtils.copyProperties(source, target);

                    target.setTask(v.getTask());
                    return target;
                }).collect(Collectors.toList());

        return SingleResponse.of(ret);
    }



    @GetMapping("/rfid/config")
    public SingleResponse<RfidConfigResult> config(@RequestParam Integer platform){
        Assert.notNull(platform, "平台id不能为空");
        return SingleResponse.of(rfidFacade.config(platform));
    }

    /**
     * 根据表身号匹配数据
     * @param sn
     * @return
     */
    @GetMapping("/rfid/sn/match/{sn}")
    public SingleResponse<RfidStockSnMatchResult> config(@PathVariable String sn){
        Assert.isTrue(!StringUtils.isEmpty(sn), "表身号不能为空");
        StockQueryRequest request = StockQueryRequest.builder()
                .isSaleable(false)
                .stockSnList(Collections.singletonList(sn))
                .build();

        StockBaseInfo stock = stockFacade.queryByStockSn(request).get(0);
        RfidStockSnMatchResult ret = null;
        if (stock != null){
            ret = RfidStockSnMatchResult.builder()
                    .brandName(stock.getBrandName())
                    .model(stock.getModel())
                    .seriesName(stock.getSeriesName())
                    .wno(stock.getWno())
                    .build();
        }
        return SingleResponse.of(ret);
    }



    /**
     * 门店收货列表
     * @return
     */
    @GetMapping("/rfid/shop/receive/{q}")
    public SingleResponse<List<RfidShopReceiveListResult>> shopReceiveList(@PathVariable String q){
        return SingleResponse.of(rfidFacade.rfidWaitReceiveList(q));
    }

    /**
     * 门店收货详情
     * @param no 关联单号
     * @return
     */
    @GetMapping("rfid/shop/receivingList")
    public SingleResponse<List<RfidWorkDetailResult>> shopReceivingList(@RequestParam String no){
        QueryCmd<StoreWorkListRequest> queryCmd = new QueryCmd<>();
        queryCmd.setBizCode(BizCode.SHOP);
        queryCmd.setUseCase(UseCase.RECEIVING_LIST);
        queryCmd.setQueryTask(true);
        StoreWorkListRequest request = new StoreWorkListRequest();
        request.setOriginSerialNo(no);
        request.setPage(1);
        request.setLimit(Integer.MAX_VALUE);
        queryCmd.setRequest(request);

        QueryPageResult query = (QueryPageResult) workDetailsCmdExe.query(queryCmd);

        List<RfidWorkDetailResult> collect = query.getResultList().stream().map(v -> {
            StoreWorkListResult element = (StoreWorkListResult) v.getResult();

            return RfidWorkDetailResult.builder()
                    .workId(element.getId())
                    .brandName(element.getBrandName())
                    .seriesName(element.getSeriesName())
                    .stockSn(element.getStockSn())
                    .model(element.getModel())
                    .stockId(element.getStockId())
                    .serialNo(element.getSerialNo())
                    .goodsId(element.getGoodsId())
                    .task(v.getTask())
                    .wno(element.getWno())
                    .build();
        }).collect(Collectors.toList());


        return SingleResponse.of(collect);
    }



    /**
     * rfid查找待出库列表
     *
     * @param shopId 门店id
     * @param q      查询条件
     * @return
     */
    @GetMapping("/storeWork/rfid/{shopId}/{q}")
    public SingleResponse<List<RfidOutStoreListResult>> rfidList(@PathVariable Integer shopId,
                                                                 @PathVariable String q) {
        Assert.notNull(shopId, "门店id不能为空");
        return SingleResponse.of(rfidFacade.rfidWaitOutStoreList(shopId, q,NO_PERMITTED_BRAND));
    }

    /**
     * rfid查找出库详情 该接口为适配接口，查询工作流
     *
     * @param no 关联单号
     * @return
     */
    @GetMapping("/storeWork/rfid/{no}")
    public SingleResponse<List<RfidWorkDetailResult>> rfidDetail(@PathVariable String no) {
        Assert.notNull(no, "关联单号不能为空");

        QueryCmd<Object> queryCmd = new QueryCmd<>();

        queryCmd.setQueryTask(true);
        //区分门店还是总部
        Integer storeId = UserContext.getUser().getStore().getId();
        log.info("rfid user storeId:{}", storeId);
        if (storeId == FlywheelConstant._ZB_ID) {
            queryCmd.setBizCode(BizCode.STORAGE);
            queryCmd.setUseCase(UseCase.OUT_STORAGE_DETAILS);
            StoreWorkOutStorageDetailRequest data = new StoreWorkOutStorageDetailRequest();
            data.setOriginSerialNo(no);
            queryCmd.setRequest(data);
        } else {
            queryCmd.setBizCode(BizCode.SHOP);
            queryCmd.setUseCase(UseCase.LOGISTICS_DELIVERY_DETAILS);
            StoreWorkDeliveryDetailRequest data = new StoreWorkDeliveryDetailRequest();
            data.setOriginSerialNo(no);
            queryCmd.setRequest(data);
        }


        QueryListResult query = (QueryListResult) workDetailsCmdExe.query(queryCmd);
        //转换
        List<RfidWorkDetailResult> ret = query.getResultList().stream().map(v -> {
            StoreWorkDetailResult temp = (StoreWorkDetailResult) v.getResult();
            return RfidWorkDetailResult.builder()
                    .workId(temp.getId())
                    .brandName(temp.getBrandName())
                    .seriesName(temp.getSeriesName())
                    .stockSn(temp.getStockSn())
                    .model(temp.getModel())
                    .stockId(temp.getStockId())
                    .serialNo(temp.getSerialNo())
                    .goodsId(temp.getGoodsId())
                    .task(v.getTask())
                    .build();
        }).collect(Collectors.toList());


        List<Integer> stockIds = ret.stream().map(RfidWorkDetailResult::getStockId).collect(Collectors.toList());
        List<StockPrintResult> stocks = stockFacade.print(new StockPrintRequest(stockIds));

        ret.forEach(v -> {
            for (StockPrintResult s : stocks) {
                if (s.getId().equals(v.getStockId())) {
                    v.setWno(s.getWno());
                    break;
                }
            }
        });

        return SingleResponse.of(ret);
    }


    /**
     * rfid出库匹配
     *
     * @param request
     * @return
     */
    @PostMapping("/storeWork/rfid/match")
    public SingleResponse<RfidOutStoreMatchResult> match(@RequestBody StoreWorkRfidMatchRequest request) {
        Assert.notNull(request, "参数不能为空");
        Assert.notNull(request.getNo(), "关联单号不能为空");
        Assert.notEmpty(request.getWnoList(), "商品编码不能为空");


        List<StockBaseInfo> waitOutStocks = request
                .getWnoList()
                .stream()
                .map(v -> stockFacade.getByWno(v))
                .collect(Collectors.toList());


        log.info("rfid  waitOutStocks ----- {}", JSONObject.toJSONString(waitOutStocks));

        List<RfidWorkDetailResult> waitOutRecords = storeWorkQueryFacade.outStorageRfidDetails(new StoreWorkOutStorageRfidDetailRequest(request.getNo()));

        log.info("rfid  waitoutrecord ----- {}", JSONObject.toJSONString(waitOutRecords));


        RfidOutStoreMatchResult ret = new RfidOutStoreMatchResult();
        ArrayList<RfidOutStoreMatchResult.Match> matchedList = new ArrayList<>();
        StoreWorkOutStorageSupplyStockRequest outStorageReq = StoreWorkOutStorageSupplyStockRequest.builder()
                .scenario(StoreWorkOutStorageSupplyStockRequest.SupplyScenario.ALLOCATE)
                .lineList(new LinkedList<>())
                .originSerialNo(request.getNo())
                .build();

        for (StockBaseInfo waitOutStock : waitOutStocks) {

            //从记录中查询是否存在该商品id的列
            RfidWorkDetailResult matchedRecord = waitOutRecords.stream()
                    .filter(v -> waitOutStock.getStockId().equals(v.getStockId()))
                    .findFirst()
                    .orElse(null);


            //该商品需要bind
            if (matchedRecord == null) {
                //随机挑选一个需要绑定的记录
                RfidWorkDetailResult canBind = waitOutRecords.stream()
                        .filter(v -> v.getGoodsId().equals(waitOutStock.getGoodsId()) && v.getStockId() == null)
                        .findFirst()
                        .orElse(null);

                if (canBind != null) {
                    canBind.setStockId(waitOutStock.getStockId());
                    canBind.setGoodsId(waitOutStock.getGoodsId());

                    outStorageReq.getLineList().add(
                            StoreWorkOutStorageSupplyStockRequest.OutStorageSupplyStockDto.builder()
                                    .id(canBind.getWorkId())
                                    .stockSn(waitOutStock.getStockSn())
                                    .build()
                    );

                    matchedRecord = RfidWorkDetailResult
                            .builder()
                            .brandName(canBind.getBrandName())
                            .model(canBind.getModel())
                            .build();
                } else { //没有能够对应bind的记录
                    ret.getUnmatched().add(waitOutStock.getWno());
                    continue;
                }
            }

            matchedList.add(RfidOutStoreMatchResult.Match
                    .builder()
                    .brand(matchedRecord.getBrandName())
                    .model(matchedRecord.getModel())
                    .build()
            );
        }


        //聚合匹配到的结果
        matchedList.stream()
                .collect(Collectors.groupingBy(RfidOutStoreMatchResult.Match::getModel))
                .forEach((key, value) -> ret.getMatched().add(
                        RfidOutStoreMatchResult.Match
                                .builder()
                                .count(value.size())
                                .model(value.get(0).getModel())
                                .brand(value.get(0).getBrand())
                                .build()
                ));

        if (!outStorageReq.getLineList().isEmpty()) {
            log.info("rfid需要填充的变身号数据为 -------{}", JSONObject.toJSONString(outStorageReq.getLineList()));
            storeWorkFacade.outStorageSupplyStock(outStorageReq);
        }

        return SingleResponse.of(ret);
    }

    /**
     * rfid表发货配件或礼品自动发货(不写先注释掉)
     * @return
     */
    /*@PostMapping("/rfid/shop/logisticsDelivery")
    public SingleResponse logisticsDelivery(@RequestBody RfidDeliveryRequest rfidDeliveryRequest){
        //参数转换
        StoreWorkDeliveryRequest request = (StoreWorkDeliveryRequest) rfidDeliveryRequest.getRequest();
        SubmitCmd<StoreWorkDeliveryRequest> submitCmd = new SubmitCmd<>();
        submitCmd.setBizCode(BizCode.SHOP);
        submitCmd.setUseCase(UseCase.LOGISTICS_DELIVERY);
        submitCmd.setRequest(request);
        submitCmd.setTaskList(rfidDeliveryRequest.getTaskList().stream().map(v->{
            UserTaskDto userTaskDto = (UserTaskDto) v;
            return userTaskDto;
        }).collect(Collectors.toList()));
        log.info("传入参数为：{}",submitCmd);
        return null;
    }*/
}
