package com.seeease.flywheel.serve.allocate.strategy;

import com.google.common.collect.Lists;
import com.seeease.flywheel.allocate.request.AllocateCreateRequest;
import com.seeease.flywheel.allocate.result.AllocateCreateResult;
import com.seeease.flywheel.serve.allocate.convert.AllocateConverter;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateDTO;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateLine;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateTask;
import com.seeease.flywheel.serve.allocate.enums.AllocateTaskStateEnum;
import com.seeease.flywheel.serve.allocate.service.BillAllocateService;
import com.seeease.flywheel.serve.allocate.service.BillAllocateTaskService;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.base.template.Bill;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.entity.StoreRelationshipSubject;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.StoreRelationshipSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreService;
import com.seeease.flywheel.serve.storework.enums.StoreWorkTypeEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.flywheel.storework.request.StoreWorKCreateRequest;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.seeeaseframework.mybatis.type.TransactionalUtil;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.exception.e.SeeeaseBaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/3/7
 */
@Slf4j
public abstract class AllocateStrategy implements Bill<AllocateCreateRequest, AllocateCreateResult> {
    @Resource
    private TransactionalUtil transactionalUtil;
    @Resource
    private BillAllocateTaskService billAllocateTaskService;
    @Resource
    protected BillAllocateService billAllocateService;
    @Resource
    protected StoreService storeService;
    @Resource
    protected StockService stockService;
    @Resource
    protected StoreRelationshipSubjectService storeRelationshipSubjectService;
    @Resource
    protected StoreManagementService storeManagementService;
    @Resource
    protected BillStoreWorkPreService billStoreWorkPreService;

    /**
     * 前置处理
     * 1、参数转换
     * 2、参数填充
     *
     * @param request
     */
    abstract void preRequestProcessing(AllocateCreateRequest request);

    /**
     * 业务校验
     * 1、必要参数校验
     * 2、金额校验
     * 3、业务可行性校验
     *
     * @param request
     * @throws BusinessException
     */
    abstract void checkRequest(AllocateCreateRequest request) throws BusinessException;


    @Override
    public void preProcessing(AllocateCreateRequest request) {
        //生成调拨单号
        request.setSerialNo(SerialNoGenerator.generateAllocateSerialNo());

        //设置当前登陆用户的门店
        request.setBelongingStoreId(Optional.ofNullable(request.getRightOfManagement())
                .filter(t -> request.isBrandTask())
                .map(storeRelationshipSubjectService::getBySubjectId)
                .map(StoreRelationshipSubject::getStoreManagementId)
                .orElse(Objects.requireNonNull(UserContext.getUser().getStore().getId()))
        );

        //补充库存信息
        List<AllocateCreateRequest.AllocateLineDto> stockDetails = request.getDetails()
                .stream()
                .filter(t -> Objects.nonNull(t.getStockId()))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(stockDetails)) {
            List<Stock> stockList = stockService.listByIds(stockDetails.stream()
                    .map(AllocateCreateRequest.AllocateLineDto::getStockId)
                    .collect(Collectors.toList()));

            Map<Integer, Stock> stockMap = stockList.stream()
                    .collect(Collectors.toMap(Stock::getId, Function.identity()));

            //补充发货方为商品位置
            stockDetails.forEach(t -> {
                Stock stock = stockMap.get(t.getStockId());
                t.setGoodsId(stock.getGoodsId());
                t.setFromId(stock.getLocationId()); //调出方采用商品位置，商品在哪里，哪里就是调出方
                t.setFromStoreId(stock.getStoreId());
                t.setFromRightOfManagement(stock.getRightOfManagement());
                t.setCostPrice(stock.getTotalPrice());
                t.setFromStockStatus(stock.getStockStatus().getValue());
                t.setConsignmentPrice(stock.getConsignmentPrice());
            });
        }

        this.preRequestProcessing(request);
    }

    @Override
    public void bizCheck(AllocateCreateRequest request) throws SeeeaseBaseException {
        Assert.notNull(request.getAllocateSource(), "来源异常");
        Assert.notNull(request.getToId(), "调入方异常");
        Assert.notNull(request.getToStoreId(), "调入仓库异常");
        Assert.isTrue(CollectionUtils.isNotEmpty(request.getDetails()), "调拨详情异常");
        request.getDetails().forEach(t -> {
            Assert.notNull(t.getGoodsId(), "商品不能为空");
            Assert.isTrue(Objects.nonNull(t.getFromId())
                    && t.getFromId() > 0
                    && t.getFromId() != request.getToId(), "调出方异常");
            Assert.isTrue(Objects.nonNull(t.getFromStoreId())
                    && t.getFromStoreId() > 0
                    && t.getFromStoreId() != request.getToStoreId(), "调出仓库异常");
            Assert.isTrue(Objects.nonNull(t.getGoodsId())
                    && t.getGoodsId() > 0, "调拨商品异常");
            Assert.isTrue(Objects.nonNull(t.getFromRightOfManagement())
                    && t.getFromRightOfManagement() > 0, "调拨经营权异常");
        });

        this.checkRequest(request);
    }

    @Override
    public AllocateCreateResult save(AllocateCreateRequest request) {
        return transactionalUtil.transactional(() -> {


            List<BillAllocateDTO> res = billAllocateService.create(request);
            List<AllocateCreateResult.AllocateDto> dtoList = new ArrayList<>();

            //创建出库单
            res.forEach(t -> {
                //门店联系人
                StoreManagementInfo fromShop = storeManagementService.selectInfoById(t.getAllocate().getFromId());
                StoreManagementInfo toShop = storeManagementService.selectInfoById(t.getAllocate().getToId());

                if (Objects.isNull(fromShop) || Objects.isNull(toShop)) {
                    throw new OperationRejectedException(OperationExceptionCode.SHOP_CUSTOMER_CONTACTS_ERROR);
                }

                List<AllocateCreateResult.AllocateWorkDto> workDtoList = this.createWork(t, fromShop, toShop);
                AllocateCreateResult.AllocateDto dto = AllocateConverter.INSTANCE.convertAllocateDto(t.getAllocate());
                dto.setFromShopShortcodes(fromShop.getShortcodes());
                dto.setToShopShortcodes(toShop.getShortcodes());
                dto.setWorkSerialNoList(workDtoList);
                dto.setStockIdList(t.getLines()
                        .stream()
                        .map(BillAllocateLine::getStockId)
                        .collect(Collectors.toList()));

                dtoList.add(dto);
            });

            //下架商品
            stockService.updateStockStatus(res.stream().map(BillAllocateDTO::getLines)
                    .flatMap(Collection::stream)
                    .map(BillAllocateLine::getStockId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()), StockStatusEnum.TransitionEnum.ALLOCATE);
            AllocateCreateResult build = AllocateCreateResult.builder()
                    .allocateDtoList(dtoList)
                    .build();

            //完成品牌调拨任务
            if (request.isBrandTask()) {
                List<BillAllocateTask> taskList = res.stream().map(t -> t.getLines().stream()
                                .map(l -> {
                                    BillAllocateTask task = AllocateConverter.INSTANCE.convertBillAllocateTask(t.getAllocate());
                                    task.setGoodsId(l.getGoodsId());
                                    task.setStockId(l.getStockId());
                                    task.setTaskState(AllocateTaskStateEnum.AT_ING);
                                    return task;
                                })
                                .collect(Collectors.toList()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
                billAllocateTaskService.insertBatchSomeColumn(taskList);
            }

            return build;
        });

    }

    /**
     * 创建出入库作业单
     *
     * @param dto
     * @param fromShop
     * @param toShop
     * @return
     */
    List<AllocateCreateResult.AllocateWorkDto> createWork(BillAllocateDTO dto, StoreManagementInfo fromShop, StoreManagementInfo toShop) {
        BillAllocate allocate = dto.getAllocate();

        //创建发货作业
        List<StoreWorkCreateResult> ckWorkList = billStoreWorkPreService.create(dto.getLines()
                .stream()
                .map(t -> StoreWorKCreateRequest.builder()
                        .mateMark(allocate.getSerialNo() + "-" + t.getId())
                        .goodsId(t.getGoodsId())
                        .stockId(t.getStockId())
                        .originSerialNo(allocate.getSerialNo())
                        .workSource(allocate.getAllocateSource().getValue())
                        .customerId(toShop.getCustomerId())
                        .customerContactId(toShop.getCustomerContactId())
                        .workType(StoreWorkTypeEnum.OUT_STORE.getValue())
                        .belongingStoreId(allocate.getFromId())
                        .build())
                .collect(Collectors.toList()));


        //创建收货作业
        List<StoreWorkCreateResult> rkWorkList = billStoreWorkPreService.create(dto.getLines()
                .stream()
                .map(t -> Lists.newArrayList(
                        //收货
                        StoreWorKCreateRequest.builder()
                                .mateMark(allocate.getSerialNo() + "-" + t.getId())
                                .goodsId(t.getGoodsId())
                                .stockId(t.getStockId())
                                .customerId(fromShop.getCustomerId())
                                .customerContactId(fromShop.getCustomerContactId())
                                .originSerialNo(allocate.getSerialNo())
                                .workSource(allocate.getAllocateSource().getValue())
                                .workType(StoreWorkTypeEnum.INT_STORE.getValue())
                                .belongingStoreId(allocate.getToId())
                                .guaranteeCardManage(t.getGuaranteeCardManage())
                                .build()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));

        Map<String, String> rkMap = rkWorkList.stream()
                .collect(Collectors.toMap(StoreWorkCreateResult::getMateMark
                        , StoreWorkCreateResult::getSerialNo));

        return ckWorkList.stream()
                .map(t -> AllocateCreateResult.AllocateWorkDto.builder()
                        .ckSerialNo(t.getSerialNo())
                        .rkSerialNo(Objects.requireNonNull(rkMap.get(t.getMateMark())))
                        .build())
                .collect(Collectors.toList());
    }

}
