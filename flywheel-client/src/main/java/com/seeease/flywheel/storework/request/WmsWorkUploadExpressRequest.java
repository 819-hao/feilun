package com.seeease.flywheel.storework.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsWorkUploadExpressRequest implements Serializable {
    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 发货物流单号
     */
    private String deliveryExpressNumber;

    /**
     * 国检到用户到物流单号
     */
    private String gjToUserExpressNumber;

    /**
     * 是否系统打单
     */
    private Boolean isSystemPrint;
}
