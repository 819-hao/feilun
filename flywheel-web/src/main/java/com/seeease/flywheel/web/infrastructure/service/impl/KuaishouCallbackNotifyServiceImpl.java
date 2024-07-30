package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kuaishou.merchant.open.api.KsMerchantApiException;
import com.kuaishou.merchant.open.api.client.AccessTokenKsMerchantClient;
import com.kuaishou.merchant.open.api.request.order.OpenSellerOrderGoodsDeliverRequest;
import com.kuaishou.merchant.open.api.response.order.OpenSellerOrderGoodsDeliverResponse;
import com.seeease.flywheel.web.common.context.KuaiShouConfig;
import com.seeease.flywheel.web.entity.KuaishouAppInfo;
import com.seeease.flywheel.web.entity.KuaishouCallbackNotify;
import com.seeease.flywheel.web.entity.KuaishouOrder;
import com.seeease.flywheel.web.entity.enums.WhetherNotifyEnum;
import com.seeease.flywheel.web.infrastructure.mapper.KuaishouCallbackNotifyMapper;
import com.seeease.flywheel.web.infrastructure.service.KuaishouAppInfoService;
import com.seeease.flywheel.web.infrastructure.service.KuaishouCallbackNotifyService;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.exception.e.OperationRejectedExceptionCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dmmasxnmf
 * @description 针对表【kuaishou_callback_notify(抖音消息通知)】的数据库操作Service实现
 * @createDate 2023-11-22 14:46:42
 */
@Service
public class KuaishouCallbackNotifyServiceImpl extends ServiceImpl<KuaishouCallbackNotifyMapper, KuaishouCallbackNotify>
        implements KuaishouCallbackNotifyService {

    @Resource
    private KuaishouAppInfoService kuaishouAppInfoService;

    /**
     * 1.订单为"待发货"状态&&无正在执行的退款&&订单API出参disableDeliveryReasonCode没有值时，允许操作订单发货API
     * 2.此API仅用于首个包裹发货，若使用一单多包裹功能，其他包裹需使用追加包裹API发货，详情查看《订单解决方案-一单多包裹场景》
     *
     * @param kuaiShouOrderList
     * @param expressNumber
     * @return
     */
    @Override
    public Map<Integer, WhetherNotifyEnum> deliveryNotify(List<KuaishouOrder> kuaiShouOrderList, String expressNumber) {

        KuaishouOrder order = kuaiShouOrderList.stream().findFirst().get();

        KuaishouAppInfo kuaishouAppInfo = kuaishouAppInfoService.list(Wrappers.<KuaishouAppInfo>lambdaQuery()
                .eq(KuaishouAppInfo::getOpenShopId, order.getKuaiShouShopId())
        ).stream().findFirst().orElse(null);

        Optional.ofNullable(kuaishouAppInfo).orElseThrow(() -> new OperationRejectedException((OperationRejectedExceptionCode) () -> "快手对应的配置项不存在"));

        AccessTokenKsMerchantClient client = new AccessTokenKsMerchantClient(kuaishouAppInfo.getAppId(), kuaishouAppInfo.getSignSecret());

        Map<Integer, WhetherNotifyEnum> map = new HashMap<>();

        for (KuaishouOrder kuaishouOrder : kuaiShouOrderList) {
            //请求下单接口
            OpenSellerOrderGoodsDeliverRequest request = new OpenSellerOrderGoodsDeliverRequest();
            request.setAccessToken(KuaiShouConfig.getAccessToken(Arrays.asList(kuaishouAppInfo.getAppId(), order.getSellerOpenId()).stream().collect(Collectors.joining(":"))));

            /**
             * 顺丰速运
             * 4
             */
            request.setExpressCode(4);

            request.setExpressNo(expressNumber);
            request.setOrderId(Long.parseLong(kuaishouOrder.getOrderId()));

            OpenSellerOrderGoodsDeliverResponse response = null;

            try {
                response = client.execute(request);
            } catch (KsMerchantApiException e) {
                log.error(e.getErrorMsg(), e);
            }

            if (response == null || response.getResult() != 1) {
                map.put(order.getId(), WhetherNotifyEnum.FAIL);
                continue;
            }
            map.put(order.getId(), WhetherNotifyEnum.SUCCESS);
        }

        return map;
    }
}




