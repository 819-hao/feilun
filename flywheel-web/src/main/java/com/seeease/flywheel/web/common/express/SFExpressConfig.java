package com.seeease.flywheel.web.common.express;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.seeease.flywheel.express.result.infrastructure.SfSfExpressAccessTokenResult;
import com.seeease.flywheel.web.common.express.client.SFExpressClient;
import lombok.Getter;
import okhttp3.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Tiro
 * @date 2023/9/20
 */
@Getter
@Configuration
public class SFExpressConfig {
    private static final MediaType JSON = MediaType.get("application/x-www-form-urlencode");
    /**
     * 服务网关地址
     */
    @NacosValue(value = "${express.sf.gateway.service}", autoRefreshed = true)
    private String gatewayService;

    /**
     * AccessToken网关地址
     */
    @NacosValue(value = "${express.sf.gateway.accessToken}", autoRefreshed = true)
    private String gatewayAccessToken;

    /**
     * 合作伙伴编码（即顾客编码）
     */
    @NacosValue(value = "${express.sf.partnerID}", autoRefreshed = true)
    private String partnerID;
    /**
     * 合作伙伴密钥 （即校验码）
     */
    @NacosValue(value = "${express.sf.secret}", autoRefreshed = true)
    private String secret;
    /**
     * 月结帐号
     */
    @NacosValue(value = "${express.sf.monthlyCard}", autoRefreshed = true)
    private String monthlyCard;
    /**
     * 顺丰模版
     */
    @NacosValue(value = "${express.sf.templateCode}", autoRefreshed = true)
    private String templateCode;

    private LoadingCache<String, SfSfExpressAccessTokenResult> ACCESS_TOKEN_CACHE;

    @PostConstruct
    public void init() {
        ACCESS_TOKEN_CACHE = CacheBuilder.newBuilder()
                .refreshAfterWrite(30, TimeUnit.MINUTES)
                .expireAfterAccess(35, TimeUnit.MINUTES)
                .build(new CacheLoader<String, SfSfExpressAccessTokenResult>() {
                    @Override
                    public SfSfExpressAccessTokenResult load(String appId) throws Exception {
                        Map<String, String> map = new HashMap<>(8);

                        map.put("partnerID", partnerID);
                        map.put("secret", secret);
                        map.put("grantType", "password");

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
                            return Optional.ofNullable(response.body().string())
                                    .map(b -> JSONObject.parseObject(b, SfSfExpressAccessTokenResult.class))
                                    .filter(a -> a.getApiResultCode().equals("A1000") && Objects.nonNull(a.getAccessToken()))
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
    public SfSfExpressAccessTokenResult getAccessToken(String appId) {
        return ACCESS_TOKEN_CACHE.getUnchecked(Objects.requireNonNull(appId));
    }

    /**
     * @return
     */
    public SfSfExpressAccessTokenResult getAccessToken() {
        return getAccessToken(partnerID);
    }


    @Bean
    public SFExpressClient sfExpressClient(SFExpressConfig clientConfig) {
        return new SFExpressClient(clientConfig);
    }


}
