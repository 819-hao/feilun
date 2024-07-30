package com.seeease.flywheel.web.infrastructure.notify;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.BaseNotice;
import com.seeease.flywheel.notify.entity.RobotMessage;
import com.seeease.flywheel.notify.entity.TextRobotMessage;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Tiro
 * @date 2023/5/18
 */
@Slf4j
@Component
public class WxCpMessageFacade implements IWxCpMessageFacade {
    @Resource
    private MessageConvert messageConvert;
    @Resource
    private WxCpConfig wxCpConfig;

    private static final MediaType JSON = MediaType.get("application/json");
    private final static String GATEWAY_API = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
    private final static String WEBHOOK_GATEWAY_API = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=";


    @Override
    public void send(BaseNotice baseNotice) {
        try {
            String msg = JSONObject.toJSONString(messageConvert.convert(baseNotice));
            RequestBody body = RequestBody.create(msg, JSON);
            Request request = new Request.Builder()
                    .url(GATEWAY_API + wxCpConfig.getAccessToken().getAccessToken())
                    .post(body)
                    .build();
            Response response = new OkHttpClient().newCall(request).execute();
            log.info("企业微信小程序消息发送成功:{}-{}", msg, response.body().string());
        } catch (Exception e) {
            log.error("企业微信小程序消息发送异常:{}{}", JSONObject.toJSONString(baseNotice), e.getMessage(), e);
        }
    }

    @Override
    public void send(RobotMessage robotMessage) {
        try {
            String msg = JSONObject.toJSONString(robotMessage);
            RequestBody body = RequestBody.create(msg, JSON);
            Request request = new Request.Builder()
                    .url(WEBHOOK_GATEWAY_API + robotMessage.getKey())
                    .post(body)
                    .build();
            Response response = new OkHttpClient().newCall(request).execute();
            log.info("企业微信机器人消息发送成功:{}-{}", msg, response.body().string());
        } catch (Exception e) {
            log.error("企业微信机器人消息发送异常:{}{}", JSONObject.toJSONString(robotMessage), e.getMessage(), e);
        }
    }


}
