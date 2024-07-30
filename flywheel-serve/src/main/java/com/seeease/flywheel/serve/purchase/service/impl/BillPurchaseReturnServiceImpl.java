package com.seeease.flywheel.serve.purchase.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.purchase.request.PurchaseReturnCancelRequest;
import com.seeease.flywheel.purchase.request.PurchaseReturnCreateRequest;
import com.seeease.flywheel.purchase.request.PurchaseReturnListRequest;
import com.seeease.flywheel.purchase.result.PurchaseReturnCancelResult;
import com.seeease.flywheel.purchase.result.PurchaseReturnCreateResult;
import com.seeease.flywheel.purchase.result.PurchaseReturnDetailsResult;
import com.seeease.flywheel.purchase.result.PurchaseReturnListResult;
import com.seeease.flywheel.serve.base.BusinessBillStateEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.service.StoreRelationshipSubjectService;
import com.seeease.flywheel.serve.purchase.convert.PurchaseReturnConverter;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturn;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturnLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseReturnLineStateEnum;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseReturnLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseReturnMapper;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseReturnService;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Service
public class BillPurchaseReturnServiceImpl extends ServiceImpl<BillPurchaseReturnMapper, BillPurchaseReturn> implements BillPurchaseReturnService {
    @Resource
    private BillPurchaseReturnLineMapper billPurchaseReturnLineMapper;

    @Resource
    private BillPurchaseLineMapper billPurchaseLineMapper;

    @Resource
    private BillPurchaseMapper billPurchaseMapper;

    @Resource
    private StockService stockService;

    @Resource
    private StoreRelationshipSubjectService storeRelationshipSubjectService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<PurchaseReturnCreateResult> create(PurchaseReturnCreateRequest request) {

        request.setRightOfManagement(storeRelationshipSubjectService.getByShopId(request.getStoreId()).getSubjectId());

        //1.表的信息 验证
        List<Integer> stockIdList = request.getDetails().stream().map(PurchaseReturnCreateRequest.BillPurchaseReturnLineDto::getStockId).collect(Collectors.toList());

        //1.采购单 查询已完成的 用到采购价格
        List<BillPurchaseLine> billPurchaseLineList = billPurchaseLineMapper.selectList(Wrappers.<BillPurchaseLine>lambdaQuery().
                in(BillPurchaseLine::getPurchaseLineState,
                        Arrays.asList(
                                //已入库
                                PurchaseLineStateEnum.WAREHOUSED,
                                //寄售中
                                PurchaseLineStateEnum.ON_CONSIGNMENT)).
                in(BillPurchaseLine::getStockId, stockIdList));

        if (CollectionUtils.isEmpty(billPurchaseLineList)) {
            throw new OperationRejectedException(OperationExceptionCode.PURCHASE_NO_EXIST);
        }

        //全量 //个人寄售 - 采购单 个人寄售特殊情况
        if (billPurchaseLineList.stream().allMatch(billPurchaseLine -> billPurchaseLine.getPurchaseLineState().equals(PurchaseLineStateEnum.ON_CONSIGNMENT))) {

            for (BillPurchaseLine billPurchaseLine : billPurchaseLineList) {

                //采购行都到完成 采购行 待结算可以不能退货
                //可结算都不能退货 只能从寄售中-->已完成
                BillPurchaseLine purchaseLine = new BillPurchaseLine();
                purchaseLine.setId(billPurchaseLine.getId());
                purchaseLine.setPurchaseLineState(PurchaseLineStateEnum.WAREHOUSED);

                billPurchaseLineMapper.updateById(purchaseLine);

                BillPurchase billPurchase = new BillPurchase();
                billPurchase.setId(billPurchaseLine.getPurchaseId());
                billPurchase.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNDER_WAY_TO_COMPLETE);

                billPurchaseMapper.updateByIdCheckState(billPurchase);
            }
        }

        List<Integer> collectByStock = billPurchaseLineList.stream().map(BillPurchaseLine::getStockId).collect(Collectors.toList());

        //经营权 商品状态
        List<Stock> stockList = stockService.list(Wrappers.<Stock>lambdaQuery()
//                .eq(Stock::getRightOfManagement, request.getRightOfManagement())
                .eq(Stock::getStockStatus, StockStatusEnum.MARKETABLE)
                .in(Stock::getId, collectByStock));

        if (CollectionUtils.isEmpty(stockList)) {
            throw new OperationRejectedException(OperationExceptionCode.STOCK_PARAMETER);
        }

        if (stockIdList.size() != stockList.size()) {
            throw new OperationRejectedException(OperationExceptionCode.NUMBER_NO_EXIST);
        }

        //3。查询采购单 用到 采购主体 采购类型 单号
        List<BillPurchase> billPurchaseList = billPurchaseMapper.selectBatchIds(billPurchaseLineList.stream().collect(Collectors.groupingBy(BillPurchaseLine::getPurchaseId)).keySet());

        billPurchaseList.stream().filter(billPurchase -> !billPurchase.getCustomerId().equals(request.getCustomerId())).findAny().ifPresent(billPurchase -> {
            throw new OperationRejectedException(OperationExceptionCode.CUSTOMER_PARAMETER);
        });

        //2。查询所有表的价格
        request.getDetails().forEach(dto -> dto.setPurchaseReturnPrice(Optional.ofNullable(dto.getPurchaseReturnPrice()).orElse(stockList.stream().filter(stock -> dto.getStockId().equals(stock.getId())).findAny().get().getPurchasePrice())));

        return packagePurchaseReturnCreateResult(request, billPurchaseLineList, stockList, billPurchaseList);

    }

    /**
     * 封装请求返回参数
     *
     * @param request
     * @param billPurchaseLineList
     * @param stockList
     * @param billPurchaseList
     * @return
     */
    private List packagePurchaseReturnCreateResult(PurchaseReturnCreateRequest request, List<BillPurchaseLine> billPurchaseLineList, List<Stock> stockList, List<BillPurchase> billPurchaseList) {

        String purchaseReturnSerialNo = SerialNoGenerator.generatePurchaseReturnSerialNo();

        AtomicInteger offs = new AtomicInteger(0);

        Map<Integer, List<PurchaseReturnCreateRequest.BillPurchaseReturnLineDto>> map = request.getDetails().stream().map(item -> {

            //构造行参数
            PurchaseReturnCreateRequest.BillPurchaseReturnLineDto billPurchaseReturnLineDto = new PurchaseReturnCreateRequest.BillPurchaseReturnLineDto();

            BillPurchase purchase = billPurchaseList.stream().filter(billPurchase ->
                    billPurchase.getId().equals(billPurchaseLineList.stream().filter(billPurchaseLine -> billPurchaseLine.getStockId().equals(item.getStockId())).findAny().get().getPurchaseId())).findAny().get();
            billPurchaseReturnLineDto.setOriginSerialNo(purchase.getSerialNo());
            billPurchaseReturnLineDto.setPurchaseType(purchase.getPurchaseSource().getValue());
            billPurchaseReturnLineDto.setPurchaseSubjectId(purchase.getPurchaseSubjectId());
            billPurchaseReturnLineDto.setPurchaseReturnPrice(item.getPurchaseReturnPrice());
            billPurchaseReturnLineDto.setStockId(item.getStockId());

            billPurchaseReturnLineDto.setLocationId(stockList.stream().filter(stock -> stock.getId().equals(item.getStockId())).findAny().get().getLocationId().intValue());
            billPurchaseReturnLineDto.setGoodsId(stockList.stream().filter(stock -> stock.getId().equals(item.getStockId())).findAny().get().getGoodsId().intValue());

            return billPurchaseReturnLineDto;
        }).collect(Collectors.groupingBy(PurchaseReturnCreateRequest.BillPurchaseReturnLineDto::getLocationId));

        return map.entrySet().stream().map(dto -> {

            Integer k = dto.getKey();
            List<PurchaseReturnCreateRequest.BillPurchaseReturnLineDto> v = dto.getValue();

            BillPurchaseReturn billPurchaseReturn = PurchaseReturnConverter.INSTANCE.convert(request);

            //生成采购退货单号
            billPurchaseReturn.setSerialNo(groupSerialNo(purchaseReturnSerialNo, map.size(), offs.addAndGet(1)));
            billPurchaseReturn.setPurchaseReturnState(BusinessBillStateEnum.UNCONFIRMED);
            billPurchaseReturn.setCustomerId(request.getCustomerId());
            billPurchaseReturn.setCustomerContactId(request.getCustomerContactId());

            billPurchaseReturn.setReturnPrice(v.stream().map(PurchaseReturnCreateRequest.BillPurchaseReturnLineDto::getPurchaseReturnPrice).reduce(BigDecimal.ZERO, BigDecimal::add));

            //传入建单门店
            billPurchaseReturn.setStoreId(request.getStoreId());

            billPurchaseReturn.setIsStore(k.intValue() == 1 ? WhetherEnum.NO : WhetherEnum.YES);
            billPurchaseReturn.setFromStoreId(k.intValue());

            baseMapper.insert(billPurchaseReturn);

            List<BillPurchaseReturnLine> billPurchaseReturnLineList = getBillPurchaseReturnLines(v, billPurchaseReturn, stockList);
            billPurchaseReturnLineList.forEach(billPurchaseReturnLine -> billPurchaseReturnLineMapper.insert(billPurchaseReturnLine));

            PurchaseReturnCreateResult purchaseReturnCreateResult = new PurchaseReturnCreateResult();

            purchaseReturnCreateResult.setStoreId(k);
            purchaseReturnCreateResult.setSerialNo(billPurchaseReturn.getSerialNo());
            purchaseReturnCreateResult.setCustomerId(billPurchaseReturn.getCustomerId());
            purchaseReturnCreateResult.setCustomerContactId(billPurchaseReturn.getCustomerContactId());


            //生成仓库
            List<StoreWorkCreateResult> collect = v.stream().map(t -> {
                StoreWorkCreateResult storeWorkCreateResult = new StoreWorkCreateResult();
                storeWorkCreateResult.setStockId(t.getStockId());
                storeWorkCreateResult.setGoodsId(t.getGoodsId());
                return storeWorkCreateResult;
            }).collect(Collectors.toList());

            purchaseReturnCreateResult.setStoreWorkList(collect);

            return purchaseReturnCreateResult;
        }).collect(Collectors.toList());
    }

    @NotNull
    private List<BillPurchaseReturnLine> getBillPurchaseReturnLines(List<PurchaseReturnCreateRequest.BillPurchaseReturnLineDto> v, BillPurchaseReturn billPurchaseReturn, List<Stock> stockList) {
        //构建返回参数
        List<BillPurchaseReturnLine> billPurchaseReturnLineList = v.stream().map(billPurchaseReturnLineDto -> {

            BillPurchaseReturnLine billPurchaseReturnLine = new BillPurchaseReturnLine();

            billPurchaseReturnLine.setPurchaseReturnId(billPurchaseReturn.getId());
            billPurchaseReturnLine.setStockId(billPurchaseReturnLineDto.getStockId());
            billPurchaseReturnLine.setOriginSerialNo(billPurchaseReturnLineDto.getOriginSerialNo());

            //退货类型
            billPurchaseReturnLine.setPurchaseReturnType(BusinessBillTypeEnum.fromValue(billPurchaseReturnLineDto.getPurchaseType()));
            billPurchaseReturnLine.setPurchaseSubjectId(billPurchaseReturnLineDto.getPurchaseSubjectId());

            billPurchaseReturnLine.setPurchaseReturnLineState(PurchaseReturnLineStateEnum.TO_BE_CONFIRMED);//采购退货行状态
            billPurchaseReturnLine.setPurchaseReturnPrice(billPurchaseReturnLineDto.getPurchaseReturnPrice());
            billPurchaseReturnLine.setRemark(billPurchaseReturnLineDto.getRemark());

            Stock stockFilter = stockList.stream().filter(stock -> stock.getId().equals(billPurchaseReturnLineDto.getStockId())).findAny().get();

            billPurchaseReturnLine.setPurchasePrice(stockFilter.getPurchasePrice());
            billPurchaseReturnLine.setLocationId(stockFilter.getLocationId());
            billPurchaseReturnLine.setStockSrc(stockFilter.getStockSrc());
            billPurchaseReturnLine.setPurchaseSubjectId(stockFilter.getSourceSubjectId());

            return billPurchaseReturnLine;
        }).collect(Collectors.toList());

        return billPurchaseReturnLineList;
    }

    @Override
    public PurchaseReturnCancelResult cancel(PurchaseReturnCancelRequest request) {

        BillPurchaseReturn purchaseReturn = baseMapper.selectById(request.getPurchaseReturnId());

        Assert.isFalse(ObjectUtils.isEmpty(purchaseReturn) || purchaseReturn.getPurchaseReturnState() != BusinessBillStateEnum.UNCONFIRMED, "不符合采购退货取消条件");

        //修改采购退货单状态
        BillPurchaseReturn billPurchaseReturn = new BillPurchaseReturn();
        billPurchaseReturn.setId(request.getPurchaseReturnId());
        billPurchaseReturn.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNCONFIRMED_TO_CANCEL_WHOLE);

        UpdateByIdCheckState.update(baseMapper, billPurchaseReturn);
        //查采购退货详情
        List<BillPurchaseReturnLine> lines = billPurchaseReturnLineMapper.selectList(Wrappers.<BillPurchaseReturnLine>lambdaQuery()
                .eq(BillPurchaseReturnLine::getPurchaseReturnId, request.getPurchaseReturnId()));
        //更新采购退货行
        lines.forEach(t -> {
            BillPurchaseReturnLine billPurchaseReturnLine = new BillPurchaseReturnLine();
            billPurchaseReturnLine.setId(t.getId());
            billPurchaseReturnLine.setPurchaseReturnLineState(PurchaseReturnLineStateEnum.CANCEL_WHOLE);
            billPurchaseReturnLineMapper.updateById(billPurchaseReturnLine);
        });
        PurchaseReturnCancelResult build = PurchaseReturnCancelResult.builder()
                .serialNo(purchaseReturn.getSerialNo())
                .line(lines.stream().map(billPurchaseReturnLine -> {
                    PurchaseReturnDetailsResult.PurchaseReturnLineVO purchaseReturnLineVO = new PurchaseReturnDetailsResult.PurchaseReturnLineVO();
                    purchaseReturnLineVO.setStockId(billPurchaseReturnLine.getStockId());
                    return purchaseReturnLineVO;
                }).collect(Collectors.toList()))
                .build();

        return build;
    }


    @Override
    public Page<PurchaseReturnListResult> page(PurchaseReturnListRequest request) {

        return this.baseMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
    }

    /**
     * 求两个集合交集，（forEasy）
     *
     * @param arr1
     * @param arr2
     * @return
     */
    private static List<Integer> intersectionForList(List<Integer> arr1, List<Integer> arr2) {
        List<Integer> resultList = new ArrayList<>();
        arr1.stream().forEach(a1 -> {
            if (arr2.contains(a1)) {
                resultList.add(a1);
            }
        });
        return resultList;
    }

    /**
     * 单号分组
     *
     * @param serialNo
     * @param size
     * @param offs
     * @return
     */
    private String groupSerialNo(String serialNo, int size, int offs) {
        if (size == NumberUtils.INTEGER_ONE) {
            return serialNo;
        }
        return serialNo + "-" + offs;
    }
}
