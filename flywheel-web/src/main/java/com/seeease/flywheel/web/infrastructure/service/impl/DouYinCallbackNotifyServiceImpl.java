package com.seeease.flywheel.web.infrastructure.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doudian.open.api.btas_saveInspectionInfo.BtasSaveInspectionInfoRequest;
import com.doudian.open.api.btas_saveInspectionInfo.BtasSaveInspectionInfoResponse;
import com.doudian.open.api.btas_saveInspectionInfo.param.BtasSaveInspectionInfoParam;
import com.doudian.open.api.order_logisticsAddSinglePack.OrderLogisticsAddSinglePackRequest;
import com.doudian.open.api.order_logisticsAddSinglePack.OrderLogisticsAddSinglePackResponse;
import com.doudian.open.api.order_logisticsAddSinglePack.param.OrderLogisticsAddSinglePackParam;
import com.doudian.open.api.order_logisticsAddSinglePack.param.ShippedOrderInfoItem;
import com.doudian.open.core.AccessToken;
import com.google.common.collect.Lists;
import com.seeease.flywheel.notify.entity.TextRobotMessage;
import com.seeease.flywheel.web.common.context.DouYinConfig;
import com.seeease.flywheel.web.entity.BrandNotify;
import com.seeease.flywheel.web.entity.DouYinCallbackNotify;
import com.seeease.flywheel.web.entity.DouYinOrder;
import com.seeease.flywheel.web.entity.DouYinOrderLine;
import com.seeease.flywheel.web.entity.enums.WhetherNotifyEnum;
import com.seeease.flywheel.web.infrastructure.mapper.DouYinCallbackNotifyMapper;
import com.seeease.flywheel.web.infrastructure.mapper.DouYinOrderLineMapper;
import com.seeease.flywheel.web.infrastructure.notify.WxCpMessageFacade;
import com.seeease.flywheel.web.infrastructure.service.DouYinCallbackNotifyService;
import com.seeease.springframework.utils.StrFormatterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @description 针对表【douyin_callback_notify(抖音消息通知)】的数据库操作Service实现
 * @createDate 2023-04-25 16:57:01
 */
@Service
@Slf4j
public class DouYinCallbackNotifyServiceImpl extends ServiceImpl<DouYinCallbackNotifyMapper, DouYinCallbackNotify>
        implements DouYinCallbackNotifyService {
    @Resource
    private DouYinOrderLineMapper douYinOrderLineMapper;
    @Resource
    private WxCpMessageFacade wxCpMessageFacade;

    @NacosValue(value = "${robot.brandNotify:}", autoRefreshed = true)
    private String brandNotifyRobot;

    @Override
    public int insertBatchSomeColumn(List<DouYinCallbackNotify> douYinCallbackNotifyList) {
        return baseMapper.insertBatchSomeColumn(douYinCallbackNotifyList);
    }

    @Override
    public Map<Integer, WhetherNotifyEnum> deliveryNotify(List<DouYinOrder> douYinOrderList, String expressNumber) {
        Map<Integer, WhetherNotifyEnum> result = new HashMap<>();
        try {
            Assert.isTrue(CollectionUtils.isNotEmpty(douYinOrderList), "抖音订单不能为空");
            Assert.isTrue(StringUtils.isNotEmpty(expressNumber), "发货单号不能为空");

            List<DouYinOrderLine> douYinOrderLineList = douYinOrderLineMapper.selectList(new LambdaQueryWrapper<DouYinOrderLine>()
                    .in(DouYinOrderLine::getOrderId, douYinOrderList.stream().map(DouYinOrder::getId).collect(Collectors.toList())));

            Map<Integer, List<DouYinOrderLine>> orderLineMap = douYinOrderLineList.stream()
                    .collect(Collectors.groupingBy(DouYinOrderLine::getOrderId));

            //含抽检码的订单
            List<Integer> withCheckCodeOrder = douYinOrderLineList.stream()
                    .filter(t -> StringUtils.isNotBlank(t.getSpotCheckCode()))
                    .map(DouYinOrderLine::getOrderId)
                    .distinct()
                    .collect(Collectors.toList());

            //抽检和正常分组
            Map<Boolean, List<DouYinOrder>> orderMap = douYinOrderList.stream()
                    .collect(Collectors.groupingBy(t -> withCheckCodeOrder.contains(t.getId())));

            //平台抽检发货
            List<DouYinOrder> checkCodeOrderList = orderMap.get(Boolean.TRUE);
            if (CollectionUtils.isNotEmpty(checkCodeOrderList)) {
                checkCodeOrderList.forEach(order -> {
                    AccessToken accessToken = DouYinConfig.getAccessToken(order.getDouYinShopId());
                    List<DouYinOrderLine> lineList = orderLineMap.get(order.getId());
                    lineList.stream().forEach(withCheckCodeLine -> {
                        //抽检 送检
                        BtasSaveInspectionInfoRequest request = new BtasSaveInspectionInfoRequest();
                        BtasSaveInspectionInfoParam param = request.getParam();
                        param.setOrderId(order.getOrderId());
                        //首次录入时必输 商品单ID
                        param.setProductOrderId(withCheckCodeLine.getDouYinSubOrderId());
                        //首次录入时必输 一件商品一个订单码，订单码由字节系统生成。 订单码
                        param.setOrderCode(withCheckCodeLine.getSpotCheckCode());
                        //枚举值 1: 新增 2: 修改
                        param.setServiceStatus(1);
                        //送检方式 1：快递送检：通过物流快递将商品送到质检机构； 2：线下送检：非物流快递将商品送到质检机构；
                        // 当选择自发货时，必须使用线下送检 首次录入时用，不支持修改
                        param.setSendType(1);
                        //可以为空（线下送检时）；否则必输 物流公司ID，由接口/order/logisticsCompanyList返回的物流公司列表中对应的ID
                        param.setSendLogisticsId("1");
                        param.setSendLogisticsCompanyCode("shunfeng");
                        //可以为空（线下送检时）；否则必输 快递送检运单号
                        param.setSendLogisticsCode(expressNumber);
                        //0：否 即：质检完成后，质检机构将商品发货给顾客 1：是 即：质检完成后，商家拿回商品，自行发货给顾客；
                        // 或者是大商派人到商家侧完成质检，然后商家自行发货。 该字段与订单取消后流程有关，注意正确传参 首次录入时用，不支持修改
                        param.setIsShippingSelf(0);
                        //质检机构发货时的物流类型，可以为空（自发货时）；否则必输入。 1：顺丰标快； 2：顺丰特惠；
                        param.setShippingLogisticsType("1");
                        //可以为空（自发货时）；否则必输 质检机构代发货时，若发顾客物流需要保价，则录入保价金额，不能高于订单金额。若无需保价则为0 单位为分
                        param.setInsuranceCost(0);
                        //0：发货优先：线下送检，客户在质检方收货前申请退款，系统将在质检收货环节驳回退款，继续履约。
                        // 1：售后优先：线下送检，客户在质检方收货前申请退款，质检方将无法收货鉴定送检商品，并退回给商家（或等待商家处理完成退款申请再做质检）。
                        // 自发货时，建议选择正常发货，否则可能出现卡单
                        param.setReturnInterceptType(0);
                        //可以为空（自发货时）；否则必输 1：邮寄：若质检过程中订单取消，质检机构将商品邮寄回商家；
                        // 2：商家自提：若质检过程中订单取消，商家需要到质检机构处自己取回商品。
                        param.setReturnType(1);
                        BtasSaveInspectionInfoResponse res = request.execute(accessToken);

                        WhetherNotifyEnum notifyEnum = res.getCode().equals("10000") ? WhetherNotifyEnum.SUCCESS : WhetherNotifyEnum.FAIL;
                        result.put(order.getId(), notifyEnum);
                    });
                });
            }
            //正常发货
            List<DouYinOrder> orderList = orderMap.get(Boolean.FALSE);
            if (CollectionUtils.isNotEmpty(orderList)) {
                AccessToken accessToken = DouYinConfig.getAccessToken(orderList.stream().findFirst().get().getDouYinShopId());
                List<DouYinOrderLine> lineList = orderList.stream()
                        .map(t -> orderLineMap.get(t.getId()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
                //在不需要抽检的情况下 支持多个订单发同一个物流包裹
                //合单情况下 支持多个订单发同一个物流包裹
                OrderLogisticsAddSinglePackRequest request = new OrderLogisticsAddSinglePackRequest();
                OrderLogisticsAddSinglePackParam param = request.getParam();
                param.setCompanyCode("shunfeng");
                //父订单ID列表
                param.setOrderIdList(orderList.stream().map(DouYinOrder::getOrderId).collect(Collectors.toList()));
                List<ShippedOrderInfoItem> shippedOrderInfo = lineList.stream()
                        .map(orderLine -> {
                            ShippedOrderInfoItem item = new ShippedOrderInfoItem();
                            item.setShippedOrderId(orderLine.getDouYinSubOrderId());
                            item.setShippedNum(orderLine.getItemNum());
                            return item;
                        }).collect(Collectors.toList());
                //需要发货的子订单信息
                param.setShippedOrderInfo(shippedOrderInfo);
                //运单号
                param.setLogisticsCode(expressNumber);
                param.setRequestId(orderList.stream().findFirst().get().getSerialNo());
                OrderLogisticsAddSinglePackResponse res = request.execute(accessToken);

                WhetherNotifyEnum notifyEnum = res.getCode().equals("10000") ? WhetherNotifyEnum.SUCCESS : WhetherNotifyEnum.FAIL;
                orderList.forEach(order -> result.put(order.getId(), notifyEnum));
            }

        } catch (Exception e) {
            log.error("告知抖音发货异常{}", e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void brandNotify(List<Integer> stockIdList) {
        try {
            log.info("通知"+ JSONObject.toJSONString(stockIdList)+":"+brandNotifyRobot);
            if (CollectionUtils.isNotEmpty(stockIdList)) {
                return;
            }
            if (StringUtils.isBlank(brandNotifyRobot)) {
                return;
            }
            List<BrandNotify> notifyList = baseMapper.getBrandNotify(stockIdList);
            if (CollectionUtils.isEmpty(notifyList)) {
                return;
            }

            wxCpMessageFacade.send(TextRobotMessage.builder()
                    .key(brandNotifyRobot)
                    .text(TextRobotMessage.Text.builder()
                            .content("【到货通知】" + notifyList.stream()
                                    .map(t -> StrFormatterUtil.format("\n【{}：{}】", t.getName(), t.getNumber()))
                                    .collect(Collectors.joining()))
                            .mentioned_list(Lists.newArrayList("@all"))
                            .build())
                    .build());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}




