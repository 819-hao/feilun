package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.doudian.open.api.logistics_waybillApply.LogisticsWaybillApplyRequest;
import com.doudian.open.api.logistics_waybillApply.LogisticsWaybillApplyResponse;
import com.doudian.open.api.logistics_waybillApply.param.LogisticsWaybillApplyParam;
import com.doudian.open.api.logistics_waybillApply.param.WaybillAppliesItem;
import com.doudian.open.core.AccessToken;
import com.doudian.open.core.GlobalConfig;
import com.doudian.open.utils.SignUtil;
import com.google.common.collect.Lists;
import com.kuaishou.merchant.open.api.KsMerchantApiException;
import com.kuaishou.merchant.open.api.client.AccessTokenKsMerchantClient;
import com.kuaishou.merchant.open.api.domain.express.*;
import com.kuaishou.merchant.open.api.request.express.OpenExpressEbillGetRequest;
import com.kuaishou.merchant.open.api.response.express.OpenExpressEbillGetResponse;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.express.request.ExpressBatchPrintRequest;
import com.seeease.flywheel.express.request.ExpressCancelRequest;
import com.seeease.flywheel.express.result.DdExpressPrintResult;
import com.seeease.flywheel.express.result.ExpressBatchPrintResult;
import com.seeease.flywheel.express.result.ExpressPrintResult;
import com.seeease.flywheel.express.result.KsExpressPrintResult;
import com.seeease.flywheel.storework.IWmsWorkCollectFacade;
import com.seeease.flywheel.storework.request.WmsWorkUploadExpressRequest;
import com.seeease.flywheel.storework.result.WmsWorkExpressResult;
import com.seeease.flywheel.web.common.context.DouYinConfig;
import com.seeease.flywheel.web.common.context.KuaiShouConfig;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.express.SFExpressConfig;
import com.seeease.flywheel.web.common.express.channel.*;
import com.seeease.flywheel.web.common.util.StrUtil;
import com.seeease.flywheel.web.entity.*;
import com.seeease.flywheel.web.infrastructure.service.*;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.Tuple2;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.exception.e.OperationRejectedExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/9/20
 */
@Slf4j
@RestController
@RequestMapping("/saleExpress")
public class SaleExpressController {
    @NacosValue(value = "${express.sf.pullAddress}", autoRefreshed = true)
    private String pullAddress;
    @NacosValue(value = "${express.sf.pullProvince}", autoRefreshed = true)
    private String pullProvince;
    @NacosValue(value = "${express.sf.pullCity}", autoRefreshed = true)
    private String pullCity;
    @NacosValue(value = "${express.sf.pullCounty}", autoRefreshed = true)
    private String pullCounty;
    @NacosValue(value = "${express.sf.pullStreet}", autoRefreshed = true)
    private String pullStreet;
    @NacosValue(value = "${express.sf.pullContact}", autoRefreshed = true)
    private String pullContact;
    @NacosValue(value = "${express.sf.pullMobile}", autoRefreshed = true)
    private String pullMobile;

    @NacosValue(value = "${saleOrder.oneOrder:}", autoRefreshed = true)
    private List<Integer> IP_SHOP_ID;


    @DubboReference(check = false, version = "1.0.0")
    private IWmsWorkCollectFacade wmsWorkCollectFacade;
    @Resource
    private DouYinOrderService douYinOrderService;
    @Resource
    private DouYinOrderLineService douYinOrderLineService;
    @Resource
    private DouyinPrintMappingService douyinPrintMappingService;
    @Resource
    private ExpressContext expressContext;

    @Resource
    private KuaishouOrderService kuaishouOrderService;

    @Resource
    private KuaishouPrintMappingService kuaishouPrintMappingService;

    @Resource
    private KuaishouAppInfoService kuaishouAppInfoService;


    @PostMapping("/saveExpressNo")
    public SingleResponse saveExpressNo(@RequestBody WmsWorkUploadExpressRequest request) {
        //查集单数据
        WmsWorkExpressResult wmsWorkExpressResult = wmsWorkCollectFacade.express(Objects.requireNonNull(request.getOriginSerialNo()));
        if (Objects.isNull(wmsWorkExpressResult)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.SALE_WORK_NOT_OPERATE);
        }
        if (wmsWorkExpressResult.getWorkIntercept().equals(WhetherEnum.YES.getValue())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.SALE_WORK_INTERCEPT);
        }
        //手动录入非系统打印
        request.setIsSystemPrint(false);
        //打单上传快递单号
        return SingleResponse.of(wmsWorkCollectFacade.uploadExpress(request));
    }

    @Getter
    @AllArgsConstructor
    enum SaleOrderChannelEnum {
        KUAI_SHOU(18, "快手"), DOU_YIN(3, "抖音"),
        ;
        private Integer value;
        private String desc;
    }


    @PostMapping("/printOrder")
    public SingleResponse printOrder(@RequestBody ExpressBatchPrintRequest requestBatch) {
        if (CollectionUtils.isEmpty(requestBatch.getSerialNoList())) {
            return SingleResponse.of(ImportResult.<DdExpressPrintResult>builder().successList(Arrays.asList()).errList(Arrays.asList()).build());
        }
        //抖店打印数据
        List<DdExpressPrintResult> douYinList = new ArrayList<>();
        //快手打印数据
        List<KsExpressPrintResult> kuaiShouList = new ArrayList<>();
        //顺丰打印数据
        List<ExpressPrintResult> sfList = new ArrayList<>();
        //失败数据
        List<String> fail = new ArrayList<>();
        for (String serialNo : requestBatch.getSerialNoList()) {
            try {
                //查集单数据
                WmsWorkExpressResult wmsWorkExpressResult = wmsWorkCollectFacade.express(serialNo);

                if (Objects.isNull(wmsWorkExpressResult)) {
                    throw new OperationRejectedException(OperationExceptionCodeEnum.SALE_WORK_NOT_OPERATE);
                }

                if (wmsWorkExpressResult.getWorkIntercept().equals(WhetherEnum.YES.getValue())) {
                    throw new OperationRejectedException(OperationExceptionCodeEnum.SALE_WORK_INTERCEPT);
                }

                //*********   公共数据源 *********

                //快递 发货人
                ExpressPlaceOrder.ContactsInfo senderInfo = null;

                //订单信息 快递
                ExpressPlaceOrder.OrderInfo orderInfo = null;

                //*********   公共数据源 *********

                //********* 一、抖音数据 ***********
                //查抖音订单数据
                List<DouYinOrder> douYinOrderList = null;

                DouYinOrder douYinOrder = null;

                DouyinPrintMapping douyinPrintMapping = null;

                //********* 抖音数据 ***********

                //********* 二、快手数据 ***********

                List<KuaishouOrder> kuaishouOrderList = null;

                KuaishouOrder kuaishouOrder = null;

                KuaishouPrintMapping kuaishouPrintMapping = null;

                //********* 快手数据 ***********

                switch (wmsWorkExpressResult.getSaleOrderChannel()) {
                    case 18:
                        kuaishouOrderList = new ArrayList<>(kuaishouOrderService.list(Wrappers.<KuaishouOrder>lambdaQuery().eq(KuaishouOrder::getSerialNo, serialNo)));

                        kuaishouOrder = kuaishouOrderList.stream().findFirst().orElse(null);

                        //查发件人信息
                        kuaishouPrintMapping = kuaishouPrintMappingService.list(Wrappers.<KuaishouPrintMapping>lambdaQuery().eq(KuaishouPrintMapping::getShopId, wmsWorkExpressResult.getSaleStoreId()).eq(Objects.nonNull(kuaishouOrder), KuaishouPrintMapping::getKuaiShouShopId, Optional.ofNullable(kuaishouOrder).map(KuaishouOrder::getKuaiShouShopId).orElse(null))).stream().findFirst().orElse(null);

                        if (ObjectUtils.isEmpty(kuaishouPrintMapping)) {
                            fail.add("未查询对应的门店发货地址:=" + wmsWorkExpressResult.getSaleStoreId());
                            continue;
                        }
                        //发件人信息
                        senderInfo = ExpressPlaceOrder.ContactsInfo.builder().company("稀蜴真品").province(kuaishouPrintMapping.getProvinceName()).city(kuaishouPrintMapping.getCityName()).town(kuaishouPrintMapping.getDistrictName()).street(kuaishouPrintMapping.getStreetName()).addressDetail(kuaishouPrintMapping.getDetailAddress()).contactName(kuaishouPrintMapping.getName()).contactTel(kuaishouPrintMapping.getMobile()).templateUrl(kuaishouPrintMapping.getTemplateUrl()).customTemplateUrl(kuaishouPrintMapping.getCustomTemplateUrl()).build();

                        orderInfo = ExpressPlaceOrder.OrderInfo.builder().saleShopId(wmsWorkExpressResult.getSaleStoreId()).saleRemarks(wmsWorkExpressResult.getSaleRemarks()).orderNo(serialNo).kuaiShouShopId(Optional.ofNullable(kuaishouOrder).map(KuaishouOrder::getKuaiShouShopId).orElse(null)).kuaiShouOrderId(Optional.ofNullable(kuaishouOrder).map(KuaishouOrder::getOrderId).orElse(null)).goodsInfoList(wmsWorkExpressResult.getGoodsInfos().stream().map(t -> ExpressPlaceOrder.GoodsInfo.builder().info(StringUtils.join(Arrays.asList(t.getBrandName(), t.getSeriesName(), t.getModel()), "/")).brandName(t.getBrandName()).seriesName(t.getSeriesName()).model(t.getModel()).stockSn(t.getStockSn()).wno(t.getWno()).btsCode(t.getSpotCheckCode()).build()).collect(Collectors.toList())).build();
                        break;
                    default:
                        //查抖音订单数据
                        douYinOrderList = new ArrayList<>(douYinOrderService.list(Wrappers.<DouYinOrder>lambdaQuery().eq(DouYinOrder::getSerialNo, serialNo)));
                        douYinOrder = douYinOrderList.stream().findFirst().orElse(null);

                        //查发件人信息
                        douyinPrintMapping = douyinPrintMappingService.list(Wrappers.<DouyinPrintMapping>lambdaQuery().eq(DouyinPrintMapping::getShopId, wmsWorkExpressResult.getSaleStoreId()).eq(Objects.nonNull(douYinOrder), DouyinPrintMapping::getDouYinShopId, Optional.ofNullable(douYinOrder).map(DouYinOrder::getDouYinShopId).orElse(null))).stream().findFirst().orElse(null);

                        if (ObjectUtils.isEmpty(douyinPrintMapping)) {
                            fail.add("未查询对应的门店发货地址:=" + wmsWorkExpressResult.getSaleStoreId());
                            continue;
                        }

                        //发件人信息
                        senderInfo = ExpressPlaceOrder.ContactsInfo.builder().company("稀蜴真品").province(douyinPrintMapping.getProvinceName()).city(douyinPrintMapping.getCityName()).town(douyinPrintMapping.getDistrictName()).street(douyinPrintMapping.getStreetName()).addressDetail(douyinPrintMapping.getDetailAddress()).contactName(douyinPrintMapping.getName()).contactTel(douyinPrintMapping.getMobile()).templateUrl(douyinPrintMapping.getTemplateUrl()).customTemplateUrl(douyinPrintMapping.getCustomTemplateUrl()).build();

                        orderInfo = ExpressPlaceOrder.OrderInfo.builder().saleShopId(wmsWorkExpressResult.getSaleStoreId()).saleRemarks(wmsWorkExpressResult.getSaleRemarks()).orderNo(serialNo).douYinShopId(Optional.ofNullable(douYinOrder).map(DouYinOrder::getDouYinShopId).orElse(null)).douYinOrderId(Optional.ofNullable(douYinOrder).map(DouYinOrder::getOrderId).orElse(null)).goodsInfoList(wmsWorkExpressResult.getGoodsInfos().stream().map(t -> ExpressPlaceOrder.GoodsInfo.builder().info(StringUtils.join(Arrays.asList(t.getBrandName(), t.getSeriesName(), t.getModel()), "/")).brandName(t.getBrandName()).seriesName(t.getSeriesName()).model(t.getModel()).stockSn(t.getStockSn()).wno(t.getWno()).btsCode(t.getSpotCheckCode()).build()).collect(Collectors.toList())).build();
                        break;
//                    default:
//                        log.warn("销售渠道不匹配,{}", wmsWorkExpressResult.getSaleOrderChannel());
                }

                List<ExpressPlaceOrder> expressPlaceOrderList = new ArrayList<>();
                switch (wmsWorkExpressResult.getInspectionType()) {
                    case 0://不质检
                        Tuple2<ExpressChannelTypeEnum, ExpressPlaceOrder.ContactsInfo> info = null;
                        switch (wmsWorkExpressResult.getSaleOrderChannel()) {
                            case 18:
                                //用户收货信息
                                info = this.userContactsInfo(kuaishouOrder, wmsWorkExpressResult);

                                assert kuaishouOrder != null;
                                expressPlaceOrderList.add(ExpressPlaceOrder.builder().requestID(this.getUUID()).businessNo(serialNo).channelType(info.getV1()).sfProductCode(SFProductCodeEnum.T6).orderInfo(orderInfo).senderInfo(senderInfo).receiverInfo(info.getV2()).itemTitle(kuaishouOrder.getItemTitle()).kuaiShouShopName(kuaishouOrder.getKuaiShouShopName()).sellerOpenId(kuaishouOrder.getSellerOpenId()).build());
                                break;
//                            case 3:
                            default:
                                //用户收货信息
                                info = this.userContactsInfo(douYinOrder, wmsWorkExpressResult);

                                expressPlaceOrderList.add(ExpressPlaceOrder.builder().requestID(this.getUUID()).businessNo(serialNo).channelType(info.getV1()).sfProductCode(SFProductCodeEnum.T6).orderInfo(orderInfo).senderInfo(senderInfo).receiverInfo(info.getV2()).build());
                                break;
                        }
                        break;
                    case 1://线下质检
                        //用户收货信息
                        Tuple2<ExpressChannelTypeEnum, ExpressPlaceOrder.ContactsInfo> userInfo = this.userContactsInfo(douYinOrder, wmsWorkExpressResult);

                        //国检地址
                        ExpressPlaceOrder.ContactsInfo transitPoint = ExpressPlaceOrder.ContactsInfo.builder().addressDetail(pullAddress).province(pullProvince).city(pullCity).town(pullCounty).street(pullStreet).contactName(pullContact).contactTel(pullMobile).templateUrl(douyinPrintMapping.getTemplateUrl()).customTemplateUrl(douyinPrintMapping.getCustomTemplateUrl()).build();

                        //稀蜴 - 国检
                        expressPlaceOrderList.add(ExpressPlaceOrder.builder().requestID(this.getUUID()).businessNo(serialNo).channelType(ExpressChannelTypeEnum.SF).sfProductCode(SFProductCodeEnum.T6).orderInfo(orderInfo).senderInfo(senderInfo).receiverInfo(transitPoint).build());

                        if (IP_SHOP_ID.contains(wmsWorkExpressResult.getSaleStoreId())) {
                            break;
                        }

                        //国检 - 用户
                        expressPlaceOrderList.add(ExpressPlaceOrder.builder().requestID(this.getUUID()).businessNo(this.gjToUserSerialNo(serialNo)).channelType(userInfo.getV1()).sfProductCode(SFProductCodeEnum.T6).orderInfo(orderInfo).senderInfo(transitPoint).receiverInfo(userInfo.getV2()).build());
                        break;
                    case 2://线上质检
                        List<DouYinOrderLine> douYinOrderLineList = douYinOrderLineService.list(Wrappers.<DouYinOrderLine>lambdaQuery().in(DouYinOrderLine::getOrderId, douYinOrderList.stream().map(DouYinOrder::getId).collect(Collectors.toList())));
                        DouYinOrderLine douYinOrderLine = douYinOrderLineList.stream().filter(t -> StringUtils.isNotBlank(t.getScAddress())).findFirst().get();

                        //线上质检合单的时候矫正抖音订单号
                        orderInfo.setDouYinOrderId(douYinOrderList.stream().filter(t -> t.getId().equals(douYinOrderLine.getOrderId())).findFirst().get().getOrderId());

                        //线上质检
                        expressPlaceOrderList.add(ExpressPlaceOrder.builder().requestID(this.getUUID()).businessNo(serialNo).channelType(ExpressChannelTypeEnum.DY_SF).sfProductCode(SFProductCodeEnum.T6).orderInfo(orderInfo).businessNo(serialNo).senderInfo(senderInfo).receiverInfo(ExpressPlaceOrder.ContactsInfo.builder().province(douYinOrderLine.getScProvince()).city(douYinOrderLine.getScCity()).town(douYinOrderLine.getScDistrict()).street(douYinOrderLine.getScStreet()).addressDetail(douYinOrderLine.getScAddress()).contactName(douYinOrderLine.getScName()).contactTel(douYinOrderLine.getScPhone()).build()).build());
                        break;
                }

                List<ExpressPlaceOrderResult> res = expressContext.create(expressPlaceOrderList);

                //上传快递单号
                this.uploadExpressOrder(res, serialNo);

                Map<String, ExpressPlaceOrderResult> resultMap = res.stream().collect(Collectors.toMap(ExpressPlaceOrderResult::getBusinessNo, Function.identity()));

                //打印数据
                expressPlaceOrderList.forEach(t -> {
                    ExpressPlaceOrderResult r = resultMap.get(t.getBusinessNo());
                    if (r.isSuccess()) {
                        try {
                            switch (t.getChannelType()) {
                                case SF:
                                    sfList.add(this.createSFExpressPrintResult(r, t));
                                    break;
                                case DY_SF:
                                    douYinList.add(this.createDdExpressPrintResult(r, t));
                                    break;
                                case KS_SF:
                                    kuaiShouList.add(this.createKsExpressPrintResult(r, t));
                                    break;
                            }
                        } catch (Exception e) {
                            log.error("打印异常{}", e.getMessage(), e);
                            fail.add(e.getMessage() + t.getOrderInfo().getOrderNo());
                        }
                    } else {
                        fail.add(r.getErrMsg() + t.getOrderInfo().getOrderNo());
                    }
                });
            } catch (Exception e) {
                log.error("打印异常{}", e.getMessage(), e);
                fail.add(e.getMessage() + serialNo);
            }
        }

        /**
         * 业务处理 end
         */
        return SingleResponse.of(ExpressBatchPrintResult.builder().sfList(sfList).douYinList(douYinList).kuaiShouList(kuaiShouList).errList(fail).build());
    }

    /**
     * 获取终端用户收货联系信息
     *
     * @param douYinOrder
     * @param wmsWorkExpressResult
     * @return
     */
    private Tuple2<ExpressChannelTypeEnum, ExpressPlaceOrder.ContactsInfo> userContactsInfo(DouYinOrder douYinOrder, WmsWorkExpressResult wmsWorkExpressResult) {
        //抖音订单
        if (Objects.nonNull(douYinOrder)) {
            return Tuple2.of(ExpressChannelTypeEnum.DY_SF, ExpressPlaceOrder.ContactsInfo.builder().province(douYinOrder.getProvince()).city(douYinOrder.getCity()).town(douYinOrder.getTown()).street(douYinOrder.getStreet()).addressDetail(douYinOrder.getEncryptDetail()).contactName(douYinOrder.getEncryptPostReceiver()).contactTel(douYinOrder.getEncryptPostTel()).build());
        }
        //走顺丰下单
        if (StringUtils.isEmpty(wmsWorkExpressResult.getProvince()) || StringUtils.isEmpty(wmsWorkExpressResult.getCity()) || StringUtils.isEmpty(wmsWorkExpressResult.getTown())) {
            //解析地址
            Map<String, String> addressResolutionMap = this.addressResolution(wmsWorkExpressResult.getContactAddress());
            return Tuple2.of(ExpressChannelTypeEnum.SF, ExpressPlaceOrder.ContactsInfo.builder().province(addressResolutionMap.get("province")).city(addressResolutionMap.get("city")).town(addressResolutionMap.get("county")).addressDetail(wmsWorkExpressResult.getContactAddress()).contactName(wmsWorkExpressResult.getContactName()).contactTel(wmsWorkExpressResult.getContactPhone()).build());
        } else {
            return Tuple2.of(ExpressChannelTypeEnum.SF, ExpressPlaceOrder.ContactsInfo.builder().province(wmsWorkExpressResult.getProvince()).city(wmsWorkExpressResult.getCity()).town(wmsWorkExpressResult.getTown()).street(wmsWorkExpressResult.getStreet()).addressDetail(wmsWorkExpressResult.getContactAddress()).contactName(wmsWorkExpressResult.getContactName()).contactTel(wmsWorkExpressResult.getContactPhone()).build());
        }
    }

    private Tuple2<ExpressChannelTypeEnum, ExpressPlaceOrder.ContactsInfo> userContactsInfo(KuaishouOrder kuaishouOrder, WmsWorkExpressResult wmsWorkExpressResult) {
        //抖音订单
        if (Objects.nonNull(kuaishouOrder)) {
            return Tuple2.of(ExpressChannelTypeEnum.KS_SF, ExpressPlaceOrder.ContactsInfo.builder().province(kuaishouOrder.getProvince()).city(kuaishouOrder.getCity()).town(kuaishouOrder.getTown()).street(kuaishouOrder.getStreet()).addressDetail(kuaishouOrder.getEncryptDetail()).contactName(kuaishouOrder.getEncryptPostReceiver()).contactTel(kuaishouOrder.getEncryptPostTel()).build());
        }
        //走顺丰下单
        if (StringUtils.isEmpty(wmsWorkExpressResult.getProvince()) || StringUtils.isEmpty(wmsWorkExpressResult.getCity()) || StringUtils.isEmpty(wmsWorkExpressResult.getTown())) {
            //解析地址
            Map<String, String> addressResolutionMap = this.addressResolution(wmsWorkExpressResult.getContactAddress());
            return Tuple2.of(ExpressChannelTypeEnum.SF, ExpressPlaceOrder.ContactsInfo.builder().province(addressResolutionMap.get("province")).city(addressResolutionMap.get("city")).town(addressResolutionMap.get("county")).addressDetail(wmsWorkExpressResult.getContactAddress()).contactName(wmsWorkExpressResult.getContactName()).contactTel(wmsWorkExpressResult.getContactPhone()).build());
        } else {
            return Tuple2.of(ExpressChannelTypeEnum.SF, ExpressPlaceOrder.ContactsInfo.builder().province(wmsWorkExpressResult.getProvince()).city(wmsWorkExpressResult.getCity()).town(wmsWorkExpressResult.getTown()).street(wmsWorkExpressResult.getStreet()).addressDetail(wmsWorkExpressResult.getContactAddress()).contactName(wmsWorkExpressResult.getContactName()).contactTel(wmsWorkExpressResult.getContactPhone()).build());
        }
    }


    /**
     * 地址转换
     *
     * @param address
     * @return
     */
    private Map<String, String> addressResolution(String address) {
        //1.地址的正则表达式
        String regex = "(?<province>[^省]+省|.+自治区|[^澳门]+澳门|[^香港]+香港|[^市]+市)?(?<city>[^自治州]+自治州|[^特别行政区]+特别行政区|[^市]+市|.*?地区|.*?行政单位|.+盟|市辖区|[^县]+县)(?<county>[^县]+县|[^市]+市|[^镇]+镇|[^区]+区|[^乡]+乡|.+场|.+旗|.+海域|.+岛)?(?<address>.*)";
        //2、创建匹配规则
        Matcher m = Pattern.compile(regex).matcher(address);
        String province;
        String city;
        String county;
        String detailAddress;
        Map<String, String> map = new HashMap<>(16);

        while (m.find()) {
            //加入省
            province = m.group("province");
            map.put("province", province == null ? "" : province.trim());
            //加入市
            city = m.group("city");
            map.put("city", city == null ? "" : city.trim());
            //加入区
            county = m.group("county");
            map.put("county", county == null ? "" : county.trim());
            //详细地址
            detailAddress = m.group("address");
            map.put("address", detailAddress == null ? "" : detailAddress.trim());
        }
        return map;
    }


    /**
     * 上传快递单号
     *
     * @param res
     * @param serialNo
     */
    private void uploadExpressOrder(List<ExpressPlaceOrderResult> res, String serialNo) {
        if (res.stream().allMatch(t -> !t.isSuccess())) {
            return;
        }
        Map<String, ExpressPlaceOrderResult> resultMap = res.stream().collect(Collectors.toMap(ExpressPlaceOrderResult::getBusinessNo, Function.identity()));

        wmsWorkCollectFacade.uploadExpress(WmsWorkUploadExpressRequest.builder().originSerialNo(serialNo).isSystemPrint(true).deliveryExpressNumber(resultMap.get(serialNo).getExpressNumber()).gjToUserExpressNumber(Optional.ofNullable(resultMap.get(this.gjToUserSerialNo(serialNo))).map(ExpressPlaceOrderResult::getExpressNumber).orElse(null)).build());

    }

    /**
     * 打两个单场景，国检到用户到订单号生成
     *
     * @param serialNo
     * @return
     */
    private String gjToUserSerialNo(String serialNo) {
        return serialNo + "-GJ";
    }

    @Resource
    private SFExpressConfig sfExpressConfig;


    private ExpressPrintResult createSFExpressPrintResult(ExpressPlaceOrderResult orderResult, ExpressPlaceOrder placeOrder) {
        return ExpressPrintResult.builder().expressNoList(Lists.newArrayList(orderResult.getExpressNumber())).productName(placeOrder.getOrderInfo().getGoodsInfoList().stream().map(result -> StringUtils.join(Arrays.asList(result.getBrandName(), result.getSeriesName(), result.getModel()), "/")).collect(Collectors.joining("\n"))).remarks(placeOrder.getOrderInfo().getSaleRemarks()).accessToken(sfExpressConfig.getAccessToken().getAccessToken()).requestID(this.getUUID()).templateCode(sfExpressConfig.getTemplateCode()).build();
    }

    /**
     * 获取抖音打印信息
     *
     * @param orderResult
     * @param placeOrder
     * @return
     */
    private DdExpressPrintResult createDdExpressPrintResult(ExpressPlaceOrderResult orderResult, ExpressPlaceOrder placeOrder) {

        AccessToken accessToken = DouYinConfig.getAccessToken(placeOrder.getOrderInfo().getDouYinShopId());

        //返回打印信息
        LogisticsWaybillApplyRequest logisticsWaybillApplyRequest = new LogisticsWaybillApplyRequest();
        LogisticsWaybillApplyParam logisticsWaybillApplyRequestParam = logisticsWaybillApplyRequest.getParam();
        WaybillAppliesItem waybillAppliesItem = new WaybillAppliesItem();
        waybillAppliesItem.setLogisticsCode("shunfeng");
        waybillAppliesItem.setTrackNo(orderResult.getExpressNumber());
        logisticsWaybillApplyRequestParam.setWaybillApplies(Arrays.asList(waybillAppliesItem));

        log.info("获取面单信息请求:{}", logisticsWaybillApplyRequest.toString());
        LogisticsWaybillApplyResponse logisticsWaybillApplyResponse = logisticsWaybillApplyRequest.execute(accessToken);
        log.info("获取面单信息响应:{}", logisticsWaybillApplyResponse.toString());

        String printData = logisticsWaybillApplyResponse.getData().getWaybillInfos().get(FlywheelConstant.INDEX).getPrintData();
        String sign = logisticsWaybillApplyResponse.getData().getWaybillInfos().get(FlywheelConstant.INDEX).getSign();

        Map<String, String> data = new HashMap<>();

        String printProductName = StringUtils.join(placeOrder.getOrderInfo().getGoodsInfoList().stream().map(stockExt -> StringUtils.join(Arrays.asList(stockExt.getWno()), "\t")).collect(Collectors.toList()), "\n");

        String model = StringUtils.join(placeOrder.getOrderInfo().getGoodsInfoList().stream().map(ExpressPlaceOrder.GoodsInfo::getModel).distinct().collect(Collectors.toList()), "\t");

        String btsCode = StringUtils.join(placeOrder.getOrderInfo().getGoodsInfoList().stream().map(ExpressPlaceOrder.GoodsInfo::getBtsCode).filter(StringUtils::isNotBlank).collect(Collectors.toList()), ",");

        data.put("productName", StringUtils.join(Arrays.asList(Optional.ofNullable(placeOrder).map(ExpressPlaceOrder::getOrderInfo).map(ExpressPlaceOrder.OrderInfo::getOrderNo).orElse(StringUtils.EMPTY), "销售备注：" + StringUtils.defaultIfBlank(placeOrder.getOrderInfo().getSaleRemarks(), "无"), printProductName), "\n"));
        data.put("remarks", placeOrder.getOrderInfo().getSaleRemarks());
        data.put("model", model);
        if (StringUtils.isNotBlank(btsCode)) {
            data.put("spotCheckCode", "国检码：" + btsCode);
        }

        String params = StringUtils.join(Arrays.asList("access_token=" + accessToken.getAccessToken(), "app_key=" + GlobalConfig.getGlobalConfig().getAppKey(), "method=logistics.getShopKey", "param_json={}", "timestamp=" + String.valueOf(System.currentTimeMillis()), "sign=" + SignUtil.sign(GlobalConfig.getGlobalConfig().getAppKey(), GlobalConfig.getGlobalConfig().getAppSecret(), "logistics.getShopKey", String.valueOf(System.currentTimeMillis()), "{}", "2"), "sign_method=md5", "v=2"), "&");
        log.info("打印参数params:{}", params);

        return DdExpressPrintResult.builder().cmd("print").requestID(this.getUUID()).version("1.0").task(DdExpressPrintResult.TaskDTO.builder().taskID(this.getUUID()).preview(Boolean.FALSE).printer("").documents(Arrays.asList(DdExpressPrintResult.DocumentsDTO.builder().docNo(orderResult.getExpressNumber()).documentID(this.getUUID()).contents(Arrays.asList(DdExpressPrintResult.ContentsDTO.builder().templateURL(placeOrder.getSenderInfo().getTemplateUrl()).params(params).signature(sign).encryptedData(printData).addData(new HashMap<String, Object>() {{
            put("senderInfo", DdExpressPrintResult.SenderInfo.builder().address(DdExpressPrintResult.Address.builder().countryCode("CHN").provinceName(placeOrder.getSenderInfo().getProvince()).cityName(placeOrder.getSenderInfo().getCity()).districtName(placeOrder.getSenderInfo().getTown()).streetName(placeOrder.getSenderInfo().getStreet()).detailAddress(placeOrder.getSenderInfo().getAddressDetail()).build()).contact(DdExpressPrintResult.Contact.builder().mobile(StrUtil.replace(placeOrder.getSenderInfo().getContactTel(), 3, placeOrder.getSenderInfo().getContactTel().length() - 4, '*')).name(placeOrder.getSenderInfo().getContactName()).build()).build());
        }}).build(), DdExpressPrintResult.DataDTO.builder().templateURL(placeOrder.getSenderInfo().getCustomTemplateUrl()).data(data).build())).build())).build()).build();
    }

    /**
     * 获取抖音打印信息
     *
     * @param orderResult
     * @param order
     * @return
     */
    private KsExpressPrintResult createKsExpressPrintResult(ExpressPlaceOrderResult orderResult, ExpressPlaceOrder order) {

        KuaishouAppInfo kuaishouAppInfo = kuaishouAppInfoService.list(Wrappers.<KuaishouAppInfo>lambdaQuery().eq(KuaishouAppInfo::getOpenShopId, order.getOrderInfo().getKuaiShouShopId())).stream().findFirst().orElse(null);

        Optional.ofNullable(kuaishouAppInfo).orElseThrow(() -> new OperationRejectedException((OperationRejectedExceptionCode) () -> "快手对应的配置项不存在"));

        AccessTokenKsMerchantClient client = new AccessTokenKsMerchantClient(kuaishouAppInfo.getAppId(), kuaishouAppInfo.getSignSecret());

        //请求下单接口
        OpenExpressEbillGetRequest request = new OpenExpressEbillGetRequest();
        request.setAccessToken(KuaiShouConfig.getAccessToken(Arrays.asList(kuaishouAppInfo.getAppId(), order.getSellerOpenId()).stream().collect(Collectors.joining(":"))));

        GetEbillOrderRequest getEbillOrderRequest = new GetEbillOrderRequest();
        getEbillOrderRequest.setMerchantCode(order.getSellerOpenId());
        getEbillOrderRequest.setMerchantName(order.getKuaiShouShopName());
        //包裹 == 订单号
        getEbillOrderRequest.setPackageCode(order.getOrderInfo().getKuaiShouOrderId());

        getEbillOrderRequest.setTotalPackageQuantity(1L);

        ItemDTO itemDTO = new ItemDTO();
        //
        itemDTO.setItemTitle(order.getItemTitle());
        itemDTO.setItemQuantity(1L);

        getEbillOrderRequest.setItemList(Arrays.asList(itemDTO));

        //收件人
        Contract receiverContract = new Contract();
        ExpressPlaceOrder.ContactsInfo receiverInfo = order.getReceiverInfo();
        receiverContract.setName(receiverInfo.getContactName());
        receiverContract.setMobile(receiverInfo.getContactTel());
        getEbillOrderRequest.setReceiverContract(receiverContract);

        //发件人
        Contract senderContract = new Contract();
        ExpressPlaceOrder.ContactsInfo senderInfo = order.getSenderInfo();
        senderContract.setName(senderInfo.getContactName());
        senderContract.setMobile(senderInfo.getContactTel());
        getEbillOrderRequest.setSenderContract(senderContract);

        //收货地址
        AddressDTO receiverAddress = new AddressDTO();
        receiverAddress.setProvinceName(receiverInfo.getProvince());
        receiverAddress.setCityName(receiverInfo.getCity());
        receiverAddress.setDistrictName(receiverInfo.getTown());

        if (StringUtils.isNotBlank(receiverInfo.getStreet())) {
            receiverAddress.setStreetName(receiverInfo.getStreet());
        }

        receiverAddress.setDetailAddress(receiverInfo.getAddressDetail());

        getEbillOrderRequest.setReceiverAddress(receiverAddress);

        //发货地址
        AddressDTO senderAddress = new AddressDTO();
        senderAddress.setProvinceName(senderInfo.getProvince());
        senderAddress.setCityName(senderInfo.getCity());
        senderAddress.setDistrictName(senderInfo.getTown());
        senderAddress.setStreetName(senderInfo.getStreet());
        senderAddress.setDetailAddress(senderInfo.getAddressDetail());
        getEbillOrderRequest.setSenderAddress(senderAddress);


        getEbillOrderRequest.setExpressCompanyCode("SF");
        getEbillOrderRequest.setOrderChannel("KUAI_SHOU");
        //https://docs.qingque.cn/d/home/eZQCstmJ4XYNo4WQdS1sPlw9E?identityId=1oEFwmDizx5#section=h.kb2i44jbl6if
        //	○ settleAccount（客户编码）：
        //		■ 必传，和商家订购服务 中的保持一致，本质为月结卡号；
        getEbillOrderRequest.setSettleAccount("5717175232");

        getEbillOrderRequest.setTradeOrderCode(order.getOrderInfo().getKuaiShouOrderId());

        getEbillOrderRequest.setExtData(JSONObject.toJSONString(new HashMap<String, String>() {
            {
                put("isvClientCode", "XYWLKPnPx_WDKJ");
            }
        }));

        getEbillOrderRequest.setPayMethod(1);
        getEbillOrderRequest.setExpressProductCode(order.getSfProductCode().getValue());

        getEbillOrderRequest.setRequestId(order.getRequestID());

        request.setGetEbillOrderRequest(Arrays.asList(getEbillOrderRequest));
        OpenExpressEbillGetResponse response = null;

        log.info("商家ERP/ISV 向快手电子面单系统获取单号和打印信息:{}", JSONObject.toJSONString(request.getGetEbillOrderRequest()));
        try {
            response = client.execute(request);
        } catch (KsMerchantApiException e) {
            log.error(e.getErrorMsg(), e);
        }

        Optional.ofNullable(response).orElseThrow(() -> new OperationRejectedException((OperationRejectedExceptionCode) () -> "快手下顺丰单失败"));
        log.info("商家ERP/ISV 向快手电子面单系统获取单号和打印信息:{}", JSONObject.toJSONString(response));
        log.info("商家ERP/ISV 向快手电子面单系统获取单号和打印信息:{}", JSONObject.toJSONString(response.getData()));

        if (response.getResult() != 1) {
            return null;
        }

        GetEbillOrderDTO getEbillOrderDTO = response.getData().get(FlywheelConstant.INDEX).getData().get(FlywheelConstant.INDEX);

        return KsExpressPrintResult.builder().cmd("print").requestID(this.getUUID()).version("1.0").task(KsExpressPrintResult.TaskDTO.builder().taskID(this.getUUID()).preview(Boolean.FALSE).printer("").firstDocumentNumber(1).totalDocumentCount(1).documents(Arrays.asList(KsExpressPrintResult.DocumentsDTO.builder().documentID(this.getUUID()).waybillCode(getEbillOrderDTO.getWaybillCode()).ksOrderFlag(Boolean.TRUE).contents(Arrays.asList(KsExpressPrintResult.ContentsDTO.builder().templateURL(order.getSenderInfo().getTemplateUrl()).key(getEbillOrderDTO.getKey()).ver(getEbillOrderDTO.getVersion()).signature(getEbillOrderDTO.getSignature()).encryptedData(getEbillOrderDTO.getPrintData()).addData(new HashMap<String, Object>() {{
                    put("senderInfo", KsExpressPrintResult.SenderInfo.builder().address(KsExpressPrintResult.Address.builder().countryCode("CHN").provinceName(order.getSenderInfo().getProvince()).cityName(order.getSenderInfo().getCity()).districtName(order.getSenderInfo().getTown()).streetName(order.getSenderInfo().getStreet()).detailAddress(order.getSenderInfo().getAddressDetail()).build()).contact(KsExpressPrintResult.Contact.builder().mobile(StrUtil.replace(order.getSenderInfo().getContactTel(), 3, order.getSenderInfo().getContactTel().length() - 4, '*')).name(order.getSenderInfo().getContactName()).build()).build());
                }}).build()
//                                        ,
//                                        KsExpressPrintResult.DataDTO.builder()
//                                                .templateURL(order.getSenderInfo().getCustomTemplateUrl())
//                                                .data(data)
//                                                .build()
        )).build())).build()).build();
    }


    /**
     * @return
     */
    private String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 回收单号
     *
     * @param request
     * @return
     */
    @PostMapping("/recoveryOrder")
    public SingleResponse recoveryOrder(@RequestBody ExpressCancelRequest request) {
        Assert.isTrue(StringUtils.isNoneBlank(request.getExpressNo()), "参数异常");

        ExpressRecoveryOrderResult res = expressContext.recoveryOrder(request.getExpressNo());

        if (res.isSuccess()) {
            return SingleResponse.buildSuccess();
        } else {
            return SingleResponse.buildFailure(-1, res.getErrMsg());
        }
    }
}
