package com.seeease.flywheel.web.common.context;

import com.doudian.open.core.AccessToken;
import com.doudian.open.core.AccessTokenBuilder;
import com.doudian.open.core.GlobalConfig;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Tiro
 * @date 2023/4/23
 */
public class DouYinConfig {
    private final static LoadingCache<Long, AccessToken> ACCESS_TOKEN_CACHE = CacheBuilder.newBuilder()
            .refreshAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<Long, AccessToken>() {
                @Override
                public AccessToken load(Long douYinShopId) throws Exception {
                    AccessToken accessToken = AccessTokenBuilder.build(douYinShopId);
                    if (accessToken.getExpireIn() < 3600) {
                        //过期时间小于1小时，刷新token
                        accessToken = AccessTokenBuilder.refresh(accessToken);
                    }
                    return accessToken;
                }
            });

    /**
     * 抖音平台获取AccessToken
     *
     * @param douYinShopId
     * @return
     */
    public static AccessToken getAccessToken(Long douYinShopId) {
        return ACCESS_TOKEN_CACHE.getUnchecked(Objects.requireNonNull(douYinShopId));
    }


    static {
        //【稀蜴抖音应用】 设置appKey和appSecret，全局设置一次
        GlobalConfig.initAppKey("7049966719791531556");
        GlobalConfig.initAppSecret("71e0c445-bec5-402b-96c1-2a3276cfc139");
    }

    @AllArgsConstructor
    @Getter
    public enum OrderStatus {
        /**
         * 已支付
         */
        PAYMENT(2L),
        /**
         * 已取消
         */
        CANCEL(4L),

        ;
        private Long value;
    }
}
