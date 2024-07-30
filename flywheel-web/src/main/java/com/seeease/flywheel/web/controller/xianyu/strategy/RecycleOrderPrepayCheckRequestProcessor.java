package com.seeease.flywheel.web.controller.xianyu.strategy;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuMethodEnum;
import com.seeease.flywheel.web.controller.xianyu.request.RecycleOrderPrepayCheckRequest;
import com.seeease.flywheel.web.controller.xianyu.result.RecycleOrderPrepayCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

/**
 * 闲鱼请求服务商信用预付检验
 *
 * @author Tiro
 * @date 2023/10/17
 */
@Slf4j
@Component
public class RecycleOrderPrepayCheckRequestProcessor implements BaseQiMenRequestProcessor<RecycleOrderPrepayCheckRequest, RecycleOrderPrepayCheckResult> {
    @Override
    public Class<RecycleOrderPrepayCheckRequest> requestClass() {
        return RecycleOrderPrepayCheckRequest.class;
    }

    @Override
    public XianYuMethodEnum getMethodEnum() {
        return XianYuMethodEnum.RECYCLE_ORDER_PREPAY_CHECK;
    }

    @Override
    public RecycleOrderPrepayCheckResult execute(RecycleOrderPrepayCheckRequest request) {
        log.info("闲鱼估价预付检验数据:{}", JSONObject.toJSONString(request));
        return RecycleOrderPrepayCheckResult.builder()
                .creditPay(false)
                .creditPayAmount(NumberUtils.LONG_ZERO)
                .build();
    }
}
