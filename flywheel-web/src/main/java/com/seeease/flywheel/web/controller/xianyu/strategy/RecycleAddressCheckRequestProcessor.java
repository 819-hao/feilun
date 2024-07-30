package com.seeease.flywheel.web.controller.xianyu.strategy;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuMethodEnum;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuShipTypeEnum;
import com.seeease.flywheel.web.controller.xianyu.request.RecycleAddressCheckRequest;
import com.seeease.flywheel.web.controller.xianyu.result.RecycleAddressCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Tiro
 * @date 2023/10/17
 */
@Slf4j
@Component
public class RecycleAddressCheckRequestProcessor implements BaseQiMenRequestProcessor<RecycleAddressCheckRequest, RecycleAddressCheckResult> {
    @Override
    public Class<RecycleAddressCheckRequest> requestClass() {
        return RecycleAddressCheckRequest.class;
    }

    @Override
    public XianYuMethodEnum getMethodEnum() {
        return XianYuMethodEnum.RECYCLE_ADDRESS_CHECK;
    }

    @Override
    public RecycleAddressCheckResult execute(RecycleAddressCheckRequest request) {
        log.info("闲鱼估价上门地址校验数据:{}", JSONObject.toJSONString(request));
        return RecycleAddressCheckResult.builder()
                .shipTypes(Lists.newArrayList(XianYuShipTypeEnum.PICKUP.getValue()))
                .build();
    }
}
