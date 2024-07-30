package com.seeease.flywheel.serve.sale.strategy;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.serve.base.*;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.enums.CustomerTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderTypeEnum;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 线上销售
 *
 * @author Tiro
 * @date 2023/3/24
 */
@Slf4j
@Component
public class SaleOrderStrategyOnLine extends SaleOrderStrategy {
    private final static List<Integer> ON_LINE_CHANNEL = Lists.newArrayList(SaleOrderChannelEnum.T_MALL.getValue()
            , SaleOrderChannelEnum.XI_YI_SHOP.getValue()
            , SaleOrderChannelEnum.DOU_YIN.getValue()
            , SaleOrderChannelEnum.KUAI_SHOU.getValue()
    );

    @Resource
    private CustomerService customerService;

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.TO_C_ON_LINE;
    }

    @Override
    void preRequestProcessing(SaleOrderCreateRequest request) {
        //订单号
        request.setParentSerialNo(SerialNoGenerator.generateToCSaleOrderSerialNo());
        //客户
        CustomerContacts customerContacts = Optional.ofNullable(request.getReceiverInfo())
                .map(t -> {
                    List<CustomerContacts> customerContactsList = customerContactsService
                            .list(Wrappers.<CustomerContacts>lambdaQuery()
                                    .eq(CustomerContacts::getName, t.getReceiverName())
                                    .eq(CustomerContacts::getPhone, t.getReceiverMobile())
                                    .eq(CustomerContacts::getAddress, t.getReceiverAddress())
                                    .orderByDesc(CustomerContacts::getId)
                            );
                    if (CollectionUtils.isNotEmpty(customerContactsList)) {
                        return customerContactsList.get(0);
                    }

                    //新建客户
                    Customer customer = new Customer();
                    customer.setCustomerName(t.getReceiverName());
                    customer.setType(CustomerTypeEnum.INDIVIDUAL);
                    customerService.save(customer);
                    CustomerContacts contacts = new CustomerContacts();
                    contacts.setCustomerId(customer.getId());
                    contacts.setAddress(t.getReceiverAddress());
                    contacts.setName(t.getReceiverName());
                    contacts.setPhone(t.getReceiverMobile());
                    customerContactsService.save(contacts);
                    return contacts;
                }).orElseThrow(() -> new BusinessException(ExceptionCode.OPT_NOT_SUPPORT));

        //设置客户联系人
        request.setCustomerId(customerContacts.getCustomerId());
        request.setCustomerContactId(customerContacts.getId());

        //Step:1 已知表身号商品信息补充(天猫国际废弃，后期预留)
        List<SaleOrderCreateRequest.BillSaleOrderLineDto> stockSnDetails = Optional.ofNullable(request.getDetails())
                .map(t -> t.stream()
                        .filter(i -> Objects.isNull(i.getStockId())) //无stockId
                        .filter(i -> StringUtils.isNotBlank(i.getStockSn())) // 有表身号
                        .collect(Collectors.toList()))
                .orElse(Collections.EMPTY_LIST);

        if (CollectionUtils.isNotEmpty(stockSnDetails)) {
            Map<String, List<Stock>> stockMap = Optional.ofNullable(stockSnDetails.stream()
                            .map(SaleOrderCreateRequest.BillSaleOrderLineDto::getStockSn)
                            .filter(StringUtils::isNoneBlank)
                            .collect(Collectors.toList()))
                    .filter(CollectionUtils::isNotEmpty)
                    .map(stockSnList -> stockService.list(Wrappers.<Stock>lambdaQuery()
                                    .in(Stock::getSn, stockSnList)
                                    .isNull(Stock::getTemp))
                            .stream()
                            .collect(Collectors.groupingBy(Stock::getSn)))
                    .orElse(Collections.EMPTY_MAP);

            Map<Integer, GoodsWatch> stockGoodsMap = Optional.ofNullable(stockMap)
                    .filter(MapUtils::isNotEmpty)
                    .map(Map::values)
                    .map(t -> t.stream()
                            .flatMap(Collection::stream)
                            .map(Stock::getGoodsId)
                            .collect(Collectors.toList()))
                    .map(t -> goodsWatchService.listByIds(t)
                            .stream()
                            .collect(Collectors.toMap(GoodsWatch::getId, Function.identity())))
                    .orElse(Collections.EMPTY_MAP);

            stockSnDetails.forEach(t -> {
                        Stock stock = Optional.ofNullable(stockMap.get(t.getStockSn()))
                                .orElse(Collections.emptyList())
                                .stream()
                                .sorted(Comparator.comparing(s -> {
                                    switch (s.getStockStatus()) {
                                        //可销售第一选择
                                        case MARKETABLE:
                                            return NumberUtils.INTEGER_ZERO;
                                        default:
                                            return NumberUtils.INTEGER_ONE;
                                    }
                                }))
                                .findFirst()
                                .orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.SN_NOT_EXIST));
                        //设置商品信息
                        t.setStockId(stock.getId());
                        t.setGoodsId(stock.getGoodsId());
                        t.setTobPrice(stock.getTobPrice());
                        t.setTocPrice(stock.getTocPrice());
                        t.setTagPrice(stock.getTagPrice());
                        t.setTotalPrice(stock.getTotalPrice());
                        t.setPricePub(Optional.ofNullable(stockGoodsMap.get(t.getGoodsId())).map(GoodsWatch::getPricePub).orElse(null));
                        t.setConsignmentPrice(stock.getConsignmentPrice());
                        t.setLocationId(stock.getLocationId());
                        t.setRightOfManagement(t.getRightOfManagement());
                    }
            );
        }

        //Step:2 已知型号编码商品信息补充
        List<SaleOrderCreateRequest.BillSaleOrderLineDto> modelCodeDetails = Optional.ofNullable(request.getDetails())
                .map(t -> t.stream()
                        .filter(i -> Objects.isNull(i.getStockId())) //无stockId
                        .filter(i -> Objects.isNull(i.getGoodsId())) //无goodsId
                        .filter(i -> StringUtils.isNotBlank(i.getModelCode())) // 有型号编码
                        .collect(Collectors.toList()))
                .orElse(Collections.EMPTY_LIST);

        if (CollectionUtils.isNotEmpty(modelCodeDetails)) {
            Map<String, GoodsWatch> goodsWatchMap = goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery()
                            .in(GoodsWatch::getModelCode, modelCodeDetails.stream()
                                    .map(SaleOrderCreateRequest.BillSaleOrderLineDto::getModelCode)
                                    .filter(StringUtils::isNoneBlank)
                                    .collect(Collectors.toList())))
                    .stream()
                    .collect(Collectors.toMap(GoodsWatch::getModelCode, Function.identity()));
            modelCodeDetails.forEach(t -> {
                GoodsWatch goods = Optional.ofNullable(goodsWatchMap.get(t.getModelCode()))
                        .orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.GOODS_MODEL_NOT_EXIST));

                t.setGoodsId(goods.getId());
                //针对直播a组设定，型号编码发货为指定门店发货
                t.setLocationId(FlywheelConstant._DF3_SHOP_ID);
                //滞后确认表身号
                t.setDelayStockSn(true);
            });
        }


        //Step:3 已知型号商品信息补充
        List<SaleOrderCreateRequest.BillSaleOrderLineDto> modelDetails = Optional.ofNullable(request.getDetails())
                .map(t -> t.stream()
                        .filter(i -> Objects.isNull(i.getStockId())) //无stockId
                        .filter(i -> Objects.isNull(i.getGoodsId())) //无goodsId
                        .filter(i -> StringUtils.isNotBlank(i.getModel())) // 有型号
                        .collect(Collectors.toList()))
                .orElse(Collections.EMPTY_LIST);

        if (CollectionUtils.isNotEmpty(modelDetails)) {
            Map<String, GoodsWatch> goodsMap = Optional.ofNullable(modelDetails.stream()
                            .map(SaleOrderCreateRequest.BillSaleOrderLineDto::getModel)
                            .filter(StringUtils::isNoneBlank)
                            .collect(Collectors.toList()))
                    .filter(CollectionUtils::isNotEmpty)
                    .map(modelList -> modelList.stream()
                            .map(StringTools::purification)
                            .collect(Collectors.toList()))
                    .map(simplifyModel -> goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery()
                                    .in(GoodsWatch::getSimplifyModel, simplifyModel))
                            .stream()
                            .collect(Collectors.toMap(GoodsWatch::getModel, Function.identity(), (k1, k2) -> k2)))
                    .orElse(Collections.EMPTY_MAP);

            Map<String, GoodsWatch> simplifyModelGoodsMap = goodsMap.values()
                    .stream()
                    .collect(Collectors.toMap(GoodsWatch::getSimplifyModel, Function.identity(), (k1, k2) -> k2));

            modelDetails.forEach(t -> {
                GoodsWatch goods = Optional.ofNullable(Optional.ofNullable(goodsMap.get(t.getModel()))
                                .orElse(simplifyModelGoodsMap.get(StringTools.purification(t.getModel()))))
                        .orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.GOODS_MODEL_NOT_EXIST));

                t.setGoodsId(goods.getId());
                //模版销售发货位置默认销售门店
                t.setLocationId(request.getShopId());
            });
        }

        //质保设置
        request.getDetails().forEach(
                t -> t.setWarrantyPeriod(SaleOrderChannelEnum.XI_YI_SHOP.getValue().intValue() == request.getSaleChannel() ?
                        5 : FlywheelConstant.two)
        );

    }

    @Override
    void checkRequest(SaleOrderCreateRequest request) throws BusinessException {
        Assert.isTrue(ON_LINE_CHANNEL.contains(request.getSaleChannel().intValue()), "渠道不支持");
        Assert.isTrue(SaleOrderModeEnum.ON_LINE.getValue().intValue() == request.getSaleMode(), "销售方式不支持");
        Assert.isTrue(SaleOrderTypeEnum.TO_C_XS.getValue().intValue() == request.getSaleType(), "销售类型不支持");

        //发货位置必须唯一
        if (request.getDetails().stream()
                .collect(Collectors.groupingBy(SaleOrderCreateRequest.BillSaleOrderLineDto::getLocationId)).size() != NumberUtils.INTEGER_ONE) {
            throw new OperationRejectedException(OperationExceptionCode.TOC_SALE_LOCATION_ID_ERROR);
        }

        //限制平台订单销售
        if (request.getDetails()
                .stream()
                .filter(t -> Objects.isNull(t.getStockId()) && StringUtils.isBlank(t.getModelCode())).count() > 0) {
            throw new OperationRejectedException(OperationExceptionCode.ONLINE_SALE_STOCK_ERROR);
        }

        request.getDetails().forEach(t -> {
            Assert.isTrue(Objects.nonNull(t.getStockId()) || StringUtils.isNoneBlank(t.getModel())
                    || StringUtils.isNoneBlank(t.getStockSn()) || StringUtils.isNoneBlank(t.getModelCode()), "预售商品不能为空");

            if (SaleOrderChannelEnum.DOU_YIN.getValue().intValue() == request.getSaleChannel() && Objects.nonNull(t.getStockId())) {
                saleBreakPriceCheck.toCSaleCheck(t);
            }
        });
    }

    @Override
    boolean allowSaleConfirm() {
        return true;
    }

    @Override
    boolean lockStock(SaleOrderCreateRequest request) throws RuntimeException {
        try {
            return super.lockStock(request);
        } catch (Exception e) {
            log.warn("线上订单锁定库存失败{}", e.getMessage(), e);
            return false;
        }
    }
}
