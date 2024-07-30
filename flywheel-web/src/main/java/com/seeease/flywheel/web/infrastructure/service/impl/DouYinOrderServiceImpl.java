package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doudian.open.api.btas_getInspectionOrder.BtasGetInspectionOrderRequest;
import com.doudian.open.api.btas_getInspectionOrder.BtasGetInspectionOrderResponse;
import com.doudian.open.api.btas_getInspectionOrder.data.BtasGetInspectionOrderData;
import com.doudian.open.api.btas_getInspectionOrder.data.ProductOrderDetailsItem;
import com.doudian.open.api.btas_getInspectionOrder.data.ProductOrdersItem;
import com.doudian.open.api.btas_getInspectionOrder.data.ScInfo;
import com.doudian.open.api.btas_getInspectionOrder.param.BtasGetInspectionOrderParam;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.sale.request.DouYinOrderConsolidationRequest;
import com.seeease.flywheel.sale.request.DouYinOrderListRequest;
import com.seeease.flywheel.sale.result.DouYinOrderConsolidationResult;
import com.seeease.flywheel.sale.result.DouYinOrderListResult;
import com.seeease.flywheel.web.common.context.DouYinConfig;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.entity.DouYinOrder;
import com.seeease.flywheel.web.entity.DouYinOrderLine;
import com.seeease.flywheel.web.entity.DouYinScInfo;
import com.seeease.flywheel.web.entity.enums.WhetherUseEnum;
import com.seeease.flywheel.web.infrastructure.mapper.DouYinOrderLineMapper;
import com.seeease.flywheel.web.infrastructure.mapper.DouYinOrderMapper;
import com.seeease.flywheel.web.infrastructure.mapper.DouYinScInfoMapper;
import com.seeease.flywheel.web.infrastructure.service.DouYinDecryptService;
import com.seeease.flywheel.web.infrastructure.service.DouYinOrderService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.BigDecimalUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author Tiro
 * @description 针对表【douyin_order(抖音订单)】的数据库操作Service实现
 * @createDate 2023-04-26 15:08:49
 */
@Service
public class DouYinOrderServiceImpl extends ServiceImpl<DouYinOrderMapper, DouYinOrder>
        implements DouYinOrderService {

    @Resource
    private DouYinOrderLineMapper douYinOrderLineMapper;
    @Resource
    private DouYinScInfoMapper douYinScInfoMapper;

    @Resource
    private DouYinDecryptService douYinDecryptService;

    /**
     * @param orderId
     * @return
     */
    @Override
    public DouYinOrder getByDouYinOrderId(String orderId) {
        return baseMapper.selectOne(Wrappers.<DouYinOrder>lambdaQuery()
                .eq(DouYinOrder::getOrderId, orderId));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DouYinOrder douYinOrder, List<DouYinOrderLine> lineList) {
        //创建订单
        baseMapper.insert(douYinOrder);
        //关联订单
        lineList.forEach(t -> t.setOrderId(douYinOrder.getId()));
        //批量创建订单行
        douYinOrderLineMapper.insertBatchSomeColumn(lineList);
    }

    @Override
    public PageResult<DouYinOrderListResult> queryPage(DouYinOrderListRequest request) {
        request.setShopId(UserContext.getUser().getStore().getId());
        Page<DouYinOrderListResult> page = baseMapper.listByRequest(Page.of(request.getPage(), request.getLimit()), request);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<DouYinOrderListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        return PageResult.<DouYinOrderListResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DouYinOrderConsolidationResult orderConsolidation(DouYinOrderConsolidationRequest request) {
        //校验是否是同一个客户
        List<DouYinOrder> douYinOrderList = baseMapper.selectList(new LambdaQueryWrapper<DouYinOrder>()
                .in(DouYinOrder::getId, request.getIds())
                .orderBy(true, true, DouYinOrder::getId));
        if (CollectionUtils.isEmpty(douYinOrderList)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_OPERATION);
        }
        if (!douYinOrderList.stream().allMatch(order -> order.getWhetherUse().equals(WhetherUseEnum.INIT))) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.DOU_YIN_ORDER_MUST_UNUSED);
        }
        if (!douYinOrderList.stream().allMatch(order -> order.getOrderStatus() == 2)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.DOU_YIN_ORDER_STATUS_NOT_ALLOW);
        }
        if (!douYinOrderList.stream().allMatch(order -> douYinOrderList.get(0).getMaskPostReceiver().equals(order.getMaskPostReceiver())
                && douYinOrderList.get(0).getMaskPostTel().equals(order.getMaskPostTel())
                && douYinOrderList.get(0).getShopId().equals(order.getShopId()))) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.DOU_YIN_ORDER_CONSOLIDATION_FAIL);
        }
        if (Objects.isNull(douYinOrderList.get(0).getDecryptPostTel()) &&
                Objects.isNull(douYinOrderList.get(0).getDecryptPostReceiver()) &&
                Objects.isNull(douYinOrderList.get(0).getDecryptAddrDetail())) {
            DouYinOrder orderDetail = douYinOrderList.get(0);
            Map<String, String> decryptMap = douYinDecryptService.orderDecrypt(orderDetail.getShopId(),
                    orderDetail.getDouYinShopId(),
                    orderDetail.getOrderId(),
                    Arrays.asList(orderDetail.getEncryptPostTel(), orderDetail.getEncryptPostReceiver(), orderDetail.getEncryptDetail()));

            douYinOrderList.forEach(douYinOrder -> {
                //--------解密信息--------
                douYinOrder.setDecryptPostTel(Optional.ofNullable(decryptMap.get(douYinOrder.getEncryptPostTel()))
                        .filter(StringUtils::isNotBlank)
                        .orElse(null));//解密收件人电话
                douYinOrder.setDecryptPostReceiver(Optional.ofNullable(decryptMap.get(douYinOrder.getEncryptPostReceiver()))
                        .filter(StringUtils::isNotBlank)
                        .orElse(null));//解密收件人姓名
                douYinOrder.setDecryptAddrDetail(Optional.ofNullable(decryptMap.get(douYinOrder.getEncryptDetail()))
                        .filter(StringUtils::isNotBlank)
                        .orElse(null));//解密收件地址
            });
            douYinOrderList.forEach(douYinOrder -> {
                DouYinOrder order = new DouYinOrder();
                order.setId(order.getId());
                order.setDecryptPostTel(order.getDecryptPostTel());
                order.setDecryptPostReceiver(order.getDecryptPostReceiver());
                order.setDecryptAddrDetail(douYinOrder.getDecryptAddrDetail());
                this.baseMapper.updateById(order);
            });
        }


        Map<String, List<ProductOrderDetailsItem>> itemMap = new HashMap<>();
        douYinOrderList.forEach(order -> {
            if (Objects.equals(order.getWhetherQuery(), WhetherEnum.NO.getValue())) {
                BtasGetInspectionOrderRequest r = new BtasGetInspectionOrderRequest();
                BtasGetInspectionOrderParam param = r.getParam();
                param.setOrderId(order.getOrderId());
                BtasGetInspectionOrderResponse response = r.execute(DouYinConfig.getAccessToken(order.getDouYinShopId()));
                BtasGetInspectionOrderData data = response.getData();
                itemMap.putAll(data.getProductOrders().stream()
                        .collect(Collectors.toMap(ProductOrdersItem::getProductOrderId, ProductOrdersItem::getProductOrderDetails)));
            }
        });

        List<DouYinOrderLine> douYinOrderLineList = douYinOrderLineMapper.selectList(new LambdaQueryWrapper<DouYinOrderLine>()
                .in(DouYinOrderLine::getOrderId, request.getIds()));
        if (CollectionUtils.isEmpty(douYinOrderLineList)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_OPERATION);
        }
        douYinOrderLineList.forEach(douYinOrderLine -> {
            if (StringUtils.isBlank(douYinOrderLine.getSpotCheckCode()) || StringUtils.isBlank(douYinOrderLine.getScAddress())) {
                List<ProductOrderDetailsItem> itemList = itemMap.getOrDefault(douYinOrderLine.getDouYinSubOrderId(),
                        new ArrayList<>());
                String spotCheckCode = itemList.stream().map(ProductOrderDetailsItem::getOrderCode)
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.joining(","));
                douYinOrderLine.setSpotCheckCode(spotCheckCode);

                ProductOrderDetailsItem detailsItem = itemList.stream().findFirst().orElse(null);

                DouYinOrderLine orderLine = new DouYinOrderLine();
                orderLine.setId(douYinOrderLine.getId());
                orderLine.setSpotCheckCode(spotCheckCode);
                if (Objects.nonNull(detailsItem)) {
                    ScInfo scInfo = detailsItem.getScInfo();
                    if (Objects.nonNull(scInfo)) {
                        DouYinScInfo douYinScInfo = douYinScInfoMapper.selectOne(new LambdaQueryWrapper<DouYinScInfo>()
                                .eq(DouYinScInfo::getScId, scInfo.getScId())
                                .eq(DouYinScInfo::getScAddress, scInfo.getScAddress()));
                        if (Objects.isNull(douYinScInfo)) {
                            douYinScInfo = new DouYinScInfo();
                            douYinScInfo.setScAddress(scInfo.getScAddress());
                            douYinScInfo.setScStreet(scInfo.getScStreet());
                            douYinScInfo.setScDistrict(scInfo.getScDistrict());
                            douYinScInfo.setScCity(scInfo.getScCity());
                            douYinScInfo.setScProvince(scInfo.getScProvince());
                            douYinScInfo.setScPhone(scInfo.getScPhone());
                            douYinScInfo.setScName(scInfo.getScName());
                            douYinScInfo.setScId(String.valueOf(scInfo.getScId()));
                            douYinScInfoMapper.insert(douYinScInfo);
                        }
                        orderLine.setScInfoId(douYinScInfo.getId());
                        orderLine.setScAddress(scInfo.getScAddress());
                        orderLine.setScStreet(scInfo.getScStreet());
                        orderLine.setScDistrict(scInfo.getScDistrict());
                        orderLine.setScCity(scInfo.getScCity());
                        orderLine.setScProvince(scInfo.getScProvince());
                        orderLine.setScPhone(scInfo.getScPhone());
                        orderLine.setScName(scInfo.getScName());
                        orderLine.setScId(String.valueOf(scInfo.getScId()));

                        douYinOrderLine.setScInfoId(douYinScInfo.getId());
                    }

                }
                douYinOrderLineMapper.updateById(orderLine);

                DouYinOrder douYinOrder = new DouYinOrder();
                douYinOrder.setId(douYinOrderLine.getOrderId());
                douYinOrder.setWhetherQuery(WhetherEnum.YES.getValue());
                baseMapper.updateById(douYinOrder);
            }
        });

        //根据客户进行合并相同型号的数据
        Map<List<String>, List<DouYinOrderLine>> map = douYinOrderLineList
                .stream().collect(Collectors.groupingBy(DouYinOrderLine::getGoodsModel));

        List<DouYinOrderLine> list = new ArrayList<>();
        for (List<DouYinOrderLine> lineList : map.values()) {
            Set<DouYinOrderLine> lineSet = lineList.stream().filter(d -> Objects.nonNull(d.getScInfoId())).collect(Collectors.toSet());
            //if (lineSet.size() > 1) {
            //    throw new OperationRejectedException(OperationExceptionCodeEnum.DOU_YIN_ORDER_STATUS_NOT_ALLOW);
            //}
            //因为合单 只需要将金额相加 数量每个型号只有一个
            BigDecimal totalOrderAmount = lineList.stream().map(DouYinOrderLine::getOrderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            String douYinSubOrderId = lineList.stream().map(DouYinOrderLine::getDouYinSubOrderId)
                    .collect(Collectors.joining(","));
            String spotCheckCode = lineList.stream().map(DouYinOrderLine::getSpotCheckCode)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(","));
//            long totalItemNum = lineList.stream().mapToLong(DouYinOrderLine::getItemNum).sum();
            DouYinOrderLine orderLine = lineList.get(0);
            orderLine.setItemNum(lineList.stream().mapToLong(DouYinOrderLine::getItemNum).max().getAsLong());
            orderLine.setDouYinSubOrderId(douYinSubOrderId);
            orderLine.setOrderAmount(totalOrderAmount);
            orderLine.setSpotCheckCode(spotCheckCode);
            orderLine.setScInfoId(lineSet.size() == 0 ? null : lineSet.stream().findFirst().get().getScInfoId());
            list.add(orderLine);
        }

        DouYinOrder douYinOrder = douYinOrderList.stream().findFirst().get();
        douYinOrder.setOrderId(douYinOrderList.stream().map(DouYinOrder::getOrderId)
                .collect(Collectors.joining(",")));
        douYinOrder.setBuyerWords(douYinOrderList.stream().map(DouYinOrder::getBuyerWords)
                .collect(Collectors.joining(",")));

        /*
         * 0 不质检 1-线下质检 2-线上质检
         */
        Integer inspectionType = list.stream()
                .filter(t -> StringUtils.isNotBlank(t.getSpotCheckCode()))
                .count() > 0 ? 2 : 0;

        //构建前端数据
        return DouYinOrderConsolidationResult.builder()
                .douYinOrderIds(request.getIds())
                .bizOrderCode(douYinOrder.getOrderId())
                .saleType(DouYinServiceImpl.SaleOrderTypeEnum.TO_C_XS.getValue())
                .saleMode(DouYinServiceImpl.SaleOrderModeEnum.ON_LINE.getValue())
                .saleChannel(DouYinServiceImpl.SaleOrderChannelEnum.DOU_YIN.getValue())
                .paymentMethod(DouYinServiceImpl.SaleOrderPaymentMethodEnum.DOU_YIN.getValue())
                .buyCause(WhetherEnum.NO.getValue())
                .remarks(Optional.ofNullable(douYinOrder.getBuyerWords())
                        .orElse(StringUtils.EMPTY)
                        + Optional.ofNullable(douYinOrder.getSellerWords())
                        .filter(StringUtils::isNotBlank)
                        .map(t -> String.format("[%s]", t))
                        .orElse(StringUtils.EMPTY)
                )
                .shopId(douYinOrder.getShopId())
                .encryptPostReceiver(douYinOrder.getEncryptPostReceiver())
                .encryptPostTel(douYinOrder.getEncryptPostTel())
                .encryptAddrArea(douYinOrder.getEncryptAddrArea())
                .encryptDetail(douYinOrder.getEncryptDetail())
                .accessToken(DouYinConfig.getAccessToken(douYinOrder.getDouYinShopId()).getAccessToken())
                .receiverInfo(DouYinOrderConsolidationResult.ReceiverInfo.builder()
                        .receiverName(Optional.ofNullable(douYinOrder.getDecryptPostReceiver()).orElse(douYinOrder.getMaskPostReceiver()))
                        .receiverMobile(Optional.ofNullable(douYinOrder.getDecryptPostTel()).orElse(douYinOrder.getMaskPostTel()))
                        .receiverAddress(Optional.ofNullable(douYinOrder.getDecryptAddrDetail())
                                .map(t -> StringUtils.join(Arrays.asList(douYinOrder.getProvince(), douYinOrder.getCity(), douYinOrder.getTown(), douYinOrder.getStreet() + t), "/"))
                                .orElse(douYinOrder.getMaskDetail()))
                        .build())
                .inspectionType(inspectionType)
                .details(list
                        .stream()
                        .filter(t -> BigDecimalUtil.gtZero(t.getOrderAmount()))
                        .map(t -> {
                            List<String> goodsModel = t.getGoodsModel();
                            if (CollectionUtils.isEmpty(goodsModel)) {
                                throw new RuntimeException("商品不存在");
                            }
                            BigDecimal subOrderAmount = t.getOrderAmount()
                                    .divide(new BigDecimal(goodsModel.size() * t.getItemNum()))
                                    .setScale(2, RoundingMode.HALF_UP);

                            List<DouYinOrderConsolidationResult.BillSaleOrderLineDto> res = goodsModel.stream()
                                    .map(model -> LongStream.range(0, t.getItemNum())
                                            .mapToObj(num -> DouYinOrderConsolidationResult.BillSaleOrderLineDto
                                                    .builder()
                                                    .subOrderCode(t.getDouYinSubOrderId())
                                                    .spotCheckCode(t.getSpotCheckCode())
                                                    .scInfoId(t.getScInfoId())
                                                    .modelCode(t.getModelCode())
                                                    .model(model)
                                                    .seriesName(StringUtils.isBlank(t.getModelCode()) ?
                                                            baseMapper.selectSeriesNameByModel(purification(model)) :
                                                            baseMapper.selectSeriesNameByModelCode(t.getModelCode()))
                                                    .brandName(StringUtils.isBlank(t.getModelCode()) ?
                                                            baseMapper.selectBrandNameByModel(purification(model)) :
                                                            baseMapper.selectBrandNameByModelCode(t.getModelCode()))
                                                    .clinchPrice(subOrderAmount)
                                                    .build())
                                            .collect(Collectors.toList())
                                    ).flatMap(Collection::stream)
                                    .collect(Collectors.toList());

                            res.get(0).setClinchPrice(t.getOrderAmount()
                                    .subtract(res.stream()
                                            .skip(1)
                                            .map(DouYinOrderConsolidationResult.BillSaleOrderLineDto::getClinchPrice)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add)));
                            return res;
                        })
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<DouYinOrder> selectListBySerialNo(List<String> serialNoList) {
        if (CollectionUtils.isEmpty(serialNoList))
            return null;
        return this.baseMapper.selectList(new LambdaQueryWrapper<DouYinOrder>()
                .in(DouYinOrder::getSerialNo, serialNoList));
    }

    @Override
    public String selectExpressNumberBySerialNo(String serialNo) {
        return this.baseMapper.selectExpressNumberBySerialNo(serialNo);
    }

    @Override
    public void backUseStep(String serialNo) {
        DouYinOrder order = this.baseMapper.selectOne(new LambdaQueryWrapper<DouYinOrder>()
                .eq(DouYinOrder::getWhetherUse, WhetherUseEnum.USE)
                .eq(DouYinOrder::getSerialNo, serialNo));
        if (Objects.isNull(order)) {
            return;
        }

        WhetherUseEnum whetherUse = null;

        if (order.getOrderStatus() == 2) {
            whetherUse = WhetherUseEnum.INIT;
        } else if (order.getOrderStatus() == 4) {
            whetherUse = WhetherUseEnum.CANCEL;
        } else {
            throw new RuntimeException("抖店订单状态异常，无法更新审核状态");
        }

        DouYinOrder up = new DouYinOrder();
        up.setId(order.getId());
        up.setWhetherUse(whetherUse);
        baseMapper.updateById(up);
    }

    private String purification(String str) {
        if (Objects.isNull(str)) {
            return null;
        }
        return str.trim().replaceAll("\\.", "");
    }
}




