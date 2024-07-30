package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.sale.request.KuaiShouOrderConsolidationRequest;
import com.seeease.flywheel.sale.request.KuaiShouOrderListRequest;
import com.seeease.flywheel.sale.result.KuaiShouOrderConsolidationResult;
import com.seeease.flywheel.sale.result.KuaiShouOrderListResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.entity.KuaishouOrder;
import com.seeease.flywheel.web.entity.enums.WhetherUseEnum;
import com.seeease.flywheel.web.infrastructure.mapper.DouYinOrderMapper;
import com.seeease.flywheel.web.infrastructure.mapper.KuaishouOrderMapper;
import com.seeease.flywheel.web.infrastructure.service.KuaishouOrderService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dmmasxnmf
 * @description 针对表【kuaishou_order(快手订单)】的数据库操作Service实现
 * @createDate 2023-12-01 16:22:28
 */
@Service
public class KuaishouOrderServiceImpl extends ServiceImpl<KuaishouOrderMapper, KuaishouOrder> implements KuaishouOrderService {
    @Resource
    private DouYinOrderMapper douYinOrderMapper;

    @Override
    public KuaiShouOrderConsolidationResult orderConsolidation(KuaiShouOrderConsolidationRequest request) {

        //校验是否是同一个客户
        List<KuaishouOrder> kuaishouOrderList = baseMapper.selectList(new LambdaQueryWrapper<KuaishouOrder>().in(KuaishouOrder::getId, request.getIds()).orderBy(true, true, KuaishouOrder::getId));

        if (CollectionUtils.isEmpty(kuaishouOrderList)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_OPERATION);
        }

        if (!kuaishouOrderList.stream().allMatch(order -> StringUtils.isNotBlank(order.getModelCode()))) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.DOU_YIN_ORDER_MUST_UNUSED);
        }

        if (!kuaishouOrderList.stream().allMatch(order -> order.getWhetherUse().equals(WhetherUseEnum.INIT.getValue()))) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.DOU_YIN_ORDER_MUST_UNUSED);
        }

        //支付成功 快手状态值
        if (!kuaishouOrderList.stream().allMatch(order -> order.getOrderStatus() == 30)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.DOU_YIN_ORDER_STATUS_NOT_ALLOW);
        }

        KuaishouOrder kuaishouOrder = kuaishouOrderList.stream().findFirst().get();

        if (!kuaishouOrderList.stream().allMatch(order -> kuaishouOrder.getMaskPostReceiver().equals(order.getMaskPostReceiver()) && kuaishouOrder.getMaskPostTel().equals(order.getMaskPostTel()) && kuaishouOrder.getShopId().equals(order.getShopId()))) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.DOU_YIN_ORDER_CONSOLIDATION_FAIL);
        }

//        Map<String, String> modeCode = goodsWatchFacade.listByModeCode(kuaishouOrderList.stream().map(KuaishouOrder::getGoodsModel).distinct().collect(Collectors.toList()));

        //构建前端数据
        return KuaiShouOrderConsolidationResult.builder().kuaiShouOrderIds(request.getIds())
                //字符串拼接
                .bizOrderCode(kuaishouOrderList.stream().map(KuaishouOrder::getOrderId).collect(Collectors.joining(","))).saleType(KuaishouOrderServiceImpl.SaleOrderTypeEnum.TO_C_XS.getValue()).saleMode(KuaishouOrderServiceImpl.SaleOrderModeEnum.ON_LINE.getValue()).saleChannel(KuaishouOrderServiceImpl.SaleOrderChannelEnum.KUAI_SHOU.getValue()).paymentMethod(KuaishouOrderServiceImpl.SaleOrderPaymentMethodEnum.KUAI_SHOU.getValue()).buyCause(WhetherEnum.NO.getValue()).remarks(kuaishouOrderList.stream().map(KuaishouOrder::getBuyerWords).collect(Collectors.joining("&"))).shopId(kuaishouOrder.getShopId()).encryptAddrArea(kuaishouOrder.getEncryptAddrArea()).encryptPostTel(kuaishouOrder.getEncryptPostTel()).encryptPostReceiver(kuaishouOrder.getEncryptPostReceiver()).encryptDetail(kuaishouOrder.getEncryptDetail())
                .receiverInfo(KuaiShouOrderConsolidationResult.ReceiverInfo.builder().receiverName(Optional.ofNullable(kuaishouOrder.getDecryptPostReceiver()).orElse(kuaishouOrder.getMaskPostReceiver()))
                        .receiverMobile(Optional.ofNullable(kuaishouOrder.getDecryptPostTel()).orElse(kuaishouOrder.getMaskPostTel()))
                        .receiverAddress(Objects.nonNull(kuaishouOrder.getDecryptAddrDetail()) ? StringUtils.join(Stream.of(kuaishouOrder.getProvince(), kuaishouOrder.getCity(), kuaishouOrder.getTown(), kuaishouOrder.getStreet(), kuaishouOrder.getDecryptAddrDetail()).filter(Objects::nonNull).collect(Collectors.joining("/"))) : StringUtils.join(Stream.of(kuaishouOrder.getProvince(), kuaishouOrder.getCity(), kuaishouOrder.getTown(), kuaishouOrder.getStreet(), kuaishouOrder.getMaskDetail()).filter(Objects::nonNull).collect(Collectors.joining("/")))).build()).inspectionType(0).details(kuaishouOrderList.stream().map(order -> {

                    if (StringUtils.isBlank(order.getModelCode())) {
                        throw new RuntimeException("快手对应商品不存在");
                    }
                    return KuaiShouOrderConsolidationResult.BillSaleOrderLineDto.builder().clinchPrice(order.getOrderAmount()).subOrderCode(order.getOrderId()).modelCode(order.getModelCode())
                            //查商品
//                                    .model(modeCode.get(order.getGoodsModel()))
                            .seriesName(douYinOrderMapper.selectSeriesNameByModelCode(order.getModelCode())).brandName(douYinOrderMapper.selectBrandNameByModelCode(order.getModelCode())).build();
                }).collect(Collectors.toList())).build();
    }

    @Override
    public PageResult queryPage(KuaiShouOrderListRequest request) {

        request.setShopId(UserContext.getUser().getStore().getId());

        Page<KuaishouOrder> page = this.baseMapper.selectPage(new Page<>(request.getPage(), request.getLimit()), Wrappers.<KuaishouOrder>lambdaQuery().eq(Objects.nonNull(request.getShopId()), KuaishouOrder::getShopId, request.getShopId()).between(StringUtils.isNotBlank(request.getCreatedStartTime()) && StringUtils.isNotBlank(request.getCreatedEndTime()), KuaishouOrder::getCreatedTime, request.getCreatedStartTime(), request.getCreatedEndTime()).between(StringUtils.isNotBlank(request.getUsageStartTime()) && StringUtils.isNotBlank(request.getUsageEndTime()), KuaishouOrder::getUsageTime, request.getUsageStartTime(), request.getUsageEndTime()).eq(Objects.nonNull(request.getWhetherUse()) && !request.getWhetherUse().equals(-1), KuaishouOrder::getWhetherUse, request.getWhetherUse()).like(Objects.nonNull(request.getBizOrderCode()) && StringUtils.isNotBlank(request.getBizOrderCode()), KuaishouOrder::getOrderId, request.getBizOrderCode()).like(Objects.nonNull(request.getGoodsModel()) && StringUtils.isNotBlank(request.getGoodsModel()), KuaishouOrder::getGoodsModel, request.getGoodsModel()).like(Objects.nonNull(request.getDecryptPostTel()) && StringUtils.isNotBlank(request.getDecryptPostTel()), KuaishouOrder::getDecryptPostTel, request.getDecryptPostTel()).like(Objects.nonNull(request.getDecryptPostReceiver()) && StringUtils.isNotBlank(request.getDecryptPostReceiver()), KuaishouOrder::getDecryptPostReceiver, request.getDecryptPostReceiver()));

        PageResult.PageResultBuilder<KuaiShouOrderListResult> builder = PageResult.<KuaiShouOrderListResult>builder().totalCount(page.getTotal()).totalPage(page.getPages());

        if (page.getTotal() == 0L) {
            return builder.result(Collections.EMPTY_LIST).build();
        }

        return builder.result(page.getRecords().stream().map(i -> KuaiShouOrderListResult.builder().id(i.getId()).bizOrderCode(i.getOrderId()).decryptPostReceiver(StringUtils.isBlank(i.getDecryptPostReceiver()) ? i.getMaskPostReceiver() : i.getDecryptPostReceiver()).decryptPostTel(StringUtils.isBlank(i.getDecryptPostTel()) ? i.getMaskPostTel() : i.getDecryptPostTel()).whetherUse(i.getWhetherUse()).createdTime(Optional.ofNullable(i.getCreatedTime()).map(u -> DateFormatUtils.format(u, "yyyy-MM-dd HH:mm:ss")).orElse(null)).usageTime(Optional.ofNullable(i.getUsageTime()).map(u -> DateFormatUtils.format(u, "yyyy-MM-dd HH:mm:ss")).orElse(null)).orderAmount(i.getOrderAmount()).buyerWords(i.getBuyerWords()).goodsModel(i.getGoodsModel()).build()).collect(Collectors.toList())).build();
    }

    @Override
    public void backUseStep(String serialNo) {
        KuaishouOrder order = this.baseMapper.selectOne(new LambdaQueryWrapper<KuaishouOrder>()
                .eq(KuaishouOrder::getWhetherUse, WhetherUseEnum.USE)
                .eq(KuaishouOrder::getSerialNo, serialNo));
        if (Objects.isNull(order)) {
            return;
        }
        KuaishouOrder up = new KuaishouOrder();
        up.setId(order.getId());
        up.setWhetherUse(WhetherUseEnum.INIT.getValue());
        baseMapper.updateById(up);
    }

    @Getter
    @AllArgsConstructor
    enum SaleOrderChannelEnum {
        KUAI_SHOU(18, "快手"),
        ;
        private Integer value;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum SaleOrderPaymentMethodEnum {
        KUAI_SHOU(6, "快手"),
        ;
        private Integer value;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum SaleOrderModeEnum {
        ON_LINE(5, "平台"),
        ;
        private Integer value;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum SaleOrderTypeEnum {
        TO_B_JS(1, "同行寄售"), TO_C_XS(2, "个人销售"),
        ;
        private Integer value;
        private String desc;
    }
}




