package com.seeease.flywheel.storework.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsWorkInterceptResult implements Serializable {
    /**
     * 源头单据单号
     */
    private String originSerialNo;
}
