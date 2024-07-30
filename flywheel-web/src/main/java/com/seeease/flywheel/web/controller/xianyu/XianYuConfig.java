package com.seeease.flywheel.web.controller.xianyu;

import com.alibaba.fastjson.JSON;
import com.taobao.api.internal.tmc.TmcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Tiro
 * @date 2023/10/16
 */
@Slf4j
@Configuration
public class XianYuConfig {
    public static final String APP_KEY = "34489754";
    public static final String APP_SECRET = "47ca20564c5f779f4a8b6ec51e6136b7";
    //session key有效期：2024-04-10
    public static final String SESSION_KEY = "6102710959c236057b1e2a2dac435540fa83d3112c627ef2215909035637";
    public static final String SERVER_URL = "https://eco.taobao.com/router/rest";
    public static final String MC_URL = "ws://mc.api.taobao.com/";
    // 测试环境
    public static final String PRE_MC_URL = "ws://premc.api.taobao.com";

    @Bean
    public FlywheelXianYuClient xianYuClient() {
        return new FlywheelXianYuClient(SERVER_URL, APP_KEY, APP_SECRET, SESSION_KEY);
    }

    @Bean
    @Profile("test")
    public TmcClient defaultTmcClientTest(FlywheelMessageHandler messageHandler) throws Exception {
        TmcClient client = new TmcClient(APP_KEY, APP_SECRET, "default"); // 关于default参考消息分组说明
        client.setQueueSize(100);  // 被推送的缓冲队列 不要设置太大 避免堆积过久 导致未即时处理 导致雪崩重发
        client.setThreadCount(2); // 处理消息线程数
        client.setMessageHandler(messageHandler);
        // 连接失败时会抛出异常, 请不要捕获, 避免未连接而不知晓, 第一次启动成功后, 后续断开会自动重连
        client.connect(PRE_MC_URL);

        log.info("【pre闲鱼消息】【tmc-client信息 client:{}】", JSON.toJSONString(client));
        log.info("【pre闲鱼消息】【api调用地址 getApiUrl:{}】", client.getApiUrl());
        log.info("【pre闲鱼消息】【tmc-client 在线状态 isOnline:{}】", client.isOnline());
        log.info("【pre闲鱼消息】【是否开启自动确认 isUseDefaultConfirm:{}】", client.isUseDefaultConfirm());

        return client;
    }

    @Bean
    @Profile("prod")
    public TmcClient defaultTmcClient(FlywheelMessageHandler messageHandler) throws Exception {
        TmcClient client = new TmcClient(APP_KEY, APP_SECRET, "default"); // 关于default参考消息分组说明
        client.setQueueSize(100);  // 被推送的缓冲队列 不要设置太大 避免堆积过久 导致未即时处理 导致雪崩重发
        client.setThreadCount(2); // 处理消息线程数
        client.setMessageHandler(messageHandler);
        // 连接失败时会抛出异常, 请不要捕获, 避免未连接而不知晓, 第一次启动成功后, 后续断开会自动重连
        client.connect(MC_URL);

        log.info("【闲鱼消息】【tmc-client信息 client:{}】", JSON.toJSONString(client));
        log.info("【闲鱼消息】【api调用地址 getApiUrl:{}】", client.getApiUrl());
        log.info("【闲鱼消息】【tmc-client 在线状态 isOnline:{}】", client.isOnline());
        log.info("【闲鱼消息】【是否开启自动确认 isUseDefaultConfirm:{}】", client.isUseDefaultConfirm());

        return client;
    }
}
