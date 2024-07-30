package com.seeease.flywheel.serve.express.infrastructure;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.seeease.flywheel.express.result.infrastructure.SfSfExpressAccessTokenResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr. Du
 * @Description 顺丰access_token维护
 * @Date create in 2023/6/26 13:42
 */
@Component
@Slf4j
public class SfExpressConfig {

    private static final MediaType JSON = MediaType.get("application/x-www-form-urlencode");

    private LoadingCache<String, SfSfExpressAccessTokenResult> ACCESS_TOKEN_CACHE;
    /**
     * 沙箱环境
     */
    @NacosValue(value = "${express.sf.gateway.accessToken:https://sfapi-sbox.sf-express.com/oauth2/accessToken}", autoRefreshed = true)
    private String gatewayAccessToken;

    /**
     * XYWLKX3CG59P
     * 合作伙伴编码（即顾客编码）
     */
    @NacosValue(value = "${express.sf.partnerID:XYWLKX3CG59P}", autoRefreshed = true)
    private String partnerID;
    /**
     * Q9erZakvxyPasF0HIoJny8sJmUOgncKZ
     * 合作伙伴密钥 （即校验码）
     */
    @NacosValue(value = "${express.sf.secret:Q9erZakvxyPasF0HIoJny8sJmUOgncKZ}", autoRefreshed = true)
    private String secret;

    @PostConstruct
    public void init() {
        ACCESS_TOKEN_CACHE = CacheBuilder.newBuilder()
                .refreshAfterWrite(30, TimeUnit.MINUTES)
                .expireAfterAccess(35, TimeUnit.MINUTES)
                .build(new CacheLoader<String, SfSfExpressAccessTokenResult>() {
                    @Override
                    public SfSfExpressAccessTokenResult load(String appId) throws Exception {
                        Map<String, String> map = new HashMap<>(8);

//                        map.put("partnerID", partnerID);
//                        map.put("secret", secret);
//                        map.put("grantType", "password");

                        OkHttpClient client = new OkHttpClient().newBuilder()
                                .callTimeout(20, TimeUnit.SECONDS)
                                .writeTimeout(20, TimeUnit.SECONDS)
                                .readTimeout(20, TimeUnit.SECONDS)
                                .build();

                        RequestBody body = RequestBody.create(JSONObject.toJSONString(map), JSON);

                        Request request = new Request.Builder()
                                .url(gatewayAccessToken
                                        + "?partnerID=" + partnerID
                                        + "&secret=" + secret
                                        + "&grantType=" + "password"
                                )
                                .header("Content-type", "application/x-www-form-urlencoded;charset=UTF-8")
                                .post(body)
                                .build();
                        try (Response response = client.newCall(request).execute()) {
                            String responseBody = response.body().string();
                            log.info("响应数据={},partnerID={},secret={},gatewayAccessToken={}", JSONObject.toJSONString(responseBody), partnerID, secret, gatewayAccessToken);
                            return Optional.ofNullable(responseBody)
                                    .map(b -> JSONObject.parseObject(b, SfSfExpressAccessTokenResult.class))
                                    .filter(a -> a.getApiResultCode().equals("A1000") && Objects.nonNull(a.getAccessToken()))
                                    .orElse(null);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            return null;
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
    public SfSfExpressAccessTokenResult getAccessToken(String appId) {
        return ACCESS_TOKEN_CACHE.getUnchecked(Objects.requireNonNull(appId));
    }

    /**
     * @return
     */
    public SfSfExpressAccessTokenResult getAccessToken() {
        return getAccessToken(partnerID);
    }
}
