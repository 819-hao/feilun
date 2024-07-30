package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.maindata.IUserFacade;
import com.seeease.flywheel.maindata.entity.UserInfo;
import com.seeease.flywheel.sale.IMarketOrderUploadFacade;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.MarketOrderUploadRequest;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.sale.request.SaleOrderWarrantyPeriodUpdateRequest;
import com.seeease.flywheel.sale.result.MarketOrderUploadResult;
import com.seeease.flywheel.sale.result.SaleOrderWarrantyPeriodUpdateResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.infrastructure.service.impl.TMallServiceImpl;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.exception.GlobalExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/4/13
 */
@Slf4j
@DubboService(version = "1.0.0")
public class MarketOrderUploadController implements IMarketOrderUploadFacade {

    @DubboReference(check = false, version = "1.0.0")
    private IUserFacade userFacade;
    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade saleOrderFacade;

    @Resource
    private CreateCmdExe createCmdExe;


    @Override
    @GlobalExceptionHandler
    public SingleResponse<MarketOrderUploadResult> upload(MarketOrderUploadRequest request) {
        //查用户
        Map<String, UserInfo> userMap = userFacade.listUser(Arrays.asList(request.getFirstSalesman(), request.getSecondSalesman(), request.getThirdSalesman())
                        .stream()
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toList())).stream()
                .collect(Collectors.toMap(UserInfo::getUserid, Function.identity(), (k1, k2) -> k2));
        //组装创建参数
        SaleOrderCreateRequest createRequest = SaleOrderCreateRequest.builder()
                .bizOrderCode(request.getBizOrderCode())
                .saleType(SaleOrderTypeEnum.TO_C_XS.value)
                .saleMode(SaleOrderModeEnum.ON_LINE.value)
                .saleChannel(SaleOrderChannelEnum.XI_YI_SHOP.value)
                .receiverInfo(SaleOrderCreateRequest.ReceiverInfo.builder()
                        .receiverName(request.getCustomerName())
                        .receiverMobile(request.getCustomerPhone())
                        .receiverAddress(request.getCustomerAddress())
                        .build())
                .paymentMethod(SaleOrderPaymentMethodEnum.WECHAT.value)
                .owner(Objects.requireNonNull(request.getFirstSalesman()))
                .firstSalesman(userMap.get(request.getFirstSalesman()).getId())
                .secondSalesman(Optional.ofNullable(userMap.get(request.getSecondSalesman())).map(UserInfo::getId).orElse(null))
                .thirdSalesman(Optional.ofNullable(userMap.get(request.getThirdSalesman())).map(UserInfo::getId).orElse(null))
                .creator(SaleOrderCreateRequest.PrescriptiveCreator.builder()
                        .createdId(userMap.get(request.getFirstSalesman()).getId())
                        .createdBy(userMap.get(request.getFirstSalesman()).getName())
                        .build()
                )
                .shopId(Objects.requireNonNull(request.getShopId())) //下单门店
                .totalSalePrice(request.getTotalSalePrice())
                .details(request.getOrderLines()
                        .stream()
                        .map(t -> SaleOrderCreateRequest.BillSaleOrderLineDto
                                .builder()
                                .stockId(t.getStockId())
                                .clinchPrice(t.getClinchPrice())
                                .isCounterPurchase(t.isCounterPurchase() ? WhetherEnum.YES.getValue() : WhetherEnum.NO.getValue())
                                .isRepurchasePolicy(t.isCounterPurchase() ? WhetherEnum.YES.getValue() : WhetherEnum.NO.getValue())
                                .whetherFix(t.isWatchStrapFeeChangeFlag() ? WhetherEnum.YES.getValue() : WhetherEnum.NO.getValue())
                                .buyBackPolicy(t.getBuyBackPolicy())
                                .repurchasePolicyUrl(t.getRepurchasePolicyUrl())
                                .build()
                        )
                        .collect(Collectors.toList()))
                .build();

        //组装创建命令
        CreateCmd<SaleOrderCreateRequest> cmd = new CreateCmd<>();
        cmd.setBizCode(BizCode.SALE);
        cmd.setUseCase(UseCase.PROCESS_CREATE);
        cmd.setRequest(createRequest);

        //创建销售单
        Object res = createCmdExe.create(cmd);
        log.info("[商场订单创建: request={}| cmd={} | res={}]", JSONObject.toJSONString(request), JSONObject.toJSON(cmd), JSONObject.toJSONString(res));

        return SingleResponse.of(MarketOrderUploadResult.builder()
                .success(true)
                .build());
    }

    @Override
    public SaleOrderWarrantyPeriodUpdateResult warrantyPeriodUpdate(SaleOrderWarrantyPeriodUpdateRequest request) {
        log.info("稀蜴商城修改质保参数: {}", JSONObject.toJSONString(request));
        saleOrderFacade.warrantyPeriodUpdate(request);
        return SaleOrderWarrantyPeriodUpdateResult.builder().warrantyPeriod(5).build();
    }

    @Getter
    @AllArgsConstructor
    enum SaleOrderPaymentMethodEnum {
        WECHAT(1, "微信"),
        ;
        private Integer value;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum SaleOrderChannelEnum {
        XI_YI_SHOP(14, "稀蜴商城"),
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
        TO_C_XS(2, "个人销售"),
        ;
        private Integer value;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum SaleOrderStateEnum {
        UN_CONFIRMED(0, "待确认"),
        UN_STARTED(1, "待开始"),
        UNDER_WAY(2, "进行中"),
        COMPLETE(4, "已完成"),
        CANCEL_WHOLE(3, "全部取消"),
        ;
        private Integer value;
        private String desc;

        public static TMallServiceImpl.SaleOrderStateEnum fromCode(int value) {
            return Arrays.stream(TMallServiceImpl.SaleOrderStateEnum.values())
                    .filter(t -> value == t.getValue())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("枚举异常"));
        }
    }
}
