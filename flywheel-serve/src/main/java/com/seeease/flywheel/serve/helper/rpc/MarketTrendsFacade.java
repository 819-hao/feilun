package com.seeease.flywheel.serve.helper.rpc;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.helper.IMarketTrendsFacade;
import com.seeease.flywheel.helper.result.MarketTrendsDetailResult;
import com.seeease.flywheel.helper.result.MarketTrendsSearchResult;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.helper.ai_image_model.AiImageModel;
import com.seeease.flywheel.serve.helper.enmus.MarketTrendsTimeRangeEnum;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderTypeEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@DubboService(version = "1.0.0")
public class MarketTrendsFacade implements IMarketTrendsFacade {
    @Resource
    private GoodsWatchService goodsWatchService;
    @NacosValue(value = "${flyWheelHelper.aiModel}", autoRefreshed = true)
    private String aiImageModel;
    @Resource
    private List<AiImageModel> aiImageModelList;
    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;
    @Resource
    private BillSaleOrderService billSaleOrderService;
    @Resource
    private StockService stockService;
    @Resource
    private BillPurchaseLineService billPurchaseLineService;

    private final Pattern pattern = Pattern.compile("\\((\\d{4})");

    @Override
    public PageResult<MarketTrendsSearchResult> search(Integer page, Integer limit, String q, String model) {
        Page<MarketTrendsSearchResult> iPage = goodsWatchService.pageGoodsForHelperSearch(page, limit, q, model);
        return PageResult.<MarketTrendsSearchResult>builder()
                .totalPage(iPage.getPages())
                .totalCount(iPage.getTotal())
                .result(iPage.getRecords())
                .build();
    }

    @Override
    public String aiModelMatch(byte[] fileBytes) {
        Assert.notEmpty(aiImageModelList, "暂无ai模型");
        AiImageModel currentModel = aiImageModelList.stream()
                .filter(v -> v.getModel().name().equals(aiImageModel))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ai 模型暂无匹配"));
        StopWatch stopWatch = new StopWatch("ai-image-model-match-task");
        stopWatch.start(String.format("model name %s start match", aiImageModel));
        String ret = currentModel.match(fileBytes);
        stopWatch.stop();
        return ret;
    }


    /**
     * 构造价格趋势列表
     * map结构 ：
     * <div>
     *     {
     *         stockId: Integer,
     *         createdTime: Date,
     *         price: bigDecimal
     *     }
     * </div>
     * 附件规则
     * 1、带【空白保卡】的排除；
     * 2、带【单表】划到附件【单表】的分类；
     * 3、带保卡上有年份【2022、2021、2020】的分别划到附件【22年全套、21年全套、20年全套】，其他带的年份划到附件【单表】上
     *
     * @return
     */
    private List<MarketTrendsDetailResult.PriceTrends> convert(List<JSONObject> maps) {
        if (maps.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> stockId = maps.stream().map(v -> v.getInteger("stockId")).collect(Collectors.toList());
        List<Stock> stocks = stockService.listByIds(stockId);
        if (stocks.isEmpty()) {
            log.warn("market  Trends convert stock not found  stockId:{}", stockId);
            return Collections.emptyList();
        }


       return   maps.stream()
                 //循环补充Map中的数据字段
                 .peek(map->{
                     Integer attachmentType = stocks.stream()
                             .filter(stock -> stock.getId().equals(map.getInteger("stockId")))
                             .findFirst()
                             .map(stock -> {
                                 String attachment = stock.getAttachment();
                                 /**
                                  *  根据附件分组
                                  * key = 1 单表
                                  * key = 2 20全套
                                  * key = 3 21全套
                                  * key = 4 22 全套
                                  * key = 0 忽略不计算
                                  */
                                 if (!StringUtils.isEmpty(attachment)) {
                                     if (attachment.contains("保卡")) {
                                         Matcher matcher = pattern.matcher(attachment);
                                         if (matcher.find()) {
                                             String group = matcher.group(1);
                                             if (group.endsWith("22")) {
                                                 return 4;
                                             } else if (group.endsWith("21")) {
                                                 return 3;
                                             } else if (group.endsWith("20")) {
                                                 return 2;
                                             }
                                         }
                                     }
                                 }
                                 return 1;
                             }).orElse(0);

                     map.put("type",attachmentType);
                 })
                 .filter(map -> map.getInteger("type") != 0)
                //根据时间分组
                 .collect(Collectors.groupingBy(map ->  DateUtils.clearTime(map.getDate("createdTime"))))
                 .values()
                 .stream()
                 .map(values ->{

                     MarketTrendsDetailResult.PriceTrends priceTrend = MarketTrendsDetailResult.PriceTrends
                             .builder()
                             .build();

                      values.stream()
                              //根据类型分组
                              .collect(Collectors.groupingBy(value -> value.getInteger("type")))
                              .forEach((type, value) -> {
                                  //取金额最大值
                                  value.stream()
                                          .max(Comparator.comparing(o -> o.getBigDecimal("price")))
                                          .ifPresent(max -> {
                                              String price = max.getBigDecimal("price").toPlainString();
                                              priceTrend.setDate(max.getDate("createdTime"));
                                              if (type == 1) {
                                                  priceTrend.setPriceSingle(price);
                                              } else if (type == 2) {
                                                  priceTrend.setPrice20(price);
                                              } else if (type == 3) {
                                                  priceTrend.setPrice21(price);
                                              } else {
                                                  priceTrend.setPrice22(price);
                                              }
                                          });
                              });
                      return priceTrend;
                 })
                 .sorted(Comparator.comparing(MarketTrendsDetailResult.PriceTrends::getDate))
                 .collect(Collectors.toList());

    }

    @Override
    public MarketTrendsDetailResult detail(Integer id, Integer timeRange) {
        MarketTrendsTimeRangeEnum enums = MarketTrendsTimeRangeEnum.of(timeRange);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS);

        String endTime = formatter.format(DateUtils.getEndTimeOfToday());
        String startTime =formatter.format(enums.getRangeStart().get());
        //个人销售数据
        LambdaQueryWrapper<BillSaleOrderLine> saleQw = Wrappers.<BillSaleOrderLine>lambdaQuery()
                .eq(BillSaleOrderLine::getGoodsId, id)
                .in(BillSaleOrderLine::getSaleLineState, Lists.newArrayList(SaleOrderLineStateEnum.DELIVERED, SaleOrderLineStateEnum.IN_RETURN, SaleOrderLineStateEnum.RETURN))
                .between(BillSaleOrderLine::getCreatedTime, startTime, endTime);
        List<BillSaleOrderLine> saleLines = billSaleOrderLineService.list(saleQw);
        List<JSONObject> saleMaps;
        if (saleLines.isEmpty()) {
            saleMaps = Collections.emptyList();
        } else {
            List<Integer> saleIds = saleLines.stream().map(BillSaleOrderLine::getSaleId).collect(Collectors.toList());
            List<Integer> tocSaleIds = billSaleOrderService.list(
                            Wrappers.<BillSaleOrder>lambdaQuery()
                                    .in(BillSaleOrder::getId, saleIds)
                                    .eq(BillSaleOrder::getSaleType, SaleOrderTypeEnum.TO_C_XS)
                    ).stream()
                    .map(BillSaleOrder::getId)
                    .collect(Collectors.toList());

            //移除tob销售单子
            saleLines.removeIf(v -> !tocSaleIds.contains(v.getSaleId()));

            saleMaps = saleLines.stream().map(v -> {
                JSONObject ret = new JSONObject();
                ret.put("stockId", v.getStockId());
                ret.put("createdTime", v.getCreatedTime());
                ret.put("price", v.getClinchPrice());
                return ret;
            }).collect(Collectors.toList());
        }


        //采购数据
        LambdaQueryWrapper<BillPurchaseLine> purchaseQw = Wrappers.<BillPurchaseLine>lambdaQuery()
                .eq(BillPurchaseLine::getGoodsId, id)
                .in(BillPurchaseLine::getPurchaseLineState, Lists.newArrayList(PurchaseLineStateEnum.WAREHOUSED, PurchaseLineStateEnum.IN_SETTLED))
                .between(BillPurchaseLine::getCreatedTime, startTime, endTime);

        List<BillPurchaseLine> purchaseLines = billPurchaseLineService.list(purchaseQw);

        List<JSONObject> purchaseMaps = purchaseLines.stream().map(v -> {
            JSONObject ret = new JSONObject();
            ret.put("stockId", v.getStockId());
            ret.put("createdTime", v.getCreatedTime());
            ret.put("price", v.getPurchasePrice());
            return ret;
        }).collect(Collectors.toList());

        return MarketTrendsDetailResult.builder()
                .saleTrends(convert(saleMaps))
                .purchaseTrends(convert(purchaseMaps))
                .build();
    }

}
