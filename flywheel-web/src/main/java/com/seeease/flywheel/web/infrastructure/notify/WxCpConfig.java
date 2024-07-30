package com.seeease.flywheel.web.infrastructure.notify;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Tiro
 * @date 2023/5/18
 */
@Component
public class WxCpConfig {
    public static final String GATEWAY_ACCESS_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    private LoadingCache<String, WxCpAccessToken> ACCESS_TOKEN_CACHE;

    /**
     * 小程序应用corpId
     */
    @NacosValue(value = "${wx.cp.corp-id:}", autoRefreshed = true)
    private String corpId;
    /**
     * 小程序应用corpSecret
     */
    @NacosValue(value = "${wx.cp.corp-secret:}", autoRefreshed = true)
    private String corpSecret;

    @PostConstruct
    public void init() {
        ACCESS_TOKEN_CACHE = CacheBuilder.newBuilder()
                .refreshAfterWrite(30, TimeUnit.MINUTES)
                .expireAfterAccess(35, TimeUnit.MINUTES)
                .build(new CacheLoader<String, WxCpAccessToken>() {
                    @Override
                    public WxCpAccessToken load(String appId) throws Exception {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(GATEWAY_ACCESS_TOKEN + "?corpid=" + corpId + "&corpsecret=" + corpSecret)
                                .build();
                        try (Response response = client.newCall(request).execute()) {
                            return Optional.ofNullable(response.body().string())
                                    .map(b -> JSONObject.parseObject(b, WxCpAccessToken.class))
                                    .filter(a -> Objects.nonNull(a.getAccessToken()))
                                    .orElse(null);
                        }
                    }
                });
    }
    /**
     * 缓存
     */


    /**
     * @param appId
     * @return
     */
    public WxCpAccessToken getAccessToken(String appId) {
        return ACCESS_TOKEN_CACHE.getUnchecked(Objects.requireNonNull(appId));
    }

    /**
     * @return
     */
    public WxCpAccessToken getAccessToken() {
        return getAccessToken(corpId);
    }
}
