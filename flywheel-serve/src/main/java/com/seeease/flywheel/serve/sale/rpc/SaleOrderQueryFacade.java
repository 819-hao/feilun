package com.seeease.flywheel.serve.sale.rpc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.sale.ISaleOrderQueryFacade;
import com.seeease.flywheel.sale.entity.SaleOrder;
import com.seeease.flywheel.sale.entity.SaleOrderLine;
import com.seeease.flywheel.sale.request.SaleOrderAccuracyQueryRequest;
import com.seeease.flywheel.sale.request.SaleOrderOffsetBasedRequest;
import com.seeease.flywheel.sale.request.SaleOrderQueryRequest;
import com.seeease.flywheel.sale.result.SaleOrderOffsetBasedResult;
import com.seeease.flywheel.sale.result.SaleOrderQueryResult;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.flywheel.serve.sale.convert.SaleOrderConverter;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/2/20
 */
@Slf4j
@DubboService(version = "1.0.0")
public class SaleOrderQueryFacade implements ISaleOrderQueryFacade {
    private static final ReentrantLock lock = new ReentrantLock();

    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private UserService userService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private BillSaleOrderService billSaleOrderService;
    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;
    @Resource
    private StockService stockService;
    @Resource
    private GoodsWatchService goodsWatchService;


    @SneakyThrows
    @Override
    public SaleOrderOffsetBasedResult queryToCOrder(SaleOrderOffsetBasedRequest request) {

        if (lock.tryLock(10, TimeUnit.MINUTES)) {
            try {
                //限制拉取数量
                request.setLimit(Optional.ofNullable(request.getLimit())
                        .filter(t -> t <= 1000)
                        .orElse(1000));

                List<BillSaleOrder> orderList = billSaleOrderService.queryToCOrderByOffset(request.getCurrentOffset(), request.getLimit());

                if (CollectionUtils.isEmpty(orderList)) {
                    return SaleOrderOffsetBasedResult.builder()
                            .saleOrderList(Collections.EMPTY_LIST)
                            .currentOffset(request.getCurrentOffset())
                            .isEnd(true)
                            .build();
                }
                int maxOffset = billSaleOrderService.maxToCOrderByOffset(request.getCurrentOffset());
                int currentOffset = orderList.stream()
                        .mapToInt(BillSaleOrder::getId)
                        .max().getAsInt();


                return SaleOrderOffsetBasedResult.builder()
                        .saleOrderList(this.convert(orderList))
                        .currentOffset(currentOffset)
                        .isEnd(currentOffset >= maxOffset)
                        .build();
            } finally {
                lock.unlock();
            }
        } else {
            throw new RuntimeException("拉取频率过高，获取锁失败");
        }
    }

    @Override
    public SaleOrderQueryResult queryToCOrder(SaleOrderQueryRequest request) {
        List<BillSaleOrder> orderList = billSaleOrderService.queryToCOrderByFinisTime(request.getFinisTime());

        return SaleOrderQueryResult.builder()
                .saleOrderList(this.convert(orderList))
                .build();
    }

    @Override
    public SaleOrderQueryResult queryToCOrder(SaleOrderAccuracyQueryRequest request) {
        List<BillSaleOrder> orderList = billSaleOrderService.queryToCOrderByRequest(request);

        return SaleOrderQueryResult.builder()
                .saleOrderList(this.convert(orderList))
                .build();
    }


    /**
     * @param orderList
     * @return
     */
    private List<SaleOrder> convert(List<BillSaleOrder> orderList) {
        if (CollectionUtils.isEmpty(orderList)) {
            return Collections.EMPTY_LIST;
        }

        Map<Integer, CustomerContacts> customerMap = Optional.ofNullable(orderList.stream()
                        .map(BillSaleOrder::getCustomerContactId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(customerContactsService::listByIds)
                .map(t -> t.stream()
                        .collect(Collectors.toMap(CustomerContacts::getId, Function.identity(), (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);


        Map<Integer, String> userMap = Optional.ofNullable(orderList.stream()
                        .map(t -> Arrays.asList(t.getFirstSalesman(), t.getSecondSalesman(), t.getThirdSalesman()))
                        .flatMap(Collection::stream)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(userService::listByIds)
                .map(t -> t.stream().collect(Collectors.toMap(s -> s.getId().intValue(), User::getUserid, (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);


        Map<Integer, String> shopMap = Optional.ofNullable(orderList.stream()
                        .map(BillSaleOrder::getShopId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(storeManagementService::selectInfoByIds)
                .map(t -> t.stream()
                        .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName, (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);


        List<BillSaleOrderLine> orderLineList = billSaleOrderLineService.list(Wrappers.<BillSaleOrderLine>lambdaQuery()
                .in(BillSaleOrderLine::getSaleId, orderList.stream()
                        .map(BillSaleOrder::getId)
                        .collect(Collectors.toList())));

        List<Integer> stockIdList = orderLineList.stream()
                .map(BillSaleOrderLine::getStockId)
                .collect(Collectors.toList());

        Map<Integer, Stock> stockMap = stockService.listByIds(stockIdList)
                .stream()
                .collect(Collectors.toMap(Stock::getId, Function.identity(), (k1, k2) -> k2));

        Map<Integer, WatchDataFusion> goodsMap = goodsWatchService.getWatchDataFusionListByStockIds(stockIdList)
                .stream()
                .collect(Collectors.toMap(WatchDataFusion::getStockId, Function.identity(), (k1, k2) -> k2));

        Map<Integer, List<BillSaleOrderLine>> lineMap = orderLineList
                .stream()
                .collect(Collectors.groupingBy(BillSaleOrderLine::getSaleId));

        return orderList.stream()
                .map(t -> {
                    SaleOrder order = SaleOrderConverter.INSTANCE.convertSaleOrder(t);

                    //设置门店信息
                    order.setShopName(shopMap.get(t.getShopId()));
                    //设置客户信息
                    if (customerMap.containsKey(t.getCustomerContactId())) {
                        CustomerContacts customer = customerMap.get(t.getCustomerContactId());
                        order.setCustomerName(customer.getName());
                        order.setCustomerPhone(customer.getPhone());
                        order.setCustomerAddress(customer.getAddress());
                    }
                    //设置销售人
                    order.setFirstSalesman(userMap.get(t.getFirstSalesman()));
                    order.setSecondSalesman(userMap.get(t.getSecondSalesman()));
                    order.setThirdSalesman(userMap.get(t.getThirdSalesman()));

                    //设置订单行
                    order.setOrderLines(lineMap.get(order.getOrderId())
                            .stream()
                            .map(s -> {
                                SaleOrderLine line = SaleOrderConverter.INSTANCE.convertSaleOrderLine(s);

                                WatchDataFusion goods = goodsMap.get(line.getStockId());
                                line.setBrandName(goods.getBrandName());
                                line.setSeriesName(goods.getSeriesName());
                                line.setModel(goods.getModel());
                                line.setPricePub(goods.getPricePub());
                                line.setMovement(goods.getMovement());

                                Stock stock = stockMap.get(line.getStockId());
                                line.setStockSn(stock.getSn());
                                line.setTocPrice(stock.getTocPrice());
                                line.setTagPrice(stock.getTagPrice());
                                line.setFiness(stock.getFiness());

                                if (WhetherEnum.YES.getValue().equals(s.getIsCounterPurchase())) {
                                    line.setWhitBuyBackPolicy(true);
                                    line.setBuyBackPolicyList(SaleOrderConverter.INSTANCE.convertBuyBackPolicyList(s.getBuyBackPolicy()));
                                }
                                return line;
                            })
                            .collect(Collectors.toList())
                    );
                    return order;
                })
                .collect(Collectors.toList());
    }

}
