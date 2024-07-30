package com.seeease.flywheel.web.infrastructure.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.entity.DouYinShopMapping;
import com.seeease.flywheel.web.entity.request.DouYinDecryptRequest;
import com.seeease.flywheel.web.entity.result.DouYinDecryptResult;
import com.seeease.flywheel.web.infrastructure.service.DouYinDecryptService;
import com.seeease.flywheel.web.infrastructure.service.DouYinShopMappingService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Tiro
 * @date 2023/9/14
 */
@Slf4j
@Service
public class DouYinDecryptServiceImpl implements DouYinDecryptService {

    private static final int HIDDEN_DECRYPT_MAX_COUNT = 10;

    @Resource
    private DouYinShopMappingService douYinShopMappingService;

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
    public Map<String, String> orderDecrypt(Integer shopId, Long douYinShopId, String douYinOrderId, List<String> cipherText) {
        if (CollectionUtils.isEmpty(cipherText)
                || cipherText.stream().filter(StringUtils::isNotBlank).count() == 0) {
            return Collections.emptyMap();
        }
        try {
            DouYinShopMapping shopMapping = douYinShopMappingService.list(Wrappers.<DouYinShopMapping>lambdaQuery()
                            .eq(DouYinShopMapping::getShopId, shopId)
                            .eq(DouYinShopMapping::getDouYinShopId, douYinShopId))
                    .stream()
                    .findFirst()
                    .orElse(null);

            //需要解密
            boolean need = (Objects.nonNull(shopMapping)
                    && WhetherEnum.YES.getValue().intValue() == shopMapping.getNeedDecrypt())
                    || douYinShopMappingService.countDecryptNumberByToDays(douYinShopId) < HIDDEN_DECRYPT_MAX_COUNT; // 当天订单已经解密的数量少于上限

            if (need) {
                return this.orderDecrypt(DouYinDecryptRequest.builder()
                                .shopId(douYinShopId)
                                .orderId(douYinOrderId)
                                .cipherText(cipherText)
                                .build())
                        .getDecryptText();
            }
        } catch (Exception e) {
            log.error("抖音订单解密异常:{}{}", douYinOrderId, e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    /**
     * @param request
     * @return
     */
    private DouYinDecryptResult orderDecrypt(DouYinDecryptRequest request) {
        try {
            String msg = JSONObject.toJSONString(request);
            RequestBody body = RequestBody.create(msg, MediaType.get("application/json"));
            Request r = new Request.Builder()
                    .url("http://101.126.45.195:8866/decrypt")
                    .post(body)
                    .build();
            Response response = new OkHttpClient().newCall(r).execute();
            DouYinDecryptResult result = JSONObject.parseObject(response.body().string(), DouYinDecryptResult.class);
            if (!result.getCode().equals("200")) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.DECRYPTION_FAILED, result.getMsg());
            }
            log.info("抖音订单解密成功:{}-{}", msg, JSONObject.toJSON(result));
            return result;
        } catch (OperationRejectedException ore) {
            throw ore;
        } catch (Exception e) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.DECRYPTION_FAILED, e.getMessage());
        }
    }


}
