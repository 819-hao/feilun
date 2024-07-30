package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.doudian.open.api.afterSale_Detail.AfterSaleDetailRequest;
import com.doudian.open.api.afterSale_Detail.data.AfterSaleDetailData;
import com.doudian.open.api.afterSale_Detail.param.AfterSaleDetailParam;
import com.doudian.open.api.btas_getInspectionOrder.BtasGetInspectionOrderRequest;
import com.doudian.open.api.btas_getInspectionOrder.param.BtasGetInspectionOrderParam;
import com.doudian.open.api.order_orderDetail.OrderOrderDetailRequest;
import com.doudian.open.api.order_orderDetail.OrderOrderDetailResponse;
import com.doudian.open.api.order_orderDetail.data.*;
import com.doudian.open.api.order_orderDetail.param.OrderOrderDetailParam;
import com.doudian.open.api.product_detail.ProductDetailRequest;
import com.doudian.open.api.product_detail.ProductDetailResponse;
import com.doudian.open.api.product_detail.data.ProductDetailData;
import com.doudian.open.api.product_detail.param.ProductDetailParam;
import com.doudian.open.core.AccessToken;
import com.doudian.open.core.DoudianOpResponse;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.TextRobotMessage;
import com.seeease.flywheel.sale.ISaleReturnOrderFacade;
import com.seeease.flywheel.sale.request.*;
import com.seeease.flywheel.sale.result.BillSaleReturnOrderResult;
import com.seeease.flywheel.web.common.context.DouYinConfig;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.executor.CancelCmdExe;
import com.seeease.flywheel.web.common.work.executor.QueryCmdExe;
import com.seeease.flywheel.web.common.work.executor.SubmitCmdExe;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.flow.WorkflowStateEnum;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.entity.*;
import com.seeease.flywheel.web.entity.douyin.*;
import com.seeease.flywheel.web.entity.enums.WhetherUseEnum;
import com.seeease.flywheel.web.entity.request.DouYinCustomerDecryptionRequest;
import com.seeease.flywheel.web.entity.request.DouYinOrderSyncBillSaleRequest;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.infrastructure.service.*;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.utils.BigDecimalUtil;
import com.seeease.springframework.utils.StrFormatterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 抖音订单同步
 *
 * @author Tiro
 * @date 2023/4/25¬
 */
@Slf4j
@RestController
@RequestMapping("/douyin/")
public class DouYinController {

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
    private DouYinRefundCreatedService refundCreatedService;
    @Resource
    private DouYinDecryptService douYinDecryptService;
    @DubboReference(check = false, version = "1.0.0")
    private ISaleReturnOrderFacade facade;
    @Resource
    private QueryCmdExe workDetailsCmdExe;
    @Resource
    private SubmitCmdExe workflowCmdExe;
    @Resource
    private CancelCmdExe cancelCmdExe;

    private static final Set<String> TAG = ImmutableSet.of(DouYinMessageBody.DouYinMessageBodyTag.REFUND_CREATED.getValue(),
            DouYinMessageBody.DouYinMessageBodyTag.REFUND_MODIFIED.getValue(), DouYinMessageBody.DouYinMessageBodyTag.REFUND_CLOSED.getValue());


    /**
     * 查询抖音店铺订单详情
     *
     * @param orderId
     * @return
     */
    @PostMapping("/queryDetail")
    public SingleResponse queryDetail(@RequestParam("orderId") String orderId, @RequestParam("shopId") Long shopId) {
        OrderOrderDetailRequest request = new OrderOrderDetailRequest();
        OrderOrderDetailParam param = request.getParam();
        param.setShopOrderId(orderId);
        return SingleResponse.of(request.execute(DouYinConfig.getAccessToken(shopId)));
    }

    /**
     * 商家_查询订单的质检信息
     *
     * @param orderId
     * @return
     */
    @PostMapping("/getInspectionOrder")
    public SingleResponse getInspectionOrder(@RequestParam("orderId") String orderId, @RequestParam("shopId") Long shopId) {
        BtasGetInspectionOrderRequest request = new BtasGetInspectionOrderRequest();
        BtasGetInspectionOrderParam param = request.getParam();
        param.setOrderId(orderId);
        return SingleResponse.of(request.execute(DouYinConfig.getAccessToken(shopId)));
    }

    /**
     * 拆单合并
     *
     * @param request
     * @return
     */
    @PostMapping("/orderConsolidation")
    public SingleResponse orderConsolidation(@RequestBody DouYinOrderConsolidationRequest request) {
        Assert.isTrue(CollectionUtils.isNotEmpty(request.getIds()), "id不能为空");
        return SingleResponse.of(douYinOrderService.orderConsolidation(request));
    }

    /**
     * 查询pc抖音列表
     *
     * @param request
     * @return
     */
    @PostMapping("/queryPage")
    public SingleResponse queryPage(@RequestBody DouYinOrderListRequest request) {

        return SingleResponse.of(douYinOrderService.queryPage(request));
    }

    /**
     * 抖音订单客户重新解密
     *
     * @param request
     * @return
     */
    @PostMapping("/customerDecryption")
    public SingleResponse customerDecryption(@RequestBody DouYinCustomerDecryptionRequest request) {
        Assert.notNull(request.getDouYinOrderId(), "单号不能为空");
        Assert.notNull(request.getCustomerId(), "客户id不能为空");
        Assert.notNull(request.getCustomerContactsId(), "客户联系id不能为空");
        //更新
        douYinService.customerDecryption(request);

        return SingleResponse.buildSuccess();
    }

    /**
     * 抖音消息同步
     *
     * @param bodyList
     * @return
     */
    @PostMapping("/callback/receive")
    public Map<String, Object> receive(@RequestBody List<DouYinMessageBody> bodyList) {
        List<DouYinCallbackNotify> notifyList = bodyList.stream()
                .map(t -> {
                    DouYinCallbackNotify notify = new DouYinCallbackNotify();
                    notify.setState(WorkflowStateEnum.INIT.getValue());
                    notify.setData(t.getData());
                    notify.setMsgId(t.getMsgId());
                    notify.setTag(t.getTag());
                    try {
                        JSONObject data = JSONObject.parseObject(t.getData());
                        notify.setDouYinShopId(data.getLong("shop_id"));
                        notify.setDouYinOrderId(data.getString("p_id"));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    return notify;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(notifyList)) {            //记录消息
            List<DouYinCallbackNotify> list = notifyList.stream()
                    .filter(t -> !TAG.contains(t.getTag()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(list)) {
                douYinCallbackNotifyService.insertBatchSomeColumn(list);
            }
        }

        //异步处理消息
        notifyList.forEach(this::handleMessage);

        //成功响应
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        return result;
    }

    /**
     * 处理消息
     *
     * @param notify
     */
    private void handleMessage(DouYinCallbackNotify notify) {
        DouYinCallbackNotify up = new DouYinCallbackNotify();
        up.setId(notify.getId());
        up.setState(WorkflowStateEnum.COMPLETE.getValue());
        try {

            switch (DouYinMessageBody.DouYinMessageBodyTag.fromCode(notify.getTag())) {


                case TRADE_PAID:
                    this.createOrder(JSONObject.parseObject(notify.getData(), DouYinTradePaidData.class));
                    break;

                case TRADE_CANCELED:
                    this.cancelOrder(JSONObject.parseObject(notify.getData(), DouYinTradeCanceledData.class));
                    break;

                case TRADE_ADDRESS_CHANGE:
                    this.addressChanged(JSONObject.parseObject(notify.getData(), DouYinTradeAddressChangedData.class));
                    break;

                case RETURN_APPLY_AGREED:
                case REFUND_AGREED:
                    this.refundOrder(JSONObject.parseObject(notify.getData(), DouYinRefundAgreedData.class));
                    break;

                // 售后申请 或者修改 走这里
                case REFUND_CREATED:
                    DouYinRefundCreatedData data = JSONObject.parseObject(notify.getData(), DouYinRefundCreatedData.class);
                    this.refundCreated(data);
                    //拦截
                    data.setSaleIntercept(true);
                    douYinService.saleIntercept(data);
                    break;
                case REFUND_MODIFIED:
                    this.refundCreated(JSONObject.parseObject(notify.getData(), DouYinRefundCreatedData.class));
                    break;
                case REFUND_CLOSED:
                    DouYinRefundCreatedData data2 = JSONObject.parseObject(notify.getData(), DouYinRefundCreatedData.class);
                    this.refundCreated(data2);
                    //拦截取消
                    data2.setSaleIntercept(false);
                    douYinService.saleIntercept(data2);

                    //取消原订单
                    try {
                        log.info("Start tiktok workflow return order ");
                        BillSaleReturnOrderResult saleReturnOrder = facade.singleByBizCode(data2.getAftersaleId().toString());
                        if (Objects.nonNull(saleReturnOrder)) {
                            LoginUser loginUser = new LoginUser();
                            loginUser.setUserid("TiktokAdmin");
                            UserContext.setUser(loginUser);
                            CancelCmd<SaleReturnOrderCancelRequest> cancelCmd = new CancelCmd<>();
                            SaleReturnOrderCancelRequest req = new SaleReturnOrderCancelRequest();
                            req.setSerialNo(saleReturnOrder.getSerialNo());
                            req.setId(saleReturnOrder.getId());
                            cancelCmd.setRequest(req);
                            cancelCmd.setUseCase(UseCase.CANCEL);
                            cancelCmd.setBizCode(BizCode.TO_C_SALE_RETURN);
                            cancelCmdExe.cancel(cancelCmd);
                            log.info("Over tiktok workflow return order ");
                        }
                        log.info(" tiktok workflow return order Not found");
                    } catch (Exception e) {
                        log.error("tiktok workflow return order error:{}", e.getMessage());
                    }

                    break;

                default:
                    log.warn("未知消息种类");
                    return;
            }

            //尝试获取抖音退货快递单号数据
            try {
                JSONObject data = JSONObject.parseObject(notify.getData(), JSONObject.class);
                Long shop_id = data.getLong("shop_id");
                String aftersale_id = data.getString("aftersale_id");
                if (shop_id != null && StringUtils.isNotEmpty(aftersale_id)) {
                    DoudianOpResponse<AfterSaleDetailData> response = new DoudianOpResponse<>();
                    AfterSaleDetailRequest request = new AfterSaleDetailRequest();
                    AfterSaleDetailParam param = request.getParam();
                    param.setAfterSaleId(aftersale_id);
                    param.setNeedOperationRecord(true);
                    response = request.execute(DouYinConfig.getAccessToken(shop_id));

                    //获取退款单号
                    String trackingNo = response.getData()
                            .getProcessInfo()
                            .getLogisticsInfo()
                            .get_return()
                            .getTrackingNo();
                    //退货快递单号存在
                    if (StringUtils.isNotEmpty(trackingNo)) {
                        log.info("抖音退货--抖音获取退货快递单号为:{},aftersaleId:{},tag:{}", trackingNo, aftersale_id, notify.getTag());

                        BillSaleReturnOrderResult saleReturnOrder = facade.singleByBizCode(aftersale_id);

                        log.info("抖音退货--查询销售退货单结果:{}", JSONObject.toJSONString(saleReturnOrder));

                        //退货单存在但是未填写快递单号
                        if (saleReturnOrder != null && StringUtils.isEmpty(saleReturnOrder.getExpressNumber())) {
                            //这里要默认一个用户
                            LoginUser loginUser = new LoginUser();
                            loginUser.setUserid("TiktokAdmin");
                            UserContext.setUser(loginUser);
                            //查询工作流
                            QueryCmd<SaleReturnOrderDetailsRequest> queryCmd = new QueryCmd<>();
                            queryCmd.setUseCase(UseCase.QUERY_DETAILS);
                            queryCmd.setBizCode(BizCode.TO_C_SALE_RETURN);
                            queryCmd.setQueryTask(true);
                            SaleReturnOrderDetailsRequest queryReq = new SaleReturnOrderDetailsRequest();
                            queryReq.setId(saleReturnOrder.getId());
                            queryReq.setSerialNo(saleReturnOrder.getSerialNo());
                            queryCmd.setRequest(queryReq);
                            log.info("抖音退货--工作流查询参数:{}", JSONObject.toJSONString(queryCmd));
                            QuerySingleResult queryRet = (QuerySingleResult) workDetailsCmdExe.query(queryCmd);
                            log.info("抖音退货--工作流查询结果:{}", JSONObject.toJSONString(queryRet));
                            //提交工作流
                            if (null != queryRet && queryRet.getTask() != null) {
                                UserTaskDto task = queryRet.getTask();
                                SubmitCmd<SaleReturnOrderExpressNumberUploadRequest> submitCmd = new SubmitCmd<>();
                                submitCmd.setBizCode(BizCode.TO_C_SALE_RETURN);
                                submitCmd.setUseCase(UseCase.UPLOAD_EXPRESS_NUMBER);
                                SaleReturnOrderExpressNumberUploadRequest subReq = new SaleReturnOrderExpressNumberUploadRequest();
                                subReq.setSaleReturnId(saleReturnOrder.getId());
                                subReq.setExpressNumber(trackingNo);
                                subReq.setTiktokSaleReturnShopId(saleReturnOrder.getShopId());
                                submitCmd.setRequest(subReq);
                                submitCmd.setTaskList(Collections.singletonList(task));
                                log.info("抖音退货--提交工作流快递单号参数:{}", JSONObject.toJSONString(submitCmd));
                                workflowCmdExe.submit(submitCmd);
                                log.info("抖音退货提交快递单号成功");
                            }
                        }

                    }
                }

            } catch (Exception e) {
                log.warn("抖音退款商品填写退款快递单号失败，可能当前回调用户为填写快递单号。失败原因：{}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("抖音消息处理异常：{}", e.getMessage(), e);
            up.setState(WorkflowStateEnum.ERROR.getValue());
            up.setErrorReason(e.getMessage());
        } finally {
            if (!TAG.contains(up.getTag())) {
                douYinCallbackNotifyService.updateById(up);
            }
        }
    }

    /**
     * 买家发起售后申请消息 插入dou_yin_refund_created数据
     *
     * @param data
     */
    private void refundCreated(DouYinRefundCreatedData data) {
        DouYinRefundCreated created = refundCreatedService.getOne(new LambdaQueryWrapper<DouYinRefundCreated>()
                .eq(DouYinRefundCreated::getAftersaleId, data.getAftersaleId()));

        DouYinRefundCreated up = new DouYinRefundCreated();
        up.setId(Optional.ofNullable(created).map(DouYinRefundCreated::getId).orElse(null));
        up.setDouYinShopId(data.getShopId());
        up.setAftersaleId(data.getAftersaleId().toString());
        up.setOrderId(data.getPId().toString());
        up.setOrderSubId(data.getSId().toString());
        up.setAftersaleStatus(data.getAftersaleStatus());
        up.setAftersaleType(data.getAftersaleType());
        up.setRefundAmount(BigDecimalUtil.centToYuan(data.getRefundAmount().toString()));
        if (Objects.nonNull(data.getApplyTime()))
            up.setApplyTime(new Date(data.getApplyTime() * 1000));
        if (Objects.nonNull(data.getModifyTime()))
            up.setModifyTime(new Date(data.getModifyTime() * 1000));
        up.setReasonCode(data.getReasonCode());

        //买家发起售后申请消息
        refundCreatedService.saveOrUpdate(up);


    }

    /**
     * 创建抖音订单
     *
     * @param data
     */
    public void createOrder(DouYinTradePaidData data) {
        //获取门店token
        AccessToken accessToken = DouYinConfig.getAccessToken(data.getShopId());

        //查订单详细信息
        OrderOrderDetailRequest request = new OrderOrderDetailRequest();
        OrderOrderDetailParam param = request.getParam();
        param.setShopOrderId(data.getPId().toString());
        OrderOrderDetailResponse response = request.execute(accessToken);
        log.info("抖音订单信息{}", JSONObject.toJSONString(response));

        ShopOrderDetail orderDetail = response.getData().getShopOrderDetail();
        Assert.isTrue(orderDetail.getOrderId().equals(data.getPId().toString()), "抖音订单号不一致");

        //加密收件人
        PostAddr postAddr = orderDetail.getPostAddr();
        //脱敏收件人
        MaskPostAddr maskPostAddr = orderDetail.getMaskPostAddr();

        DouYinOrder douYinOrder = new DouYinOrder();
        douYinOrder.setDouYinShopId(orderDetail.getShopId());//抖音门店id
        douYinOrder.setDouYinShopName(orderDetail.getShopName());//抖音门店名称
        douYinOrder.setOrderId(orderDetail.getOrderId());//抖音订单id
        douYinOrder.setOrderStatus(orderDetail.getOrderStatus());//订单状态
        douYinOrder.setOrderStatusDesc(orderDetail.getOrderStatusDesc());//订单状态描述
        douYinOrder.setOrderType(orderDetail.getOrderType());//订单类型
        douYinOrder.setOrderTypeDesc(orderDetail.getOrderTypeDesc());//订单类型描述
        douYinOrder.setOrderAmount(BigDecimalUtil.centToYuan(String.valueOf(orderDetail.getOrderAmount() + orderDetail.getModifyAmount() - orderDetail.getPromotionShopAmount())));//订单金额
        douYinOrder.setPayAmount(BigDecimalUtil.centToYuan(orderDetail.getPayAmount().toString()));//支付金额
        douYinOrder.setPayTime(new Date(orderDetail.getPayTime() * 1000));//支付时间
        douYinOrder.setPayType(orderDetail.getPayType());//支付类型
        douYinOrder.setChannelPaymentNo(orderDetail.getChannelPaymentNo());//支付渠道的流水号
        douYinOrder.setBuyerWords(orderDetail.getBuyerWords());//买家留言
        douYinOrder.setSellerWords(orderDetail.getSellerWords());//商家备注
        douYinOrder.setWhetherUse(WhetherUseEnum.INIT);
        //地址信息
        douYinOrder.setOpenAddressId(orderDetail.getOpenAddressId());//开放平台地址id
        //省
        douYinOrder.setProvince(Optional.ofNullable(maskPostAddr.getProvince())
                .map(Province::getName)
                .filter(StringUtils::isNotBlank)
                .orElse(Optional.ofNullable(postAddr.getProvince())
                        .map(Province::getName)
                        .orElse(StringUtils.EMPTY)));
        //市
        douYinOrder.setCity(Optional.ofNullable(maskPostAddr.getCity())
                .map(City::getName)
                .filter(StringUtils::isNotBlank)
                .orElse(Optional.ofNullable(postAddr.getCity())
                        .map(City::getName)
                        .orElse(StringUtils.EMPTY)));
        //区
        douYinOrder.setTown(Optional.ofNullable(maskPostAddr.getTown())
                .map(Town::getName)
                .filter(StringUtils::isNotBlank)
                .orElse(Optional.ofNullable(postAddr.getTown())
                        .map(Town::getName)
                        .orElse(StringUtils.EMPTY)));
        //街道
        douYinOrder.setStreet(Optional.ofNullable(maskPostAddr.getStreet())
                .map(Street::getName)
                .filter(StringUtils::isNotBlank)
                .orElse(Optional.ofNullable(postAddr.getStreet())
                        .map(Street::getName)
                        .orElse(StringUtils.EMPTY)));

        //--------密文信息--------
        StringBuffer addrArea = new StringBuffer()
                .append(Optional.ofNullable(postAddr.getProvince())   //省
                        .map(Province::getName)
                        .orElse(StringUtils.EMPTY))
                .append(Optional.ofNullable(postAddr.getCity())  //市
                        .map(City::getName)
                        .orElse(StringUtils.EMPTY))
                .append(Optional.ofNullable(postAddr.getTown())  //区
                        .map(Town::getName)
                        .orElse(StringUtils.EMPTY))
                .append(Optional.ofNullable(postAddr.getStreet())//街道
                        .map(Street::getName)
                        .orElse(StringUtils.EMPTY));

        douYinOrder.setEncryptPostTel(orderDetail.getEncryptPostTel());// 密文收件人电话
        douYinOrder.setEncryptPostReceiver(orderDetail.getEncryptPostReceiver());//密文收件人姓名
        douYinOrder.setEncryptAddrArea(addrArea.toString());//密文收件地址省市区
        douYinOrder.setEncryptDetail(postAddr.getEncryptDetail());//密文收件地址


        //--------脱敏信息--------
        StringBuffer maskAddrDetail = new StringBuffer()
                .append(Optional.ofNullable(maskPostAddr.getProvince()) //省
                        .map(Province::getName)
                        .orElse(StringUtils.EMPTY))  //市
                .append(Optional.ofNullable(maskPostAddr.getCity())
                        .map(City::getName)
                        .orElse(StringUtils.EMPTY))
                .append(Optional.ofNullable(maskPostAddr.getTown())      //区
                        .map(Town::getName)
                        .orElse(StringUtils.EMPTY))
                .append(Optional.ofNullable(maskPostAddr.getStreet())//街道
                        .map(Street::getName)
                        .orElse(StringUtils.EMPTY))
                .append(maskPostAddr.getDetail());//地址

        douYinOrder.setMaskPostTel(orderDetail.getMaskPostTel());//脱敏收件人电话
        douYinOrder.setMaskPostReceiver(orderDetail.getMaskPostReceiver());//脱敏收件人姓名
        douYinOrder.setMaskDetail(maskAddrDetail.toString());//脱敏收件地址

        //保存订单行
        List<DouYinOrderLine> lineList = orderDetail.getSkuOrderList()
                .stream()
                .map(t -> {
                    DouYinOrderLine line = new DouYinOrderLine();
                    line.setOrderId(douYinOrder.getId()); //关联订单
                    line.setDouYinSubOrderId(t.getOrderId());//抖音子订单号
                    line.setLineState(t.getOrderStatus());//行状态
                    line.setItemNum(t.getItemNum());//商品数量
                    line.setProductId(t.getProductIdStr());//产品id
                    line.setProductName(t.getProductName());//产品名称
                    line.setModelCode(t.getCode());//飞轮型号编码
                    try {
                        //查商品
                        List<String> modelList = Optional.ofNullable(t.getBundleSkuInfo()) //组合商品
                                .filter(CollectionUtils::isNotEmpty)
                                .map(sku -> sku.stream().map(BundleSkuInfoItem::getProductId).collect(Collectors.toList()))
                                .orElse(Arrays.asList(t.getProductIdStr()))
                                .stream()
                                .map(productId -> {
                                    ProductDetailRequest productDetailRequest = new ProductDetailRequest();
                                    ProductDetailParam productDetailParam = productDetailRequest.getParam();
                                    productDetailParam.setProductId(productId);
                                    ProductDetailResponse productDetailResponse = productDetailRequest.execute(accessToken);

                                    return Optional.ofNullable(productDetailResponse.getData())
                                            .map(ProductDetailData::getProductFormatNew)
                                            .map(DouYinProductFormatNew::getGoodsModel)
                                            .orElse(null);//型号
                                })
                                .collect(Collectors.toList());
                        line.setGoodsModel(modelList);//型号
                    } catch (Exception e) {
                        log.error("抖音订单行解析型号异常{}", e.getMessage(), e);
                    }

                    line.setOrderAmount(BigDecimalUtil.centToYuan(String.valueOf(t.getOrderAmount() + t.getModifyAmount() - t.getPromotionShopAmount())));//商品金额
                    line.setPayAmount(BigDecimalUtil.centToYuan(t.getPayAmount().toString()));//支付金额
                    line.setAuthorId(t.getAuthorId());//直播主播id（达人）
                    line.setRoomId(t.getRoomId());//直播间id，有值则代表订单来自直播间
                    line.setVideoId(t.getVideoId());//视频id，有值则代表订单来自短视频

                    return line;
                })
                .collect(Collectors.toList());


        //查抖音门店与飞轮门店映射关系
        DouYinShopMapping shopMapping = douYinShopMappingService.getByDouYinShopId(douYinOrder.getDouYinShopId(), lineList.stream()
                .map(DouYinOrderLine::getAuthorId)
                .filter(t -> Objects.nonNull(t) && t > 0)
                .findFirst()
                .orElse(0L)); // 0为默认达人id

        //设置飞轮门店id
        douYinOrder.setShopId(Optional.ofNullable(shopMapping).map(DouYinShopMapping::getShopId).orElse(null));

        //解密订单密文
        Map<String, String> decryptMap = Optional.ofNullable(shopMapping)
                .map(t -> douYinDecryptService.orderDecrypt(
                        t.getShopId(),
                        t.getDouYinShopId(),
                        orderDetail.getOrderId(),
                        Arrays.asList(orderDetail.getEncryptPostTel(), orderDetail.getEncryptPostReceiver(), orderDetail.getPostAddr().getEncryptDetail()))
                ).orElse(Collections.EMPTY_MAP);

        //--------解密信息--------
        douYinOrder.setDecryptPostTel(Optional.ofNullable(decryptMap.get(orderDetail.getEncryptPostTel()))
                .filter(StringUtils::isNotBlank)
                .orElse(null));//解密收件人电话
        douYinOrder.setDecryptPostReceiver(Optional.ofNullable(decryptMap.get(orderDetail.getEncryptPostReceiver()))
                .filter(StringUtils::isNotBlank)
                .orElse(null));//解密收件人姓名
        douYinOrder.setDecryptAddrDetail(Optional.ofNullable(decryptMap.get(postAddr.getEncryptDetail()))
                .filter(StringUtils::isNotBlank)
                .orElse(null));//解密收件地址

        //创建订单
        douYinOrderService.create(douYinOrder, lineList);

        if (Objects.nonNull(shopMapping)
                && WhetherEnum.NO.getValue().equals(shopMapping.getManualCreation())
                && douYinOrder.getOrderStatus().longValue() != DouYinConfig.OrderStatus.CANCEL.getValue()
                && BigDecimalUtil.gtZero(douYinOrder.getOrderAmount())) {
            try {
                /*
                 *自动创建飞轮订单条件
                 * 1、存在映射关系
                 * 2、已支付状态
                 * 3、支付金额>0
                 * 4、不是手动创建
                 */
                String serialNo = douYinService.create(douYinOrder, lineList, shopMapping);
                DouYinOrder up = new DouYinOrder();
                up.setId(douYinOrder.getId());
                up.setSerialNo(serialNo);
                douYinOrderService.updateById(up);
            } catch (Exception e) {
                log.error("抖音创建飞轮订单异常{}", e.getMessage(), e);
            }
        }

    }

    @PostMapping("/syncBillSale")
    public SingleResponse syncBillSale(@RequestBody DouYinOrderSyncBillSaleRequest request) {
        Assert.notNull(request.getDouYinOrderIdList(), "单号不能为空");

        request.getDouYinOrderIdList().forEach(orderId -> {
            try {
                DouYinOrder douYinOrder = douYinOrderService.getByDouYinOrderId(orderId);
                if (Objects.isNull(douYinOrder)) {
                    throw new RuntimeException("订单不存在");
                }
                List<DouYinOrderLine> lineList = douYinOrderLineService.list(Wrappers.<DouYinOrderLine>lambdaQuery()
                        .eq(DouYinOrderLine::getOrderId, douYinOrder.getId()));

                DouYinShopMapping shopMapping = douYinShopMappingService.getByDouYinShopId(douYinOrder.getDouYinShopId(), lineList.stream()
                        .map(DouYinOrderLine::getAuthorId)
                        .filter(t -> Objects.nonNull(t) && t > 0)
                        .findFirst()
                        .orElse(0L)); // 0为默认达人id
                /*
                 *创建飞轮订单
                 * 1、存在映射关系
                 * 2、已支付状态
                 * 3、支付金额>0
                 */
                if (Objects.nonNull(shopMapping)
                        && douYinOrder.getOrderStatus().longValue() != DouYinConfig.OrderStatus.CANCEL.getValue()
                        && BigDecimalUtil.gtZero(douYinOrder.getOrderAmount())) {
                    try {
                        String serialNo = douYinService.create(douYinOrder, lineList, shopMapping);
                        DouYinOrder up = new DouYinOrder();
                        up.setId(douYinOrder.getId());
                        up.setSerialNo(serialNo);
                        douYinOrderService.updateById(up);
                    } catch (Exception e) {
                        log.error("抖音创建飞轮订单异常{}", e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                log.error("抖音创建飞轮订单异常{}", e.getMessage(), e);
            }
        });
        return SingleResponse.buildSuccess();
    }


    /**
     * 取消订单
     *
     * @param data
     */
    private void cancelOrder(DouYinTradeCanceledData data) {
        DouYinOrder order = douYinOrderService.getByDouYinOrderId(data.getPId().toString());

        if (Objects.isNull(order)) {
            throw new RuntimeException("订单不存在");
        }

        DouYinOrder up = new DouYinOrder();
        up.setId(order.getId());
        up.setWhetherUse(order.getWhetherUse().equals(WhetherUseEnum.INIT) ? WhetherUseEnum.CANCEL : null);
        up.setOrderStatus(data.getOrderStatus());
        up.setOrderStatusDesc("取消订单");
        up.setCancelTime(new Date(data.getCancelTime() * 1000));
        up.setCancelReason(data.getCancelReason());

        //创建飞轮订单
        try {
            //取消飞轮订单
            douYinService.cancelOrder(order);
        } catch (BusinessException e) {
            log.warn("抖音取消飞轮订单异常{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("抖音取消飞轮订单异常{}", e.getMessage(), e);
        }

        //取消订单
        douYinOrderService.updateById(up);

        //发送通知
        this.sendMsg("用户取消", order.getOrderId(), order.getSerialNo());
    }


    /**
     * 修改地址
     *
     * @param data
     */
    private void addressChanged(DouYinTradeAddressChangedData data) {
        DouYinOrder order = douYinOrderService.getByDouYinOrderId(data.getPId().toString());

        if (Objects.isNull(order)) {
            throw new RuntimeException("订单不存在");
        }

        DouYinTradeAddressChangedData.ReceiverMsg receiverMsg = data.getReceiverMsg();

        DouYinTradeAddressChangedData.Addr addr = JSONObject.parseObject(receiverMsg.getAddr().replace("\\\"", "\""), DouYinTradeAddressChangedData.Addr.class);

        StringBuffer addrArea = new StringBuffer().append(Optional.ofNullable(addr.getProvince()) //省
                        .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
                        .orElse(StringUtils.EMPTY))
                .append(Optional.ofNullable(addr.getCity())  //市
                        .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
                        .orElse(StringUtils.EMPTY))
                .append(Optional.ofNullable(addr.getTown())  //区
                        .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
                        .orElse(StringUtils.EMPTY))
                .append(Optional.ofNullable(addr.getStreet())  //街道
                        .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
                        .orElse(StringUtils.EMPTY));


        //更新订单信息
        DouYinOrder up = new DouYinOrder();
        up.setId(order.getId());
        //省
        up.setProvince(Optional.ofNullable(addr.getProvince()) //省
                .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
                .orElse(StringUtils.EMPTY));
        //市
        up.setCity(Optional.ofNullable(addr.getCity())  //市
                .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
                .orElse(StringUtils.EMPTY));
        //区
        up.setTown(Optional.ofNullable(addr.getTown())  //区
                .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
                .orElse(StringUtils.EMPTY));
        //街道
        up.setStreet(Optional.ofNullable(addr.getStreet())  //街道
                .map(DouYinTradeAddressChangedData.AdministrativeArea::getName)
                .orElse(StringUtils.EMPTY));
        //--------脱敏信息--------
        up.setMaskPostTel(up.getDecryptPostTel());//脱敏收件人电话
        up.setMaskPostReceiver(up.getDecryptPostReceiver());//脱敏收件人姓名
        up.setMaskDetail(up.getDecryptAddrDetail());//脱敏收件地址
        //--------密文信息--------
        up.setEncryptPostTel(receiverMsg.getEncrypt_tel());// 密文收件人电话
        up.setEncryptPostReceiver(receiverMsg.getEncrypt_name());//密文收件人姓名
        up.setEncryptAddrArea(addrArea.toString());//密文收件地址省市区
        up.setEncryptDetail(addr.getEncrypt_detail());//密文收件地址

        //解密订单密文
        Map<String, String> decryptMap = douYinDecryptService.orderDecrypt(
                order.getShopId(),
                order.getDouYinShopId(),
                order.getOrderId(),
                Arrays.asList(receiverMsg.getEncrypt_tel(), receiverMsg.getEncrypt_name(), addr.getEncrypt_detail()));
        //--------解密信息--------
        up.setDecryptPostTel(Optional.ofNullable(decryptMap.get(receiverMsg.getEncrypt_tel()))
                .filter(StringUtils::isNotBlank)
                .orElse(null));//解密收件人电话
        up.setDecryptPostReceiver(Optional.ofNullable(decryptMap.get(receiverMsg.getEncrypt_name()))
                .filter(StringUtils::isNotBlank)
                .orElse(null));//解密收件人姓名
        up.setDecryptAddrDetail(Optional.ofNullable(decryptMap.get(addr.getEncrypt_detail()))
                .filter(StringUtils::isNotBlank)
                .orElse(null));//解密收件地址

        //更改收货地址
        douYinOrderService.updateById(up);

        if (Objects.nonNull(order.getSerialNo())) {
            //更新客户信息
            up.setOrderId(order.getOrderId());
            up.setSerialNo(order.getSerialNo());
            douYinService.updateCustomerInfo(up);
        }
    }


    /**
     * 抖音退货
     *
     * @param data
     */
    private void refundOrder(DouYinRefundAgreedData data) {
        DouYinOrderRefund refund = douYinOrderRefundService.getOne(Wrappers.<DouYinOrderRefund>lambdaQuery()
                .eq(DouYinOrderRefund::getRefundOrderId, data.getAftersaleId()));

        //未退货
        if (!Optional.ofNullable(refund).map(DouYinOrderRefund::getReturnSerialNo).isPresent()) {
            DouYinOrderRefund up = new DouYinOrderRefund();
            up.setId(Optional.ofNullable(refund).map(DouYinOrderRefund::getId).orElse(null));
            up.setDouYinShopId(data.getShopId());
            up.setRefundOrderId(data.getAftersaleId().toString());
            up.setOrderId(data.getPId().toString());
            up.setOrderSubId(data.getSId().toString());
            up.setRefundStatus(data.getAftersaleStatus());
            up.setRefundType(data.getAftersaleType());
            up.setRefundAmount(BigDecimalUtil.centToYuan(data.getRefundAmount().toString()));
            up.setRefundAgreedTime(new Date(data.getAgree_time() * 1000));
            up.setReasonCode(data.getReasonCode());

            //创建飞轮逆向订单
            String serialNo = "";
            try {
                DouYinOrder order = douYinOrderService.getByDouYinOrderId(up.getOrderId());
                //退货飞轮订单
                serialNo = douYinService.refundOrder(up, order);
                up.setReturnSerialNo(serialNo);
            } catch (BusinessException e) {
                log.warn("抖音退货飞轮退货单创建异常{}", e.getMessage(), e);
            } catch (Exception e) {
                log.error("抖音退货飞轮退货单创建异常{}", e.getMessage(), e);
            }

            //退货订单
            douYinOrderRefundService.saveOrUpdate(up);

            //发送通知
            this.sendMsg("用户退货", up.getOrderId(), serialNo);

        }


    }


    /**
     * 发送机器人消息
     *
     * @param msg
     * @param orderId
     * @param serialNo
     */
    private void sendMsg(String msg, String orderId, String serialNo) {
        Optional.ofNullable(orderId)
                .filter(StringUtils::isNotBlank)
                .map(douYinShopMappingService::getByDouYinOrderId)
                .filter(t -> StringUtils.isNotBlank(t.getRobot()))
                .ifPresent(mapping -> wxCpMessageFacade.send(TextRobotMessage.builder()
                        .key(mapping.getRobot())
                        .text(TextRobotMessage.Text.builder()
                                .content(StrFormatterUtil.format("【售后状态：{}】\n【直播组：{}】\n【抖音订单号：{}】\n【飞轮订单号：{}】",
                                        msg, mapping.getShopName(), orderId, StringUtils.defaultString(serialNo, "无")))
                                .mentioned_list(Stream.of(Lists.newArrayList(mapping.getOrderOwner().split(",")), Lists.newArrayList("@all"))
                                        .flatMap(Collection::stream)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList()))
                                .build())
                        .build()));

    }


}
