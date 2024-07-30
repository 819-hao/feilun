package com.seeease.flywheel.web.infrastructure.service;

import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/9/14
 */
public interface KuaiShouDecryptService {
    /**
     * 解密抖音订单
     *
     * @param shopId
     * @param douYinShopId
     * @param douYinOrderId
     * @param cipherText
     * @param appId
     * @return
     */
    Map<String, String> orderDecrypt(Integer shopId, Long douYinShopId, Long douYinOrderId, List<String> cipherText, String appId);

}
