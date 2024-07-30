package com.seeease.flywheel.web.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.doudian.open.api.logistics_cancelOrder.LogisticsCancelOrderRequest;
import com.doudian.open.api.logistics_cancelOrder.LogisticsCancelOrderResponse;
import com.doudian.open.api.logistics_cancelOrder.param.LogisticsCancelOrderParam;
import com.doudian.open.api.logistics_getCustomTemplateList.LogisticsGetCustomTemplateListRequest;
import com.doudian.open.api.logistics_getCustomTemplateList.LogisticsGetCustomTemplateListResponse;
import com.doudian.open.api.logistics_getCustomTemplateList.param.LogisticsGetCustomTemplateListParam;
import com.doudian.open.api.logistics_listShopNetsite.LogisticsListShopNetsiteRequest;
import com.doudian.open.api.logistics_listShopNetsite.LogisticsListShopNetsiteResponse;
import com.doudian.open.api.logistics_listShopNetsite.param.LogisticsListShopNetsiteParam;
import com.doudian.open.api.logistics_newCreateOrder.LogisticsNewCreateOrderRequest;
import com.doudian.open.api.logistics_templateList.LogisticsTemplateListRequest;
import com.doudian.open.api.logistics_templateList.LogisticsTemplateListResponse;
import com.doudian.open.api.logistics_waybillApply.LogisticsWaybillApplyRequest;
import com.doudian.open.api.logistics_waybillApply.LogisticsWaybillApplyResponse;
import com.doudian.open.api.logistics_waybillApply.param.LogisticsWaybillApplyParam;
import com.doudian.open.api.logistics_waybillApply.param.WaybillAppliesItem;
import com.doudian.open.core.AccessToken;
import com.doudian.open.core.GlobalConfig;
import com.doudian.open.utils.SignUtil;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.express.IExpressFacade;
import com.seeease.flywheel.express.request.ExpressBatchPrintRequest;
import com.seeease.flywheel.express.request.ExpressCancelRequest;
import com.seeease.flywheel.express.request.ExpressPrintRequest;
import com.seeease.flywheel.express.request.infrastructure.SfExpressAccessTokenRequest;
import com.seeease.flywheel.express.request.infrastructure.SfExpressCreateOrderRequest;
import com.seeease.flywheel.express.result.DdExpressPrintResult;
import com.seeease.flywheel.express.result.ExpressPrintResult;
import com.seeease.flywheel.express.result.infrastructure.SfExpressCreateOrderResult;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.result.PrintOptionResult;
import com.seeease.flywheel.sf.IExpressOrderFacade;
import com.seeease.flywheel.sf.IExpressOrderPrintFacade;
import com.seeease.flywheel.sf.request.ExpressOrderCreateRequest;
import com.seeease.flywheel.sf.request.ExpressOrderEditRequest;
import com.seeease.flywheel.sf.request.ExpressOrderPrintCreateRequest;
import com.seeease.flywheel.sf.request.ExpressOrderQueryRequest;
import com.seeease.flywheel.sf.result.ExpressOrderCreateResult;
import com.seeease.flywheel.sf.result.ExpressOrderQueryResult;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.IStoreWorkQueryFacade;
import com.seeease.flywheel.storework.IWmsWorkInterceptFacade;
import com.seeease.flywheel.storework.request.StoreWorkDeliveryDetailRequest;
import com.seeease.flywheel.storework.request.StoreWorkEditRequest;
import com.seeease.flywheel.storework.result.StoreWorkDetailResult;
import com.seeease.flywheel.web.common.context.DouYinConfig;
import com.seeease.flywheel.web.common.util.StrUtil;
import com.seeease.flywheel.web.controller.express.request.ExpressCreateRequest;
import com.seeease.flywheel.web.controller.express.result.ExpressCreateResult;
import com.seeease.flywheel.web.controller.express.strategy.DdSfExpressContext;
import com.seeease.flywheel.web.entity.DouYinRefundCreated;
import com.seeease.flywheel.web.entity.DouYinScInfo;
import com.seeease.flywheel.web.entity.DouYinShopMapping;
import com.seeease.flywheel.web.entity.DouyinPrintMapping;
import com.seeease.flywheel.web.infrastructure.service.DouYinRefundCreatedService;
import com.seeease.flywheel.web.infrastructure.service.DouYinScInfoService;
import com.seeease.flywheel.web.infrastructure.service.DouYinShopMappingService;
import com.seeease.flywheel.web.infrastructure.service.DouyinPrintMappingService;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 快递方面信息
 * @Date create in 2023/6/25 13:43
 */
@Slf4j
@RestController
@RequestMapping("/express")
public class ExpressController {

    /**
     * 地址的正则表达式
     */
    private final String REGEX = "(?<province>[^省]+省|.+自治区|[^澳门]+澳门|[^香港]+香港|[^市]+市)?(?<city>[^自治州]+自治州|[^特别行政区]+特别行政区|[^市]+市|.*?地区|.*?行政单位|.+盟|市辖区|[^县]+县)(?<county>[^县]+县|[^市]+市|[^镇]+镇|[^区]+区|[^乡]+乡|.+场|.+旗|.+海域|.+岛)?(?<address>.*)";

    @DubboReference(check = false, version = "1.0.0")
    private IExpressFacade expressFacade;

    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade saleOrderFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkQueryFacade storeWorkQueryFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkFacade storeWorkFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IExpressOrderFacade expressOrderFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IExpressOrderPrintFacade expressOrderPrintFacade;

    @DubboReference(check = false)
    private IWmsWorkInterceptFacade wmsWorkInterceptFacade;

    @Resource
    private DouYinScInfoService douYinScInfoService;

    @Resource
    private DouYinShopMappingService douYinShopMappingService;

    @Resource
    private DouyinPrintMappingService douyinPrintMappingService;

    @Resource
    private DouYinRefundCreatedService refundCreatedService;

    @NacosValue(value = "${express.sf.monthlyCard:7551234567}", autoRefreshed = true)
    private String monthlyCard;

    @NacosValue(value = "${express.sf.templateCode:fm_150_standard_XYWLKX3CG59P}", autoRefreshed = true)
    private String templateCode;

    //************************** start 寄件信息 **************************

    /**
     *
     */
    @NacosValue(value = "${express.sf.pushAddress:7551234567}", autoRefreshed = true)
    private String pushAddress;
    @NacosValue(value = "${express.sf.pushProvince:7551234567}", autoRefreshed = true)
    private String pushProvince;
    @NacosValue(value = "${express.sf.pushCity:7551234567}", autoRefreshed = true)
    private String pushCity;
    @NacosValue(value = "${express.sf.pushCounty:7551234567}", autoRefreshed = true)
    private String pushCounty;
    @NacosValue(value = "${express.sf.pushContact:7551234567}", autoRefreshed = true)
    private String pushContact;
    @NacosValue(value = "${express.sf.pushMobile:7551234567}", autoRefreshed = true)
    private String pushMobile;

    //************************** start 寄件信息 **************************

    //************************** start 收件信息 **************************

    /**
     *
     */
    @NacosValue(value = "${express.sf.pullAddress:7551234567}", autoRefreshed = true)
    private String pullAddress;
    @NacosValue(value = "${express.sf.pullProvince:7551234567}", autoRefreshed = true)
    private String pullProvince;
    @NacosValue(value = "${express.sf.pullCity:7551234567}", autoRefreshed = true)
    private String pullCity;
    @NacosValue(value = "${express.sf.pullCounty:7551234567}", autoRefreshed = true)
    private String pullCounty;
    @NacosValue(value = "${express.sf.pullContact:7551234567}", autoRefreshed = true)
    private String pullContact;
    @NacosValue(value = "${express.sf.pullMobile:7551234567}", autoRefreshed = true)
    private String pullMobile;

    /**
     * 打印顺丰单
     * CIQ 国检
     * //0.校验销售单 赋值
     *
     * @param request
     * @return
     */
    @PostMapping("/print")
    public SingleResponse print(@RequestBody ExpressPrintRequest request) {
        //打印类型
        PrintOptionResult printOptionResult = saleOrderFacade.printOption(request.getSerialNo());
        if (StringUtils.isNotBlank(printOptionResult.getBizOrderCode())) {
            for (String code : printOptionResult.getBizOrderCode().split(",")) {
                if (Objects.nonNull(refundCreatedService.getOne(new LambdaQueryWrapper<DouYinRefundCreated>()
                        .eq(DouYinRefundCreated::getOrderId, code)
                        .eq(DouYinRefundCreated::getAftersaleStatus, 6L)
                        .eq(DouYinRefundCreated::getAftersaleType, 2L)))) {
                    return SingleResponse.buildFailure(500, "当前发货中有抖音用户申请售后的商品，请处理完后再发货！！");
                }
            }
        }
        Integer print = printOptionResult.getPrintOption();
        //1.校验发货清单和物流下单记录成功记录 2。查询发货单有无顺丰单号
        List<StoreWorkDetailResult> resultList = storeWorkQueryFacade.deliveryDetailByPrint(StoreWorkDeliveryDetailRequest.builder().originSerialNo(request.getSerialNo()).belongingStoreId(UserContext.getUser().getStore().getId()).build());
        //查询下单记录有无记录
        ExpressOrderQueryResult queryResult = expressOrderFacade.query(ExpressOrderQueryRequest.builder().serialNo(request.getSerialNo()).build());
        //打印的requestId
        String requestID = UUID.randomUUID().toString().replace("-", "");
        List<ExpressOrderQueryResult.ExpressOrderQueryDTO> collect = queryResult.getList().stream().filter(expressOrderQueryDTO -> expressOrderQueryDTO.getExpressState().equals(2)).collect(Collectors.toList());
        List<String> expressNoList = new ArrayList();
        //不存在出库单
        if (CollectionUtils.isEmpty(resultList)) {
            return SingleResponse.buildFailure(800, "未知错误");
        }
        //已下单 记录也录入 发货也录入
        else if (CollectionUtils.isNotEmpty(resultList) && collect.stream().anyMatch(expressOrderQueryDTO -> expressOrderQueryDTO.getSonSerialNo().equals(request.getSerialNo()))) {
            //写入打印日志
            for (ExpressOrderQueryResult.ExpressOrderQueryDTO expressOrderQueryDTO : collect) {
                expressOrderPrintFacade.create(ExpressOrderPrintCreateRequest.builder().requestId(requestID).printTemplate(templateCode).expressOrderId(expressOrderQueryDTO.getId()).build());
                expressNoList.add(expressOrderQueryDTO.getExpressNo());
            }
        } else if (CollectionUtils.isNotEmpty(resultList) && resultList.stream().anyMatch(storeResult -> Objects.nonNull(storeResult.getExpressNumber()))) {
            return SingleResponse.buildFailure(800, "未知错误");
        }
        //有顺丰单但是不匹配 无顺丰单
        else {
            //第一个订单
            String requestId = UUID.randomUUID().toString().replace("-", "");
            SfExpressCreateOrderResult result = null;
            ExpressOrderCreateResult expressOrderCreateResult = null;
            //要下单 //0.无需国检 滨江到其他  //1.线下需国检 滨江到国检 //2.平台需国检 滨江到国检
            switch (print) {
                case -1:
                    return SingleResponse.buildFailure(800, "不允许打印");
                case 0:
                case 1:
                    StoreWorkDetailResult storeWorkDetailResult = resultList.get(FlywheelConstant.INDEX);
                    if (print.equals(0) && (
                            StringUtils.isNotBlank(storeWorkDetailResult.getContactName()) &&
                                    StringUtils.isNotBlank(storeWorkDetailResult.getContactPhone()) &&
                                    StringUtils.isNotBlank(storeWorkDetailResult.getContactAddress()) &&
                                    StringUtils.contains(storeWorkDetailResult.getContactName(), "*") &&
                                    StringUtils.contains(storeWorkDetailResult.getContactPhone(), "*") &&
                                    StringUtils.contains(storeWorkDetailResult.getContactAddress(), "*")
                    )) {
                        //脱敏
                        return SingleResponse.buildFailure(800, "不允许打印");
                    }
                    expressOrderCreateResult = expressOrderFacade.create(ExpressOrderCreateRequest.builder().serialNo(request.getSerialNo()).sonSerialNo(request.getSerialNo()).douYinShopId(-1L).expressChannel(1).requestId(requestId).build());

                    result = create(requestId, storeWorkDetailResult, request.getSerialNo(), print.equals(1), printOptionResult.getShopId());
                    break;
                case 2:
//                    客户
                    expressOrderCreateResult = expressOrderFacade.create(ExpressOrderCreateRequest.builder().serialNo(request.getSerialNo()).sonSerialNo(request.getSerialNo()).douYinShopId(-1L).expressChannel(1).requestId(requestId).build());
                    result = create(requestId, request.getSerialNo(), printOptionResult.getDouYinOption(), resultList.get(FlywheelConstant.INDEX), printOptionResult.getShopId());
                    break;
                default:
                    return SingleResponse.buildFailure(800, "未知错误");
            }
            if (isCheck(result)) {
                String waybillNo = result.getMsgData().getWaybillNoInfoList().get(FlywheelConstant.INDEX).getWaybillNo();
//                        修改物流单记录
                editSuccessfulSfExpress(waybillNo, resultList, expressOrderCreateResult.getId());
                //写入打印日志
                expressOrderPrintFacade.create(ExpressOrderPrintCreateRequest.builder().requestId(requestID).expressOrderId(expressOrderCreateResult.getId()).printTemplate(templateCode).build());
                expressNoList.add(waybillNo);
            } else {
                expressOrderFacade.edit(ExpressOrderEditRequest.builder().expressState(3).id(expressOrderCreateResult.getId()).errorMsg(result.getErrorMsg().toString()).build());
                return SingleResponse.buildFailure(800, result.getErrorMsg().toString());
            }
        }
        return SingleResponse.of(ExpressPrintResult.builder().expressNoList(expressNoList).productName(resultList.stream().map(result -> StringUtils.join(Arrays.asList(result.getBrandName(), result.getSeriesName(), result.getModel()), "/")).collect(Collectors.joining("\n"))).remarks(printOptionResult.getRemarks()).accessToken(expressFacade.getAccessToken(SfExpressAccessTokenRequest.builder().build()).getAccessToken()).requestID(requestID).templateCode(templateCode).build());
    }


    /**
     * 封装第一步
     *
     * @param serialNo
     * @return
     */
    private SfExpressCreateOrderRequest.SfExpressCreateOrderRequestBuilder createOrderRequestBuilder(String serialNo) {

        return SfExpressCreateOrderRequest.builder()
                .orderId(serialNo)
                .language(FlywheelConstant.LANGUAGE)
                //月结卡号
                .monthlyCard(monthlyCard)
                //订单货物总重量（郑州空港海关必填）， 若为子母件必填， 单位千克， 精确到小数点后3位，如果提供此值， 必须>0 (子母件需>6)
                .totalWeight(2.0)
                //包裹数，一个包裹对应一个运单号；若包裹数大于1，则返回一个母运单号和N-1个子运单号
                .parcelQty(1)
                //付款方式，支持以下值： 1:寄方付 2:收方付 3:第三方付
//                .payMethod(1)
                //快件自取，支持以下值： 1：客户同意快件自取 0：客户不同意快件自取
//                .isOneselfPickup(1)
//              扩展属性
                .extraInfoList(Arrays.asList())
                //2 顺丰标快 快件产品类别， 支持附录 《快件产品类别表》 的产品编码值，仅可使用与顺丰销售约定的快件产品
                .expressTypeId(2);
    }

    /**
     * 发货人
     *
     * @return
     */
    private SfExpressCreateOrderRequest.ContactInfoListDTO contactInfoListDTOByPush(Integer shopId) {
        return douyinPrintMappingService.list(Wrappers.<DouyinPrintMapping>lambdaQuery().eq(DouyinPrintMapping::getShopId, shopId))
                .stream()
                .map(t -> SfExpressCreateOrderRequest.ContactInfoListDTO.builder()
                        .company("稀蜴真品")
                        .contactType(1)
                        .address(t.getDetailAddress())
                        .province(t.getProvinceName())
                        .city(t.getCityName())
                        .county(t.getDistrictName())
                        .contact(t.getName())
                        .mobile(t.getMobile())
                        .build())
                .findFirst()
                .orElseGet(() -> SfExpressCreateOrderRequest.ContactInfoListDTO.builder()
                        .company("稀蜴真品")
                        .contactType(1)
                        .address(pushAddress)
                        .province(pushProvince)
                        .city(pushCity)
                        .county(pushCounty)
                        .contact(pushContact)
                        .mobile(pushMobile)
                        .build());
    }

    /**
     * 收件人国检
     *
     * @return
     */
    private SfExpressCreateOrderRequest.ContactInfoListDTO contactInfoListDTOByPull() {

        return SfExpressCreateOrderRequest.ContactInfoListDTO.builder()
                .company("国检")
                .contactType(2)
                .address(pullAddress)
                .province(pullProvince)
                .city(pullCity)
                .county(pushCounty)
                .company("")
                .contact(pullContact)
                .mobile(pullMobile)
                .build();
    }

    /**
     * 客户
     *
     * @param contactName
     * @param contactPhone
     * @param contactAddress
     * @return
     */
    private SfExpressCreateOrderRequest.ContactInfoListDTO contactInfoListDTOByPull(String contactName, String contactPhone, String contactAddress) {

        Map<String, String> addressResolutionMap = addressResolution(contactAddress);

        return SfExpressCreateOrderRequest.ContactInfoListDTO.builder()
                .contactType(2)
                .address(contactAddress)
                .province(addressResolutionMap.get("province"))
                .city(addressResolutionMap.get("city"))
                .county(addressResolutionMap.get("county"))
                .company("")
                .contact(contactName)
                .mobile(contactPhone)
                .build();
    }

    /**
     * 抖音抽检国检
     *
     * @param douYinScInfo
     * @return
     */
    private SfExpressCreateOrderRequest.ContactInfoListDTO contactInfoListDTOByDouyin(DouYinScInfo douYinScInfo) {
        return SfExpressCreateOrderRequest.ContactInfoListDTO.builder()
                .contactType(2)
                .address(douYinScInfo.getScAddress())
                .province(douYinScInfo.getScProvince())
                .city(douYinScInfo.getScCity())
                .county(douYinScInfo.getScDistrict())
//                .company(douYinScInfo.getScName())
                .contact(douYinScInfo.getScName())
                .mobile(douYinScInfo.getScPhone())
                .build();
    }

    /**
     * 封装商品基本信息
     *
     * @param s 品牌，系列，型号，表身号
     * @return
     */
    private SfExpressCreateOrderRequest.CargoDetailsDTO cargoDetailsDTO(String... s) {
        return SfExpressCreateOrderRequest.CargoDetailsDTO.builder()
//                                .amount(308.0)
//                                .count(1.0)
                .name(StringUtils.join(s, "/"))
//                                .unit("个")
//                                .volume(0.0)
//                                .weight(0.1)
                .build();
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

        if (m.matches()) {
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
     * @param requestId
     * @param storeWorkDetailResult
     * @param serialNo
     * @param CIQ                   是否走国检 true false
     * @return
     */
    private SfExpressCreateOrderResult create(String requestId, StoreWorkDetailResult storeWorkDetailResult, String serialNo, Boolean CIQ, Integer shopId) {
        SfExpressCreateOrderRequest sfExpressCreateOrderRequest = createOrderRequestBuilder(serialNo)
                .contactInfoList(
                        Arrays.asList(contactInfoListDTOByPush(shopId), CIQ ? contactInfoListDTOByPull() : contactInfoListDTOByPull(storeWorkDetailResult.getContactName(), storeWorkDetailResult.getContactPhone(), storeWorkDetailResult.getContactAddress()))
                ).cargoDetails(
                        Arrays.asList(cargoDetailsDTO(storeWorkDetailResult.getBrandName(), storeWorkDetailResult.getSeriesName(), storeWorkDetailResult.getModel(), storeWorkDetailResult.getStockSn()))
                ).build();
        //记录操作
        sfExpressCreateOrderRequest.setRequestID(requestId);
        SfExpressCreateOrderResult result = expressFacade.createOrder(sfExpressCreateOrderRequest);
        return result;
    }

    /**
     * 地址状态
     *
     * @param requestId
     * @param serialNo
     * @param scId
     * @return
     */
    private SfExpressCreateOrderResult create(String requestId, String serialNo, Integer scId, StoreWorkDetailResult storeWorkDetailResult, Integer shopId) {

        DouYinScInfo douYinScInfo = douYinScInfoService.getById(scId);

        SfExpressCreateOrderRequest sfExpressCreateOrderRequest = createOrderRequestBuilder(serialNo)
                .contactInfoList(
                        Arrays.asList(contactInfoListDTOByPush(shopId), contactInfoListDTOByDouyin(douYinScInfo))
                ).cargoDetails(
                        Arrays.asList(cargoDetailsDTO(storeWorkDetailResult.getBrandName(), storeWorkDetailResult.getSeriesName(), storeWorkDetailResult.getModel(), storeWorkDetailResult.getStockSn()))
                ).build();
        //记录操作
        sfExpressCreateOrderRequest.setRequestID(requestId);
        SfExpressCreateOrderResult result = expressFacade.createOrder(sfExpressCreateOrderRequest);
        return result;
    }

    /**
     * @param waybillNo      物流单号
     * @param resultList     仓库出库单列表
     * @param expressOrderId 物流单id
     */
    private void editSuccessfulSfExpress(String waybillNo, List<StoreWorkDetailResult> resultList, Integer expressOrderId) {

        //写入收货信息 更新到发货单
        resultList.forEach(e -> {
            StoreWorkEditRequest storeWorkEditRequest = new StoreWorkEditRequest();
            storeWorkEditRequest.setWorkId(e.getId());
            storeWorkEditRequest.setDeliveryExpressNumber(waybillNo);
            storeWorkFacade.edit(storeWorkEditRequest);
        });

        //更新物流日志
        expressOrderFacade.edit(ExpressOrderEditRequest.builder().expressState(2).id(expressOrderId).expressNo(waybillNo).build());
    }

    /**
     * 判断是否符合
     *
     * @param result
     * @return
     */
    private boolean isCheck(SfExpressCreateOrderResult result) {
        return Objects.nonNull(result) && (result.getSuccess().equals("true") && result.getErrorCode().equals("S0000"));
    }

    /**
     * 抖店打印 取消
     *
     * @param request
     * @return
     */
    @PostMapping("/printCancelByDd")
    public SingleResponse printCancelByDd(@RequestBody ExpressCancelRequest request) {

        if (ObjectUtils.isEmpty(request) || (StringUtils.isBlank(request.getSerialNo()) && StringUtils.isBlank(request.getExpressNo()))) {
            return SingleResponse.buildFailure(800, "参数有误");
        }
        ExpressOrderQueryResult expressOrderQueryResult = expressOrderFacade.query(ExpressOrderQueryRequest.builder().expressNo(request.getExpressNo()).serialNo(request.getSerialNo()).build());
        if (ObjectUtils.isEmpty(expressOrderQueryResult) || CollectionUtils.isEmpty(expressOrderQueryResult.getList())) {
            return SingleResponse.buildFailure(800, "参数有误");
        }
        ExpressOrderQueryResult.ExpressOrderQueryDTO expressOrderQueryDTO = expressOrderQueryResult.getList().stream().findFirst().orElse(null);

        if (ObjectUtils.isEmpty(expressOrderQueryDTO)) {
            return SingleResponse.buildFailure(800, "参数有误");
        }

        AccessToken accessToken = DouYinConfig.getAccessToken(expressOrderQueryDTO.getDouYinShopId());

        LogisticsCancelOrderRequest logisticsCancelOrderRequest = new LogisticsCancelOrderRequest();
        LogisticsCancelOrderParam param = logisticsCancelOrderRequest.getParam();
        param.setLogisticsCode("shunfeng");
        param.setTrackNo(request.getExpressNo());
        log.info("用于ISV/商家ERP系统 端发起取消已获取的电子面单号:{}", logisticsCancelOrderRequest.toString());
        LogisticsCancelOrderResponse logisticsCancelOrderResponse = logisticsCancelOrderRequest.execute(accessToken);
        log.info("用于ISV/商家ERP系统 端发起取消已获取的电子面单号:{}", logisticsCancelOrderResponse.toString());

        if (!"10000".equals(logisticsCancelOrderResponse.getCode())) {
            return SingleResponse.buildFailure(Integer.parseInt(logisticsCancelOrderResponse.getCode()), logisticsCancelOrderResponse.getSubMsg());
        }
        //更新物流日志 取消
        expressOrderFacade.edit(ExpressOrderEditRequest.builder().expressState(4)
                .id(expressOrderQueryResult.getList().get(FlywheelConstant.INDEX).getId())
                .build());
        return SingleResponse.buildSuccess();
    }

    /**
     * 抖店打印 取消
     *
     * @param request
     * @return
     */
    @PostMapping("/listShopNetsite")
    public SingleResponse listShopNetsite(@RequestBody ExpressPrintRequest request) {

        AccessToken accessToken = DouYinConfig.getAccessToken(65711065L);

        LogisticsListShopNetsiteRequest logisticsListShopNetsiteRequest = new LogisticsListShopNetsiteRequest();
        LogisticsListShopNetsiteParam param = logisticsListShopNetsiteRequest.getParam();
        param.setLogisticsCode("shunfeng");

        LogisticsListShopNetsiteResponse logisticsListShopNetsiteResponse = logisticsListShopNetsiteRequest.execute(accessToken);

        return SingleResponse.of(logisticsListShopNetsiteResponse);
    }

    /**
     * 抖店打印 自定义模版
     *
     * @param request
     * @return
     */
    @PostMapping("/getCustomTemplateList")
    public SingleResponse getCustomTemplateList(@RequestBody ExpressPrintRequest request) {

        AccessToken accessToken = DouYinConfig.getAccessToken(Long.valueOf(request.getSerialNo()));

        LogisticsGetCustomTemplateListRequest logisticsGetCustomTemplateListRequest = new LogisticsGetCustomTemplateListRequest();
        LogisticsGetCustomTemplateListParam param = logisticsGetCustomTemplateListRequest.getParam();
        param.setLogisticsCode("shunfeng");

        LogisticsGetCustomTemplateListResponse logisticsGetCustomTemplateListResponse = logisticsGetCustomTemplateListRequest.execute(accessToken);

        return SingleResponse.of(logisticsGetCustomTemplateListResponse);
    }

    /**
     * 抖店打印 标准模版
     *
     * @param request
     * @return
     */
    @PostMapping("/getTemplateList")
    public SingleResponse getTemplateList(@RequestBody ExpressPrintRequest request) {

        AccessToken accessToken = DouYinConfig.getAccessToken(Long.valueOf(request.getSerialNo()));

        LogisticsTemplateListRequest logisticsTemplateListRequest = new LogisticsTemplateListRequest();
        LogisticsTemplateListResponse logisticsTemplateListResponse = logisticsTemplateListRequest.execute(accessToken);

        return SingleResponse.of(logisticsTemplateListResponse);
    }

    @Resource
    private DdSfExpressContext ddSfExpressContext;

    /**
     * 抖店打印 策略请求
     *
     * @param requestBatch
     * @return
     */
    @PostMapping("/printByDd")
    public SingleResponse printByDd(@RequestBody ExpressBatchPrintRequest requestBatch) {
        if (CollectionUtils.isEmpty(requestBatch.getSerialNoList())) {
            return SingleResponse.of(ImportResult.<DdExpressPrintResult>builder().successList(Arrays.asList()).errList(Arrays.asList()).build());
        }
        /**
         * 业务处理 start 迭代每一条数据
         */
        List<DdExpressPrintResult> success = new ArrayList<>();
        List<String> fail = new ArrayList<>();
        for (String serialNo : requestBatch.getSerialNoList()) {

            ExpressPrintRequest request = ExpressPrintRequest.builder().serialNo(serialNo).build();
            //打印类型
            PrintOptionResult printOptionResult = saleOrderFacade.printOptionByDd(request.getSerialNo());

            if (ObjectUtils.isEmpty(printOptionResult.getShopId())) {
                fail.add("未查询对应的门店:=" + printOptionResult.getShopId());
                continue;
            }
//            if (printOptionResult.getStockIdList().stream().anyMatch(Objects::isNull)) {
//                fail.add("未填入表身号，禁止打印:=" + printOptionResult.getShopId());
//                continue;
//            }
            DouYinShopMapping douYinShopMapping = douYinShopMappingService.list(Wrappers.<DouYinShopMapping>lambdaQuery().eq(DouYinShopMapping::getShopId, printOptionResult.getShopId())).stream().findFirst().orElse(null);
            if (ObjectUtils.isEmpty(douYinShopMapping)) {
                fail.add("未查询对应的门店:=" + printOptionResult.getShopId());
                continue;
            }
            DouyinPrintMapping douyinPrintMapping = douyinPrintMappingService.list(Wrappers.<DouyinPrintMapping>lambdaQuery()
                    .eq(DouyinPrintMapping::getShopId, printOptionResult.getShopId())
                    .eq(DouyinPrintMapping::getDouYinShopId, douYinShopMapping.getDouYinShopId())
            ).stream().findFirst().orElse(null);
            if (ObjectUtils.isEmpty(douYinShopMapping)) {
                fail.add("未查询对应的门店:=" + printOptionResult.getShopId());
                continue;
            }
            AccessToken accessToken = DouYinConfig.getAccessToken(douYinShopMapping.getDouYinShopId());
            //获取签名
            String sign = StringUtils.join(Arrays.asList(
                    "access_token=" + accessToken.getAccessToken(), "app_key=" + GlobalConfig.getGlobalConfig().getAppKey(), "method=logistics.getShopKey", "param_json={}", "timestamp=" + String.valueOf(System.currentTimeMillis()), "sign=" + SignUtil.sign(GlobalConfig.getGlobalConfig().getAppKey(), GlobalConfig.getGlobalConfig().getAppSecret(), "logistics.getShopKey", String.valueOf(System.currentTimeMillis()), "{}", "2"), "sign_method=md5", "v=2"), "&");
            log.info("打印参数params:{}", sign);
            //1.校验发货清单和物流下单记录成功记录 2。查询发货单有无顺丰单号
            List<StoreWorkDetailResult> resultList = storeWorkQueryFacade.deliveryDetailByPrint(StoreWorkDeliveryDetailRequest.builder().originSerialNo(request.getSerialNo()).belongingStoreId(UserContext.getUser().getStore().getId()).build());
            //不存在出库单
            if (CollectionUtils.isEmpty(resultList)) {
                fail.add("未查询对应的快递单号:=" + serialNo);
                continue;
            }
            //查询下单记录有无记录
            ExpressOrderQueryResult queryResult = expressOrderFacade.query(ExpressOrderQueryRequest.builder().serialNo(request.getSerialNo()).build());
            List<ExpressOrderQueryResult.ExpressOrderQueryDTO> collect = queryResult.getList().stream().filter(expressOrderQueryDTO -> expressOrderQueryDTO.getExpressState().equals(2)).collect(Collectors.toList());
            //数据节点id
            String requestId = UUID.randomUUID().toString().replace("-", "");
            String requestID = UUID.randomUUID().toString().replace("-", "");
            String taskID = UUID.randomUUID().toString().replace("-", "");
            String documentID = UUID.randomUUID().toString().replace("-", "");
            //已下单 记录也录入 发货也录入
            if (CollectionUtils.isNotEmpty(resultList) && collect.stream().anyMatch(expressOrderQueryDTO -> expressOrderQueryDTO.getSonSerialNo().equals(request.getSerialNo()))) {
                if (collect.size() != 1) {
                    fail.add("业务单超限:=" + serialNo);
                    continue;
                }
                //写入打印日志
                for (ExpressOrderQueryResult.ExpressOrderQueryDTO expressOrderQueryDTO : collect) {
                    //打印数据
                    success.add(createDdExpressPrintResult(accessToken, printOptionResult, douyinPrintMapping, expressOrderQueryDTO.getExpressNo(), sign, expressOrderQueryDTO.getId(), requestID, taskID, documentID));
                    break;
                }
                continue;
            } else if (CollectionUtils.isNotEmpty(resultList) && resultList.stream().anyMatch(storeResult -> Objects.nonNull(storeResult.getExpressNumber()))) {
                fail.add("未下单的出库单号:=" + serialNo);
                continue;
            }
            //有顺丰单但是不匹配 无顺丰单
            else {
                ExpressCreateRequest build = ExpressCreateRequest
                        .builder()
                        .printOptionResult(printOptionResult).accessToken(accessToken).douYinShopMapping(douYinShopMapping).logisticsNewCreateOrderRequest(new LogisticsNewCreateOrderRequest()).resultList(resultList)
                        .requestId(requestId).requestID(requestID).taskID(taskID).documentID(documentID)
                        .build();
                ExpressCreateResult expressCreateResult = ddSfExpressContext.create(build);
                if (ObjectUtils.isEmpty(expressCreateResult) || !"10000".equals(expressCreateResult.getLogisticsNewCreateOrderResponse().getCode())) {
                    fail.add("抖店提示信息:=" + expressCreateResult.getLogisticsNewCreateOrderResponse().getSubMsg());
                    continue;
                }
                //打印数据
                success.add(createDdExpressPrintResult(accessToken, printOptionResult, build.getDouyinPrintMapping(), build.getExpressOrderCreateResult().getWaybillNo(), sign, build.getExpressOrderCreateResult().getId(), requestID, taskID, documentID));
                continue;
            }
        }

        /**
         * 业务处理 end
         */
        return SingleResponse.of(ImportResult.<DdExpressPrintResult>builder()
                .successList(success)
                .errList(fail)
                .build());
    }

    /**
     * 获取打印信息
     *
     * @param accessToken        抖店token
     * @param printOptionResult  打印数据
     * @param douyinPrintMapping 抖音打印映射
     * @param expressNo          快递单号
     * @param params             抖店参数
     * @param expressId          快递单号id
     * @param requestID
     * @param taskID
     * @param documentID
     * @return
     */
    private DdExpressPrintResult createDdExpressPrintResult(AccessToken accessToken, PrintOptionResult printOptionResult, DouyinPrintMapping douyinPrintMapping,
                                                            String expressNo, String params, Integer expressId,
                                                            String requestID, String taskID, String documentID) {
        //写入打印日志
        expressOrderPrintFacade.create(ExpressOrderPrintCreateRequest.builder().requestId(requestID).expressOrderId(expressId).printTemplate(templateCode).build());
        //返回打印信息
        LogisticsWaybillApplyRequest logisticsWaybillApplyRequest = new LogisticsWaybillApplyRequest();
        LogisticsWaybillApplyParam logisticsWaybillApplyRequestParam = logisticsWaybillApplyRequest.getParam();
        WaybillAppliesItem waybillAppliesItem = new WaybillAppliesItem();
        waybillAppliesItem.setLogisticsCode("shunfeng");
        waybillAppliesItem.setTrackNo(expressNo);
        logisticsWaybillApplyRequestParam.setWaybillApplies(Arrays.asList(waybillAppliesItem));

        log.info("获取面单信息请求:{}", logisticsWaybillApplyRequest.toString());
        LogisticsWaybillApplyResponse logisticsWaybillApplyResponse = logisticsWaybillApplyRequest.execute(accessToken);
        log.info("获取面单信息响应:{}", logisticsWaybillApplyResponse.toString());

        String printData = logisticsWaybillApplyResponse.getData().getWaybillInfos().get(FlywheelConstant.INDEX).getPrintData();
        String sign = logisticsWaybillApplyResponse.getData().getWaybillInfos().get(FlywheelConstant.INDEX).getSign();

        return DdExpressPrintResult.builder()
                .cmd("print")
                .requestID(requestID)
                .version("1.0")
                .task(
                        DdExpressPrintResult.TaskDTO.builder()
                                .taskID(taskID)
                                .preview(Boolean.FALSE)
                                .printer("")
                                .documents(Arrays.asList(
                                        DdExpressPrintResult.DocumentsDTO.builder()
                                                .docNo(expressNo)
                                                .documentID(documentID)
                                                .contents(Arrays.asList(
                                                        DdExpressPrintResult.ContentsDTO.builder()
                                                                .templateURL(douyinPrintMapping.getTemplateUrl())
                                                                .params(params)
                                                                .signature(sign)
                                                                .encryptedData(printData)
                                                                .addData(new HashMap<String, Object>() {{
                                                                    put("senderInfo", DdExpressPrintResult.SenderInfo.builder()
                                                                            .address(DdExpressPrintResult.Address.builder()
                                                                                    .countryCode("CHN")
                                                                                    .provinceName(douyinPrintMapping.getProvinceName())
                                                                                    .cityName(douyinPrintMapping.getCityName())
                                                                                    .districtName(douyinPrintMapping.getDistrictName())
                                                                                    .streetName(douyinPrintMapping.getStreetName())
                                                                                    .detailAddress(douyinPrintMapping.getDetailAddress())
                                                                                    .build())
                                                                            .contact(DdExpressPrintResult.Contact.builder()
                                                                                    .mobile(
                                                                                            StrUtil.replace(douyinPrintMapping.getMobile(), 3, douyinPrintMapping.getMobile().length() - 4, '*')
                                                                                    )
                                                                                    .name(douyinPrintMapping.getName())
                                                                                    .build())
                                                                            .build());
                                                                }})
                                                                .build()
                                                        ,
                                                        DdExpressPrintResult.DataDTO.builder()
                                                                .templateURL(douyinPrintMapping.getCustomTemplateUrl())
                                                                .data(StringUtils.isNotBlank(printOptionResult.getSpotCheckCode()) ? new HashMap<String, String>() {{
                                                                    put("spotCheckCode", "国检码：" + printOptionResult.getSpotCheckCode());
                                                                    put("productName", StringUtils.join(Arrays.asList("销售备注：" + ((StringUtils.isBlank(printOptionResult.getRemarks()) ? "无" : printOptionResult.getRemarks())), printOptionResult.getPrintProductName()), "\n"));
                                                                    put("remarks", printOptionResult.getRemarks());
                                                                    put("model", printOptionResult.getModel());
                                                                }} : new HashMap<String, String>() {{
                                                                    put("productName", StringUtils.join(Arrays.asList("销售备注：" + ((StringUtils.isBlank(printOptionResult.getRemarks()) ? "无" : printOptionResult.getRemarks())), printOptionResult.getPrintProductName()), "\n"));
                                                                    put("remarks", printOptionResult.getRemarks());
                                                                    put("model", printOptionResult.getModel());
                                                                }})
                                                                .build()
                                                ))
                                                .build()
                                ))
                                .build()
                )
                .build();
    }

}
