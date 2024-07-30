package com.seeease.flywheel.web.infrastructure.service;

import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/9/14
 */
public interface DouYinDecryptService {
    /**
     * 解密抖音订单
     *
     * @param shopId 门店id
     * @param douYinShopId 快手id
     * @param douYinOrderId 订单id
     * @param cipherText 解密的字段
     * @return
     */
    Map<String, String> orderDecrypt(Integer shopId, Long douYinShopId, String douYinOrderId, List<String> cipherText);

}
