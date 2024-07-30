package com.seeease.flywheel.web.infrastructure.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kuaishou.merchant.open.api.client.AccessTokenKsMerchantClient;
import com.kuaishou.merchant.open.api.domain.order.DecryptBaseMetaInfo;
import com.kuaishou.merchant.open.api.domain.order.DecryptResultMetaInfo;
import com.kuaishou.merchant.open.api.request.order.OpenOrderDecryptBatchRequest;
import com.kuaishou.merchant.open.api.response.order.OpenOrderDecryptBatchResponse;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.web.common.context.KuaiShouConfig;
import com.seeease.flywheel.web.entity.KuaishouAppInfo;
import com.seeease.flywheel.web.entity.KuaishouShopMapping;
import com.seeease.flywheel.web.infrastructure.service.KuaiShouDecryptService;
import com.seeease.flywheel.web.infrastructure.service.KuaishouAppInfoService;
import com.seeease.flywheel.web.infrastructure.service.KuaishouShopMappingService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/9/14
 */
@Slf4j
@Service
public class KuaiShouDecryptServiceImpl implements KuaiShouDecryptService {

    private static final int HIDDEN_DECRYPT_MAX_COUNT = 10;

    @Resource
    private KuaishouShopMappingService kuaishouShopMappingService;

    @Resource
    private KuaishouAppInfoService kuaishouAppInfoService;

    /**
     * 解密
     *
     * @param shopId
     * @param douYinShopId
     * @param douYinOrderId
     * @param cipherText
     * @return
     */
    @Override
    public Map<String, String> orderDecrypt(Integer shopId, Long douYinShopId, Long douYinOrderId, List<String> cipherText, String appId) {
        if (CollectionUtils.isEmpty(cipherText)
                || cipherText.stream().filter(StringUtils::isNotBlank).count() == 0) {
            return Collections.emptyMap();
        }
        try {

            //快手门店映射
            KuaishouShopMapping shopMapping = kuaishouShopMappingService.list(Wrappers.<KuaishouShopMapping>lambdaQuery()
                    .eq(KuaishouShopMapping::getShopId, shopId)
                    .eq(KuaishouShopMapping::getKuaiShouShopId, douYinShopId))
                    .stream()
                    .findFirst()
                    .orElse(null);

            List<KuaishouAppInfo> kuaishouAppInfoList = kuaishouAppInfoService.list(Wrappers.<KuaishouAppInfo>lambdaQuery().eq(KuaishouAppInfo::getAppId, appId));

            //需要解密
            boolean need = (Objects.nonNull(shopMapping)
                    && CollectionUtils.isNotEmpty(kuaishouAppInfoList)
                    && WhetherEnum.YES.getValue().intValue() == shopMapping.getNeedDecrypt())
                    //todo
                    || kuaishouShopMappingService.countDecryptNumberByToDays(douYinShopId) < HIDDEN_DECRYPT_MAX_COUNT; // 当天订单已经解密的数量少于上限

            if (need) {

                KuaishouAppInfo kuaishouAppInfo = kuaishouAppInfoList.get(FlywheelConstant.INDEX);

                AccessTokenKsMerchantClient client = new AccessTokenKsMerchantClient(kuaishouAppInfo.getAppId(), kuaishouAppInfo.getSignSecret());

                String accessToken = KuaiShouConfig.getAccessToken(StringUtils.join(Arrays.asList(appId, "f1909b5b7c661d5314bf572441e510f3"), ":"));

                OpenOrderDecryptBatchRequest request = new OpenOrderDecryptBatchRequest();
                request.setAccessToken(accessToken);
                request.setApiMethodVersion(1L);

                request.setBatchDecryptList(cipherText.stream().map(t -> {
                    DecryptBaseMetaInfo decryptBaseMetaInfo = new DecryptBaseMetaInfo();
                    decryptBaseMetaInfo.setEncryptedData(t);
                    decryptBaseMetaInfo.setBizId(String.valueOf(douYinOrderId));
                    return decryptBaseMetaInfo;
                }).collect(Collectors.toList()));

                OpenOrderDecryptBatchResponse response = client.execute(request);

                log.info("快手订单解密信息{}", JSONObject.toJSONString(response));

                if (response.getResult() != 1) {
                    log.error("解密失败");
                    return Collections.emptyMap();
                }

                Map<String, String> map = new HashMap<>();

                for (DecryptResultMetaInfo decryptResultMetaInfo : response.getBatchDecryptResultList()) {

                    if (!decryptResultMetaInfo.getErrorCode().equals(1)) {
                        log.error("解密失败,errorCode={},errorMsg={}", decryptResultMetaInfo.getErrorCode(), decryptResultMetaInfo.getErrorMsg());
                        break;
                    }
                    map.put(decryptResultMetaInfo.getEncryptedData(), decryptResultMetaInfo.getDecryptedData());
                }
                return map;
            }
        } catch (Exception e) {
            log.error("快手订单解密异常:{}{}", douYinOrderId, e.getMessage(), e);
        }
        return Collections.emptyMap();
    }
}
