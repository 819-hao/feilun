package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kuaishou.merchant.open.api.KsMerchantApiException;
import com.kuaishou.merchant.open.api.client.AccessTokenKsMerchantClient;
import com.kuaishou.merchant.open.api.client.oauth.OauthAccessTokenKsClient;
import com.kuaishou.merchant.open.api.common.utils.PlatformEventSecurityUtil;
import com.kuaishou.merchant.open.api.domain.order.*;
import com.kuaishou.merchant.open.api.domain.refund.MerchantRefundDetailDataView;
import com.kuaishou.merchant.open.api.request.order.OpenOrderDetailRequest;
import com.kuaishou.merchant.open.api.request.refund.OpenSellerOrderRefundDetailRequest;
import com.kuaishou.merchant.open.api.response.oauth.KsAccessTokenResponse;
import com.kuaishou.merchant.open.api.response.order.OpenOrderDetailResponse;
import com.kuaishou.merchant.open.api.response.refund.OpenSellerOrderRefundDetailResponse;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.goods.IGoodsWatchFacade;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.sale.ISaleReturnOrderFacade;
import com.seeease.flywheel.sale.request.KuaiShouOrderConsolidationRequest;
import com.seeease.flywheel.sale.request.KuaiShouOrderListRequest;
import com.seeease.flywheel.web.common.context.KuaiShouConfig;
import com.seeease.flywheel.web.common.work.executor.QueryCmdExe;
import com.seeease.flywheel.web.common.work.executor.SubmitCmdExe;
import com.seeease.flywheel.web.common.work.flow.WorkflowStateEnum;
import com.seeease.flywheel.web.entity.*;
import com.seeease.flywheel.web.entity.enums.WhetherUseEnum;
import com.seeease.flywheel.web.entity.kuaishou.KuaiShouMessageBody;
import com.seeease.flywheel.web.entity.kuaishou.KuaiShouPayFailInfo;
import com.seeease.flywheel.web.entity.kuaishou.KuaiShouPaySuccessInfo;
import com.seeease.flywheel.web.infrastructure.service.*;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.utils.BigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.repository.query.Param;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 快手订单同步
 *
 * @author Tiro
 * @date 2023/4/25¬
 */
@Slf4j
@RestController
@RequestMapping("/kuaishou/")
public class KuaiShouController {

    @DubboReference(check = false, version = "1.0.0")
    private IGoodsWatchFacade goodsWatchFacade;

    @Resource
    private KuaishouAppInfoService kuaishouAppInfoService;

    @Resource
    private KuaishouTokenInfoService kuaishouTokenInfoService;

    @Resource
    private KuaishouShopMappingService kuaishouShopMappingService;

    @Resource
    private KuaishouOrderService kuaishouOrderService;

    @Resource
    private KuaiShouService kuaiShouService;

    @Resource
    private KuaishouOrderRefundService kuaishouOrderRefundService;

    @Resource
    private DouYinShopMappingService douYinShopMappingService;
    @Resource
    private DouYinCallbackNotifyService douYinCallbackNotifyService;
    @Resource
    private DouYinOrderService douYinOrderService;
    @Resource
    private DouYinOrderLineService douYinOrderLineService;
    @Resource
    private DouYinService douYinService;
    @Resource
    private DouYinOrderRefundService douYinOrderRefundService;
    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;
    @Resource
    private DouYinDecryptService douYinDecryptService;
    @DubboReference(check = false, version = "1.0.0")
    private ISaleReturnOrderFacade facade;
    @Resource
    private QueryCmdExe workDetailsCmdExe;
    @Resource
    private SubmitCmdExe workflowCmdExe;

    @Resource
    private KuaishouCallbackNotifyService kuaiShouCallbackNotifyService;

//    private static final Set<String> EVENT = ImmutableSet.of(
//            KuaiShouMessageBody.KuaiShouMessageBodyEvent.REFUND_CREATED.getValue()
//            , KuaiShouMessageBody.KuaiShouMessageBodyEvent.REFUND_MODIFIED.getValue()
//            , KuaiShouMessageBody.KuaiShouMessageBodyEvent.REFUND_CLOSED.getValue()
//    );


    /**
     * 业务授权
     *
     * @param code
     * @param state
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/redirect/{appKey}")
    public void redirect(@Param("code") String code, @Param("state") String state, @PathVariable String appKey, HttpServletRequest request, HttpServletResponse response) {

        //appKey 代表不同的应用
        //state 代表不同业务 授权不同的业务
        log.info("code={},appKey={},state={}", code, appKey, state);

        List<KuaishouAppInfo> kuaishouAppInfoList = kuaishouAppInfoService.list(Wrappers.<KuaishouAppInfo>lambdaQuery().eq(KuaishouAppInfo::getAppId, appKey));

        if (CollectionUtils.isEmpty(kuaishouAppInfoList)) {
            return;
        }

        KuaishouAppInfo kuaishouAppInfo = kuaishouAppInfoList.get(FlywheelConstant.INDEX);

        try {

            OauthAccessTokenKsClient oauthAccessTokenKsClient = new OauthAccessTokenKsClient(kuaishouAppInfo.getAppId(), kuaishouAppInfo.getAppSecret());

            KsAccessTokenResponse ksAccessTokenResponse = oauthAccessTokenKsClient.getAccessToken(code);

            int result = ksAccessTokenResponse.getResult();

            if (result != 1) {
                log.error("result={},error={},error_msg ={}", ksAccessTokenResponse.getResult(), ksAccessTokenResponse.getError(), ksAccessTokenResponse.getErrorMsg());
                log.error("code授权失败");
                return;
            }

            List<KuaishouTokenInfo> kuaishouTokenInfoList = kuaishouTokenInfoService.list(Wrappers.<KuaishouTokenInfo>lambdaQuery().eq(KuaishouTokenInfo::getAppId, appKey).eq(KuaishouTokenInfo::getOpenId, ksAccessTokenResponse.getOpenId()));

            KuaishouTokenInfo tokenInfo = new KuaishouTokenInfo();

            if (CollectionUtils.isNotEmpty(kuaishouTokenInfoList)) {
                KuaishouTokenInfo kuaishouTokenInfo = kuaishouTokenInfoList.get(FlywheelConstant.INDEX);
                tokenInfo.setId(kuaishouTokenInfo.getId());
            }

            tokenInfo.setAppId(appKey);
            tokenInfo.setOpenId(ksAccessTokenResponse.getOpenId());
            tokenInfo.setAccessToken(ksAccessTokenResponse.getAccessToken());
            tokenInfo.setExpiresIn(ksAccessTokenResponse.getExpiresIn());
            tokenInfo.setAccessTokenExpiresTime(System.currentTimeMillis() + (ksAccessTokenResponse.getExpiresIn() - 200L) * 1000L);

            tokenInfo.setRefreshToken(ksAccessTokenResponse.getRefreshToken());
            Long refreshTokenExpiresIn = Optional.of(ksAccessTokenResponse.getRefreshTokenExpiresIn()).orElse(180L * 24L * 3600L);
            tokenInfo.setRefreshTokenExpiresIn(refreshTokenExpiresIn);
            tokenInfo.setRefreshTokenExpiresTime(System.currentTimeMillis() + (refreshTokenExpiresIn - 200L) * 1000L);

            kuaishouTokenInfoService.saveOrUpdate(tokenInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

//    /**
//     * 查询抖音店铺订单详情
//     *
//     * @param orderId
//     * @return
//     */
//    @PostMapping("/queryDetail")
//    public SingleResponse queryDetail(@RequestParam("orderId") String orderId, @RequestParam("shopId") Long shopId) {
//        OrderOrderDetailRequest request = new OrderOrderDetailRequest();
//        OrderOrderDetailParam param = request.getParam();
//        param.setShopOrderId(orderId);
//        return SingleResponse.of(request.execute(DouYinConfig.getAccessToken(shopId)));
//    }

//    /**
//     * 商家_查询订单的质检信息
//     *
//     * @param orderId
//     * @return
//     */
//    @PostMapping("/getInspectionOrder")
//    public SingleResponse getInspectionOrder(@RequestParam("orderId") String orderId, @RequestParam("shopId") Long shopId) {
//        BtasGetInspectionOrderRequest request = new BtasGetInspectionOrderRequest();
//        BtasGetInspectionOrderParam param = request.getParam();
//        param.setOrderId(orderId);
//        return SingleResponse.of(request.execute(DouYinConfig.getAccessToken(shopId)));
//    }

    /**
     * 快手拆单合并
     *
     * @param request
     * @return
     */
    @PostMapping("/orderConsolidation")
    public SingleResponse orderConsolidation(@RequestBody KuaiShouOrderConsolidationRequest request) {
        Assert.isTrue(CollectionUtils.isNotEmpty(request.getIds()), "id不能为空");
        return SingleResponse.of(kuaishouOrderService.orderConsolidation(request));
    }

    /**
     * 查询pc快手列表
     *
     * @param request
     * @return
     */
    @PostMapping("/queryPage")
    public SingleResponse queryPage(@RequestBody KuaiShouOrderListRequest request) {

        return SingleResponse.of(kuaishouOrderService.queryPage(request));
    }


    /**
     * 快手消息同步
     *
     * @param appKey 业务应用
     * @param body   消息体
     * @return
     */
    @PostMapping(value = "/callback/receive/{appKey}")
    public Map<String, Object> receive(@PathVariable String appKey, @RequestBody String body) throws KsMerchantApiException {

        //成功响应
        Map<String, Object> result = new HashMap<>(1);
        result.put("result", 1);

        log.info("快手消息入参：body={},appKey={}", body, appKey);

        if (StringUtils.isBlank(body)) {
            return result;
        }

        KuaishouAppInfo kuaishouAppInfo = kuaishouAppInfoService.getOne(Wrappers.<KuaishouAppInfo>lambdaQuery().eq(KuaishouAppInfo::getAppId, appKey));

        Assert.isTrue(Objects.nonNull(kuaishouAppInfo), "快手消息通知应用不存在");

        String decryptString = PlatformEventSecurityUtil.decode(body, kuaishouAppInfo.getEncodingAesKey());

        Assert.isTrue(StringUtils.isNotBlank(decryptString), "消息解密失败");

        log.info("消息解密信息:{}", decryptString);

        JSONObject msgBodyJsonObject = JSONObject.parseObject(decryptString);

        if (Optional.ofNullable(msgBodyJsonObject.getBoolean("test")).orElse(false)) {
            return result;
        }

        KuaiShouMessageBody kuaiShouMessageBody = JSON.parseObject(decryptString, KuaiShouMessageBody.class);

        KuaishouCallbackNotify kuaishouCallbackNotify = new KuaishouCallbackNotify();

        kuaishouCallbackNotify.setState(WorkflowStateEnum.INIT.getValue());
        kuaishouCallbackNotify.setMsgId(kuaiShouMessageBody.getMsgId());
        kuaishouCallbackNotify.setEventId(kuaiShouMessageBody.getEventId());
        kuaishouCallbackNotify.setBizId(kuaiShouMessageBody.getBizId());
        kuaishouCallbackNotify.setUserId(kuaiShouMessageBody.getUserId());
        kuaishouCallbackNotify.setOpenId(kuaiShouMessageBody.getOpenId());
        kuaishouCallbackNotify.setAppKey(kuaiShouMessageBody.getAppKey());
        kuaishouCallbackNotify.setEvent(kuaiShouMessageBody.getEvent());
        kuaishouCallbackNotify.setStatus(kuaiShouMessageBody.getStatus());
        kuaishouCallbackNotify.setCreateTime(kuaiShouMessageBody.getCreateTime());
        kuaishouCallbackNotify.setUpdateTime(kuaiShouMessageBody.getUpdateTime());
        kuaishouCallbackNotify.setInfo(kuaiShouMessageBody.getInfo());

        JSONObject infoJsonObject = JSONObject.parseObject(kuaiShouMessageBody.getInfo());
        kuaishouCallbackNotify.setKuaiShouSellerId(infoJsonObject.getLong("sellerId"));
        kuaishouCallbackNotify.setKuaiShouOrderId(String.valueOf(infoJsonObject.getLong("oid")));

        if (Objects.isNull(kuaishouCallbackNotify.getKuaiShouOrderId())) {
            kuaishouCallbackNotify.setKuaiShouOrderId(String.valueOf(infoJsonObject.getLong("orderId")));
        }

        kuaiShouCallbackNotifyService.save(kuaishouCallbackNotify);
        //处理消息
        handleMessage(kuaishouCallbackNotify, kuaishouAppInfo);

        return result;
    }

    /**
     * 处理消息
     *
     * @param notify
     * @param kuaishouAppInfo
     */
    private void handleMessage(KuaishouCallbackNotify notify, KuaishouAppInfo kuaishouAppInfo) {

        KuaishouCallbackNotify up = new KuaishouCallbackNotify();
        up.setId(notify.getId());
        up.setState(WorkflowStateEnum.COMPLETE.getValue());
        try {
            JSONObject info = JSONObject.parseObject(notify.getInfo());
            switch (KuaiShouMessageBody.KuaiShouMessageBodyEvent.fromCode(notify.getEvent())) {

                // 订单支付成功
                // 创建订单
                case TRADE_PAID:

                    if (Objects.requireNonNull(KuaiShouPaySuccessInfo.KuaiShouPaySuccessInfoStatus.fromCode(info.getInteger("status"))) == KuaiShouPaySuccessInfo.KuaiShouPaySuccessInfoStatus.TRADE_PAID) {
                        this.createOrder(notify, kuaishouAppInfo);
                    } else {
                        log.warn("订单支付成功,未知消息状态,{}", info.getInteger("status"));
                    }

                    break;
                // 订单交易失败
                // 待付款-->没有快手订单
                // 待发货-->有快手订单 && (已审核或者未审核) || 还有发国检
                // 已发货
                // 已签收
                // todo 只负责把订单状态从待审核变成已取消
                case TRADE_CANCELED:
                    if (Objects.requireNonNull(KuaiShouPayFailInfo.KuaiShouPayFailInfoStatus.fromCode(info.getInteger("status"))) == KuaiShouPayFailInfo.KuaiShouPayFailInfoStatus.REFUND_MODIFIED) {
                        this.cancelOrder(notify, kuaishouAppInfo);
                    } else {
                        log.warn("订单交易失败,未知消息状态,{}", info.getInteger("status"));
                    }
                    break;
                //售后更新消息
                //根据售后ID查询售后单详情，当前未单独返回“售后状态”字段，“售后状态”由出参中的status(退款状态)和negotiateStatus(协商状态)组合进行识别。
                case RETURN_APPLY_AGREED:
                    // 退款方式，枚举： [1, "退货退款"] [10, "仅退款"] [3, "换货"][4, "补寄"][5, "维修"]
                    if (Arrays.asList(1, 10).contains(info.getInteger("handlingWay"))) {
                        this.refundOrder(notify, kuaishouAppInfo);
                    } else {
                        //更新消息
                        log.warn("售后更新消息,{}", JSONObject.toJSONString(info));
                        this.refundCreated(notify);
                    }
                    break;
                //售后新增消息
                case REFUND_AGREED:
                    log.warn("售后新增消息,{}", JSONObject.toJSONString(info));
                    this.refundCreated(notify);
                    break;
                default:
                    log.warn("快手消息->未知消息种类,{}", JSONObject.toJSONString(notify));
                    break;
            }

        } catch (Exception e) {
            log.error("快手消息处理异常：{}", e.getMessage(), e);
            up.setState(WorkflowStateEnum.ERROR.getValue());
            up.setErrorReason(e.getMessage());
        } finally {
            kuaiShouCallbackNotifyService.updateById(up);
        }
    }

    /**
     * 买家发起售后申请消息 插入dou_yin_refund_created数据
     *
     * @param data
     */
    private void refundCreated(KuaishouCallbackNotify data) {

        //详细业务参数
        JSONObject jsonObject = JSONObject.parseObject(data.getInfo());

        //新增或者更新
        KuaishouOrderRefund created = kuaishouOrderRefundService.getOne(new LambdaQueryWrapper<KuaishouOrderRefund>()
                .eq(KuaishouOrderRefund::getRefundOrderId, String.valueOf(jsonObject.getLong("refundId")))
        );

        KuaishouOrderRefund kuaishouOrderRefund = new KuaishouOrderRefund();
        kuaishouOrderRefund.setId(Optional.ofNullable(created).map(KuaishouOrderRefund::getId).orElse(null));
        kuaishouOrderRefund.setKuaiShouShopId(data.getUserId());
        kuaishouOrderRefund.setRefundOrderId(String.valueOf(jsonObject.getLong("refundId")));
        kuaishouOrderRefund.setOrderId(String.valueOf(jsonObject.getLong("orderId")));
        kuaishouOrderRefund.setOrderSubId(String.valueOf(jsonObject.getLong("orderId")));
        //退款方式，枚举： [1, "退货退款"] [10, "仅退款"] [3, "换货"][4, "补寄"][5, "维修"]
        kuaishouOrderRefund.setRefundType(jsonObject.getInteger("handlingWay"));
        //订单退款状态[0, "未知状态"],[10, "买家仅退款申请"],[11, "买家退货退款申请"],[20, "平台介入-买家仅退款申请"],
        // [21, "平台介入-买家退货退款申请"],[22, "平台介入-已确认退货退款"],[23, "平台介入-待买家确认收货"],[30, "商品回寄信息待买家更新"],
        // [40, "商品回寄信息待卖家确认"],[45, "卖家已经发货，等待买家确认收货"],[50, "退款执行中"],[60, "退款成功"],[70, "退款失败"]
        kuaishouOrderRefund.setRefundStatus(jsonObject.getInteger("status"));
        //todo 金额通知里面没有
        if (Objects.nonNull(jsonObject.getInteger("createTime"))) {
            kuaishouOrderRefund.setRefundAgreedTime(new Date(jsonObject.getInteger("createTime")));
        }

        //特殊退款类型[0, "非特殊退款"] [1, "价保"]
        kuaishouOrderRefund.setReasonCode(jsonObject.getInteger("specialRefundType"));

        //买家发起售后申请消息
        kuaishouOrderRefundService.saveOrUpdate(kuaishouOrderRefund);
    }

    /**
     * 创建快手订单
     *
     * @param notify          消息体
     * @param kuaishouAppInfo 通知对应应用信息
     * @throws KsMerchantApiException
     */
    public void createOrder(KuaishouCallbackNotify notify, KuaishouAppInfo kuaishouAppInfo) throws KsMerchantApiException {

        String accessToken = KuaiShouConfig.getAccessToken(Arrays.asList(notify.getAppKey(), notify.getOpenId()).stream().collect(Collectors.joining(":")));

        AccessTokenKsMerchantClient client = new AccessTokenKsMerchantClient(kuaishouAppInfo.getAppId(), kuaishouAppInfo.getSignSecret());

        OpenOrderDetailRequest request = new OpenOrderDetailRequest();

        request.setOid(Long.parseLong(notify.getKuaiShouOrderId()));

        request.setAccessToken(accessToken);

        OpenOrderDetailResponse response = client.execute(request);

        if (response.getResult() != 1) {
            log.warn("快手获取订单失败->{}", JSONObject.toJSONString(response));
            return;
        }

        log.info("快手订单详细信息->{}", JSONObject.toJSONString(response));

        //数据
        OrderDetail data = response.getData();

        OrderDetailBaseInfo orderBaseInfo = data.getOrderBaseInfo();

        Assert.isTrue(orderBaseInfo.getOid().equals(Long.parseLong(notify.getKuaiShouOrderId())), "快手订单号与消息通知订单号不一致");

        //创建订单
        kuaishouOrderService.save(packageKuaiShouOrder(data, notify.getKuaiShouSellerId()));

//            if (Objects.nonNull(kuaishouShopMapping)
//                    && WhetherEnum.NO.getValue().equals(kuaishouShopMapping.getManualCreation())
//                    && douYinOrder.getOrderStatus().longValue() != DouYinConfig.OrderStatus.CANCEL.getValue()
//                    && BigDecimalUtil.gtZero(douYinOrder.getOrderAmount())) {
//                /*
//                 *自动创建飞轮订单条件
//                 * 1、存在映射关系
//                 * 2、已支付状态
//                 * 3、支付金额>0
//                 * 4、不是手动创建
//                 */
//                String serialNo = douYinService.create(douYinOrder, lineList, shopMapping);
//                DouYinOrder up = new DouYinOrder();
//                up.setId(douYinOrder.getId());
//                up.setSerialNo(serialNo);
//                douYinOrderService.updateById(up);
//            }
    }

//    @PostMapping("/syncBillSale")
//    public SingleResponse syncBillSale(@RequestBody DouYinOrderSyncBillSaleRequest request) {
//        Assert.notNull(request.getDouYinOrderIdList(), "单号不能为空");
//
//        request.getDouYinOrderIdList().forEach(orderId -> {
//            try {
//                DouYinOrder douYinOrder = douYinOrderService.getByDouYinOrderId(orderId);
//                if (Objects.isNull(douYinOrder)) {
//                    throw new RuntimeException("订单不存在");
//                }
//                List<DouYinOrderLine> lineList = douYinOrderLineService.list(Wrappers.<DouYinOrderLine>lambdaQuery()
//                        .eq(DouYinOrderLine::getOrderId, douYinOrder.getId()));
//
//                DouYinShopMapping shopMapping = douYinShopMappingService.getByDouYinShopId(douYinOrder.getDouYinShopId(), lineList.stream()
//                        .map(DouYinOrderLine::getAuthorId)
//                        .filter(t -> Objects.nonNull(t) && t > 0)
//                        .findFirst()
//                        .orElse(0L)); // 0为默认达人id
//                /*
//                 *创建飞轮订单
//                 * 1、存在映射关系
//                 * 2、已支付状态
//                 * 3、支付金额>0
//                 */
//                if (Objects.nonNull(shopMapping)
//                        && douYinOrder.getOrderStatus().longValue() != DouYinConfig.OrderStatus.CANCEL.getValue()
//                        && BigDecimalUtil.gtZero(douYinOrder.getOrderAmount())) {
//                    try {
//                        String serialNo = douYinService.create(douYinOrder, lineList, shopMapping);
//                        DouYinOrder up = new DouYinOrder();
//                        up.setId(douYinOrder.getId());
//                        up.setSerialNo(serialNo);
//                        douYinOrderService.updateById(up);
//                    } catch (Exception e) {
//                        log.error("抖音创建飞轮订单异常{}", e.getMessage(), e);
//                    }
//                }
//            } catch (Exception e) {
//                log.error("抖音创建飞轮订单异常{}", e.getMessage(), e);
//            }
//        });
//        return SingleResponse.buildSuccess();
//    }

    /**
     * 取消订单 对于快手来说 是订单失败
     *
     * @param notify
     * @param kuaishouAppInfo
     */
    private void cancelOrder(KuaishouCallbackNotify notify, KuaishouAppInfo kuaishouAppInfo) {

        KuaishouOrder kuaishouOrder = kuaishouOrderService.getOne(Wrappers.<KuaishouOrder>lambdaQuery()
                .eq(KuaishouOrder::getOrderId, notify.getKuaiShouOrderId()));

        if (Objects.isNull(kuaishouOrder)) {
            throw new RuntimeException("快手订单不存在");
        }

        JSONObject jsonObject = JSONObject.parseObject(notify.getInfo());

        KuaishouOrder ko = new KuaishouOrder();
        ko.setId(kuaishouOrder.getId());
        //已审核 or 未审核
        ko.setWhetherUse(kuaishouOrder.getWhetherUse().equals(WhetherUseEnum.INIT.getValue()) ? WhetherUseEnum.CANCEL.getValue() : null);
        ko.setOrderStatus(jsonObject.getInteger("status"));

        ko.setCancelTime(new Date(jsonObject.getLong("updateTime")));

        //创建飞轮订单
//        try {
//            //取消飞轮订单
//            if (kuaishouOrder.getWhetherUse().equals(WhetherUseEnum.USE.getValue())) {
//                // 肯定有
//                kuaiShouService.cancelOrder(kuaishouOrder);
//            }
//        } catch (BusinessException e) {
//            log.warn("快手取消飞轮订单异常{}", e.getMessage(), e);
//        } catch (Exception e) {
//            log.error("快手取消飞轮订单异常{}", e.getMessage(), e);
//        }

        //取消订单
        kuaishouOrderService.updateById(ko);
    }


    /**
     * 修改地址
     *
     * @param data
     */
//    private void addressChanged(DouYinTradeAddressChangedData data) {
//        DouYinOrder order = douYinOrderService.getByDouYinOrderId(data.getPId().toString());
//
//        if (Objects.isNull(order)) {
//            throw new RuntimeException("订单不存在");
//        }
//
//        DouYinTradeAddressChangedData.ReceiverMsg receiverMsg = data.getReceiverMsg();
//
//        DouYinTradeAddressChangedData.Addr addr = JSONObject.parseObject(receiverMsg.getAddr().replace("\\\"", "\""), DouYinTradeAddressChangedData.Addr.class);
//
//        StringBuffer addrArea = new StringBuffer().append(Optional.ofNullable(addr.getProvince()) //省
//                        .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
//                        .orElse(StringUtils.EMPTY))
//                .append(Optional.ofNullable(addr.getCity())  //市
//                        .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
//                        .orElse(StringUtils.EMPTY))
//                .append(Optional.ofNullable(addr.getTown())  //区
//                        .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
//                        .orElse(StringUtils.EMPTY))
//                .append(Optional.ofNullable(addr.getStreet())  //街道
//                        .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
//                        .orElse(StringUtils.EMPTY));
//
//
//        //更新订单信息
//        DouYinOrder up = new DouYinOrder();
//        up.setId(order.getId());
//        //省
//        up.setProvince(Optional.ofNullable(addr.getProvince()) //省
//                .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
//                .orElse(StringUtils.EMPTY));
//        //市
//        up.setCity(Optional.ofNullable(addr.getCity())  //市
//                .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
//                .orElse(StringUtils.EMPTY));
//        //区
//        up.setTown(Optional.ofNullable(addr.getTown())  //区
//                .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
//                .orElse(StringUtils.EMPTY));
//        //街道
//        up.setStreet(Optional.ofNullable(addr.getStreet())  //街道
//                .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
//                .orElse(StringUtils.EMPTY));
//        //--------脱敏信息--------
//        up.setMaskPostTel(up.getDecryptPostTel());//脱敏收件人电话
//        up.setMaskPostReceiver(up.getDecryptPostReceiver());//脱敏收件人姓名
//        up.setMaskDetail(up.getDecryptAddrDetail());//脱敏收件地址
//        //--------密文信息--------
//        up.setEncryptPostTel(receiverMsg.getEncrypt_tel());// 密文收件人电话
//        up.setEncryptPostReceiver(receiverMsg.getEncrypt_name());//密文收件人姓名
//        up.setEncryptAddrArea(addrArea.toString());//密文收件地址省市区
//        up.setEncryptDetail(addr.getEncrypt_detail());//密文收件地址
//
//        //解密订单密文
//        Map<String, String> decryptMap = douYinDecryptService.orderDecrypt(
//                order.getShopId(),
//                order.getDouYinShopId(),
//                order.getOrderId(),
//                Arrays.asList(receiverMsg.getEncrypt_tel(), receiverMsg.getEncrypt_name(), addr.getEncrypt_detail()));
//        //--------解密信息--------
//        up.setDecryptPostTel(Optional.ofNullable(decryptMap.get(receiverMsg.getEncrypt_tel()))
//                .filter(StringUtils::isNotBlank)
//                .orElse(null));//解密收件人电话
//        up.setDecryptPostReceiver(Optional.ofNullable(decryptMap.get(receiverMsg.getEncrypt_name()))
//                .filter(StringUtils::isNotBlank)
//                .orElse(null));//解密收件人姓名
//        up.setDecryptAddrDetail(Optional.ofNullable(decryptMap.get(addr.getEncrypt_detail()))
//                .filter(StringUtils::isNotBlank)
//                .orElse(null));//解密收件地址
//
//        //更改收货地址
//        douYinOrderService.updateById(up);
//
//        if (Objects.nonNull(order.getSerialNo())) {
//            //更新客户信息
//            up.setOrderId(order.getOrderId());
//            douYinService.updateCustomerInfo(up);
//        }
//    }

    /**
     * 快手退货
     *
     * @param notify
     * @param kuaishouAppInfo
     */
    private void refundOrder(KuaishouCallbackNotify notify, KuaishouAppInfo kuaishouAppInfo) throws KsMerchantApiException {

        // 通知消息
        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(notify.getInfo()));

        String accessToken = KuaiShouConfig.getAccessToken(Arrays.asList(notify.getAppKey(),
                notify.getOpenId()).stream().collect(Collectors.joining(":")));

        AccessTokenKsMerchantClient client = new AccessTokenKsMerchantClient(kuaishouAppInfo.getAppId(), kuaishouAppInfo.getSignSecret());

        OpenSellerOrderRefundDetailRequest request = new OpenSellerOrderRefundDetailRequest();

        request.setRefundId(jsonObject.getLong("refundId"));

        request.setAccessToken(accessToken);

        OpenSellerOrderRefundDetailResponse response = client.execute(request);

        if (response.getResult() != 1) {
            log.warn("快手获取售后订单失败->{}", JSONObject.toJSONString(response));
            return;
        }

        log.info("快手售后订单详细信息->{}", JSONObject.toJSONString(response));

        MerchantRefundDetailDataView data = response.getData();

//        if (!(Arrays.asList(10, 11).contains(data.getStatus()) && data.getNegotiateStatus().equals(2))) {
//            return;
//        }




        //不是退货退款不考虑
        if (!(Arrays.asList(40).contains(data.getStatus()))) {
            return;
        }

        //查询快手订单有无审核
        KuaishouOrder kuaishouOrder = kuaishouOrderService.getOne(Wrappers.<KuaishouOrder>lambdaQuery().eq(KuaishouOrder::getOrderId, data.getOid()));

        if (Objects.isNull(kuaishouOrder)) {
            throw new RuntimeException("快手订单不存在");
        }

        if (!kuaishouOrder.getWhetherUse().equals(WhetherUseEnum.USE.getValue())) {
            return;
        }

        KuaishouOrderRefund refund = kuaishouOrderRefundService.getOne(Wrappers.<KuaishouOrderRefund>lambdaQuery()
                .eq(KuaishouOrderRefund::getRefundOrderId, String.valueOf(jsonObject.getLong("refundId"))));

        //未退货
        if (!Optional.ofNullable(refund).map(KuaishouOrderRefund::getReturnSerialNo).isPresent()) {
            KuaishouOrderRefund up = new KuaishouOrderRefund();
            up.setId(Optional.ofNullable(refund).map(KuaishouOrderRefund::getId).orElse(null));
            //取一下售后订单详情 填充数据
            up.setKuaiShouShopId(data.getSellerId());
            up.setRefundOrderId(String.valueOf(data.getRefundId()));
            up.setOrderId(String.valueOf(data.getOid()));
            up.setOrderSubId(String.valueOf(data.getOid()));
            up.setRefundStatus(data.getStatus());
            up.setRefundType(data.getHandlingWay());

            up.setRefundAmount(BigDecimalUtil.centToYuan(String.valueOf(data.getRefundFee())));
            up.setRefundAgreedTime(new Date(jsonObject.getLong("updateTime")));
//            协商状态，枚举：[0, "未知状态"] [1, "待商家处理"] [2, "商家同意"] [3, "商家驳回，等待买家修改"]
            up.setReasonCode(data.getSpecialRefundType());
            up.setKuaiShouShopName(String.valueOf(jsonObject.getInteger("eventType")));

            //创建飞轮逆向订单
            String serialNo = StringUtils.EMPTY;
            try {
                //退货飞轮订单
                serialNo = kuaiShouService.refundOrder(up, kuaishouOrder);
                up.setReturnSerialNo(serialNo);
            } catch (BusinessException e) {
                log.warn("快手退货飞轮退货单创建异常{}", e.getMessage(), e);
            } catch (Exception e) {
                log.error("快手退货飞轮退货单创建异常{}", e.getMessage(), e);
            }

            //退货订单
            kuaishouOrderRefundService.saveOrUpdate(up);

        }
    }


    /**
     * 发送机器人消息
     *
     * @param msg
     * @param orderId
     * @param serialNo
     */
//    private void sendMsg(String msg, String orderId, String serialNo) {
//        Optional.ofNullable(orderId)
//                .filter(StringUtils::isNotBlank)
//                .map(douYinShopMappingService::getByDouYinOrderId)
//                .filter(t -> StringUtils.isNotBlank(t.getRobot()))
//                .ifPresent(mapping -> wxCpMessageFacade.send(TextRobotMessage.builder()
//                        .key(mapping.getRobot())
//                        .text(TextRobotMessage.Text.builder()
//                                .content(StrFormatterUtil.format("【售后状态：{}】\n【直播组：{}】\n【抖音订单号：{}】\n【飞轮订单号：{}】",
//                                        msg, mapping.getShopName(), orderId, StringUtils.defaultString(serialNo, "无")))
//                                .mentioned_list(Stream.of(Lists.newArrayList(mapping.getOrderOwner().split(",")), Lists.newArrayList("@all"))
//                                        .flatMap(Collection::stream)
//                                        .filter(Objects::nonNull)
//                                        .collect(Collectors.toList()))
//                                .build())
//                        .build()));
//
//    }

    /**
     * 包装快手订单
     *
     * @param orderDetail
     * @param kuaiShouShopId
     * @return
     */
    private KuaishouOrder packageKuaiShouOrder(OrderDetail orderDetail, Long kuaiShouShopId) {

        OrderDetailBaseInfo orderBaseInfo = orderDetail.getOrderBaseInfo();

        KuaishouOrder kuaishouOrder = new KuaishouOrder();

        kuaishouOrder.setKuaiShouShopId(kuaiShouShopId);
        kuaishouOrder.setKuaiShouShopName(orderBaseInfo.getSellerNick());
        kuaishouOrder.setOrderId(String.valueOf(orderBaseInfo.getOid()));
        kuaishouOrder.setOrderStatus(orderBaseInfo.getStatus());
        kuaishouOrder.setOrderType(orderBaseInfo.getCpsType());
        kuaishouOrder.setActivityType(orderBaseInfo.getActivityType());
        kuaishouOrder.setPayType(orderBaseInfo.getPayType());
        kuaishouOrder.setPayChannel(orderBaseInfo.getPayChannel());
        kuaishouOrder.setCoType(orderBaseInfo.getCoType());
        kuaishouOrder.setCarrierType(orderBaseInfo.getCarrierType());
        kuaishouOrder.setCarrierId(orderBaseInfo.getCarrierId());
        kuaishouOrder.setOrderAmount(BigDecimalUtil.centToYuan(String.valueOf(orderBaseInfo.getTotalFee())));
        kuaishouOrder.setPayAmount(BigDecimalUtil.centToYuan(String.valueOf(orderBaseInfo.getTotalFee())));
        kuaishouOrder.setPayTime(new Date(orderBaseInfo.getPayTime()));
        kuaishouOrder.setBuyerWords(orderBaseInfo.getRemark());
        kuaishouOrder.setSellerOpenId(orderBaseInfo.getSellerOpenId());
        if (null != orderDetail.getOrderNote()){
            kuaishouOrder.setSellerWords(CollectionUtils.isEmpty(orderDetail.getOrderNote().getOrderNoteInfo()) ? "" : orderDetail.getOrderNote().getOrderNoteInfo().stream().map(i -> StringUtils.join(Arrays.asList(i.getNote(), i.getOperatorName()), "-")).collect(Collectors.joining(",")));
        }
        kuaishouOrder.setWhetherQuery(1);

        OrderAddressInfo orderAddressInfo = orderDetail.getOrderAddress();

        kuaishouOrder.setProvince(orderAddressInfo.getProvince());
        kuaishouOrder.setCity(orderAddressInfo.getCity());
        kuaishouOrder.setTown(orderAddressInfo.getDistrict());
        //快手新增变更
        kuaishouOrder.setStreet(orderAddressInfo.getTown());
        //省市区拼接 encrypt_addr_area
        String addrArea = StringUtils.join(
                Stream.of(orderAddressInfo.getProvince(), orderAddressInfo.getCity(), orderAddressInfo.getDistrict()
                        , orderAddressInfo.getTown() //todo 参数还没有给
                ).filter(Objects::nonNull).collect(Collectors.toList())

                , "");

        kuaishouOrder.setEncryptAddrArea(addrArea);

        //加密信息
        kuaishouOrder.setEncryptPostTel(orderAddressInfo.getEncryptedMobile());
        kuaishouOrder.setEncryptPostReceiver(orderAddressInfo.getEncryptedConsignee());
        kuaishouOrder.setEncryptDetail(orderAddressInfo.getEncryptedAddress());

        //脱敏
        kuaishouOrder.setMaskPostTel(orderAddressInfo.getDesensitiseMobile());
        kuaishouOrder.setMaskPostReceiver(orderAddressInfo.getDesensitiseConsignee());
        kuaishouOrder.setMaskDetail(orderAddressInfo.getDesensitiseAddress());

        kuaishouOrder.setWhetherUse(WhetherUseEnum.INIT.getValue());
        OrderSellerRoleInfo orderSellerRoleInfo = orderBaseInfo.getOrderSellerRoleInfo();

        kuaishouOrder.setRoleName(orderSellerRoleInfo.getRoleName());
        kuaishouOrder.setRoleId(orderSellerRoleInfo.getRoleId());
        kuaishouOrder.setRoleType(orderSellerRoleInfo.getRoleType());

        OrderItemInfo orderItemInfo = orderDetail.getOrderItemInfo();

        kuaishouOrder.setSkuId(orderItemInfo.getSkuId());
        kuaishouOrder.setRelSkuId(orderItemInfo.getRelSkuId());
        kuaishouOrder.setSkuDesc(orderItemInfo.getSkuDesc());
        kuaishouOrder.setSkuNick(orderItemInfo.getSkuNick());
        kuaishouOrder.setItemId(orderItemInfo.getItemId());
        kuaishouOrder.setRelItemId(orderItemInfo.getRelItemId());
        kuaishouOrder.setItemLinkUrl(orderItemInfo.getItemLinkUrl());
        kuaishouOrder.setItemTitle(orderItemInfo.getItemTitle());
        kuaishouOrder.setItemPicUrl(orderItemInfo.getItemPicUrl());
        kuaishouOrder.setNum(orderItemInfo.getNum());
        kuaishouOrder.setOriginalPrice(BigDecimalUtil.centToYuan(String.valueOf(orderItemInfo.getOriginalPrice())));
        kuaishouOrder.setDiscountFee(BigDecimalUtil.centToYuan(String.valueOf(orderItemInfo.getDiscountFee())));
        kuaishouOrder.setPrice(BigDecimalUtil.centToYuan(String.valueOf(orderItemInfo.getPrice())));
        kuaishouOrder.setItemType(orderItemInfo.getItemType());

        //查商品
        String modelCode = Optional.ofNullable(orderItemInfo.getSkuNick()) //组合商品
                .filter(StringUtils::isNotBlank).orElse(null);
        kuaishouOrder.setModelCode(modelCode);
        kuaishouOrder.setGoodsModel(Objects.nonNull(modelCode) ? Optional.ofNullable(goodsWatchFacade.listByModeCode(Arrays.asList(modelCode)).get(modelCode)).orElse(null) : null);

        //查抖音门店与飞轮门店映射关系
        List<KuaishouShopMapping> kuaishouShopMappingList = kuaishouShopMappingService.list(Wrappers.<KuaishouShopMapping>lambdaQuery().eq(KuaishouShopMapping::getKuaiShouShopId, kuaiShouShopId)
//                            .eq(KuaishouShopMapping::getAuthorId, Optional.ofNullable(orderSellerRoleInfo)
//                                    .map(OrderSellerRoleInfo::getRoleId)
//                                    .filter(t -> Objects.nonNull(t) && t > 0)
//                                    .orElse(0L))
        );
        // 0为默认达人id

        Assert.isTrue(CollectionUtils.isNotEmpty(kuaishouShopMappingList), "无映射");

        KuaishouShopMapping kuaishouShopMapping = kuaishouShopMappingList.get(FlywheelConstant.INDEX);


        /**
         * //解密订单密文 不需要解密
         *
         Map<String, String> decryptMap = Optional.ofNullable(kuaishouShopMapping)
         .map(t -> kuaiShouDecryptService.orderDecrypt(
         t.getShopId(),
         t.getKuaiShouShopId(),
         orderBaseInfo.getOid(),
         Arrays.asList(orderAddressInfo.getEncryptedMobile()
         , orderAddressInfo.getEncryptedConsignee()
         , orderAddressInfo.getEncryptedAddress()
         ),
         notify.getAppKey()
         )
         ).orElse(Collections.EMPTY_MAP);

         //--------解密信息--------
         kuaishouOrder.setDecryptPostTel(Optional.ofNullable(decryptMap.get(orderAddressInfo.getDesensitiseMobile()))
         .filter(StringUtils::isNotBlank)
         .orElse(null));//解密收件人电话
         kuaishouOrder.setDecryptPostReceiver(Optional.ofNullable(decryptMap.get(orderAddressInfo.getEncryptedConsignee()))
         .filter(StringUtils::isNotBlank)
         .orElse(null));//解密收件人姓名
         kuaishouOrder.setDecryptAddrDetail(Optional.ofNullable(decryptMap.get(orderAddressInfo.getEncryptedAddress()))
         .filter(StringUtils::isNotBlank)
         .orElse(null));//解密收件地址
         */
        //设置飞轮门店id
        kuaishouOrder.setShopId(Optional.ofNullable(kuaishouShopMapping).map(KuaishouShopMapping::getShopId).orElse(null));

        return kuaishouOrder;
    }
}