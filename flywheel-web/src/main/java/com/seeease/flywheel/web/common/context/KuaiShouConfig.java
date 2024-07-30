package com.seeease.flywheel.web.common.context;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.kuaishou.merchant.open.api.KsMerchantApiException;
import com.kuaishou.merchant.open.api.client.oauth.OauthAccessTokenKsClient;
import com.kuaishou.merchant.open.api.response.oauth.KsAccessTokenResponse;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.web.entity.KuaishouAppInfo;
import com.seeease.flywheel.web.entity.KuaishouTokenInfo;
import com.seeease.flywheel.web.infrastructure.service.KuaishouAppInfoService;
import com.seeease.flywheel.web.infrastructure.service.KuaishouTokenInfoService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Tiro
 * @date 2023/4/23
 */
@Slf4j
public class KuaiShouConfig {

    /**
     * String appKey = "ks666251271217652715";
     * String signSecret = "16d058dee884c96bad6b25a68db1a7a5";
     * String appSecret = "nhQpfR8O7IcG_Sn1MUjJXw";
     * String 消息秘钥 = "ynlHhkPWy0qPDDd1RfGQYQ==";
     */

    public final static LoadingCache<String, String> ACCESS_TOKEN_CACHE = CacheBuilder.newBuilder()
            .refreshAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                /**
                 *
                 * @param appIdAndOpenId appId:openId
                 * @return
                 * @throws Exception
                 */
                @Override
                public String load(String appIdAndOpenId) throws KsMerchantApiException {

                    String[] split = StringUtils.split(appIdAndOpenId, ":");
                    log.info("appKey={},openId={}", split[0], split[1]);

                    KuaishouTokenInfoService kuaishouTokenInfoService = SpringUtils.getBean(KuaishouTokenInfoService.class);

                    List<KuaishouTokenInfo> kuaishouTokenInfoList = kuaishouTokenInfoService.list(Wrappers.<KuaishouTokenInfo>lambdaQuery()
                            .eq(KuaishouTokenInfo::getAppId, split[0])
                            .eq(KuaishouTokenInfo::getOpenId, split[1])
                    );

                    if (CollectionUtils.isEmpty(kuaishouTokenInfoList)) {
                        log.error("未授权1,appKey={},openId={}", split[0], split[1]);
                        return "";
                    }

                    KuaishouTokenInfo kuaishouTokenInfo = kuaishouTokenInfoList.get(FlywheelConstant.INDEX);

                    if (System.currentTimeMillis() > kuaishouTokenInfo.getAccessTokenExpiresTime()) {

                        KuaishouAppInfoService kuaishouAppInfoService = SpringUtils.getBean(KuaishouAppInfoService.class);

                        List<KuaishouAppInfo> kuaishouAppInfoList = kuaishouAppInfoService.list(Wrappers.<KuaishouAppInfo>lambdaQuery().eq(KuaishouAppInfo::getAppId, split[0]));

                        if (CollectionUtils.isEmpty(kuaishouAppInfoList)) {
                            log.error("未授权2,appKey={},openId={}", split[0], split[1]);
                            return "";
                        }
                        KuaishouAppInfo kuaishouAppInfo = kuaishouAppInfoList.get(FlywheelConstant.INDEX);

                        OauthAccessTokenKsClient client = new OauthAccessTokenKsClient(kuaishouAppInfo.getAppId(), kuaishouAppInfo.getAppSecret());

                        KsAccessTokenResponse ksAccessTokenResponse = client.refreshAccessToken(kuaishouTokenInfo.getRefreshToken());

                        int result = ksAccessTokenResponse.getResult();

                        if (result != 1) {
                            log.error("result={},error={},error_msg ={}", ksAccessTokenResponse.getResult(), ksAccessTokenResponse.getError(), ksAccessTokenResponse.getErrorMsg());
                            log.error("刷新授权失败");
                            return "";
                        }

                        KuaishouTokenInfo tokenInfo = new KuaishouTokenInfo();
                        tokenInfo.setId(kuaishouTokenInfo.getId());
                        tokenInfo.setAccessToken(ksAccessTokenResponse.getAccessToken());
                        tokenInfo.setExpiresIn(ksAccessTokenResponse.getExpiresIn());
                        tokenInfo.setAccessTokenExpiresTime(System.currentTimeMillis() + (ksAccessTokenResponse.getExpiresIn() - 200L) * 1000L);

                        tokenInfo.setRefreshToken(ksAccessTokenResponse.getRefreshToken());
                        tokenInfo.setRefreshTokenExpiresIn(ksAccessTokenResponse.getRefreshTokenExpiresIn());
                        tokenInfo.setRefreshTokenExpiresTime(System.currentTimeMillis() + (ksAccessTokenResponse.getRefreshTokenExpiresIn() - 200L) * 1000L);

                        kuaishouTokenInfoService.updateById(tokenInfo);

                        return ksAccessTokenResponse.getAccessToken();
                    }
                    return kuaishouTokenInfo.getAccessToken();
                }
            });

    /**
     * 抖音平台获取AccessToken
     *
     * @param appIdAndOpenId
     * @return
     */
    public static String getAccessToken(String appIdAndOpenId) {
        return ACCESS_TOKEN_CACHE.getUnchecked(Objects.requireNonNull(appIdAndOpenId));
    }


//    static {
//        //【稀蜴抖音应用】 设置appKey和appSecret，全局设置一次
////        GlobalConfig.initAppKey("7049966719791531556");
////        GlobalConfig.initAppSecret("71e0c445-bec5-402b-96c1-2a3276cfc139");
//
//
//    }

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
