package com.seeease.flywheel.web.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/9/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DouYinDecryptRequest implements Serializable {
    /**
     * 抖音门店短id
     */
    private Long shopId;
    /**
     * 抖音订单id
     */
    private String orderId;
    /**
     * 密文字符串列表
     */
    List<String> cipherText;
}
