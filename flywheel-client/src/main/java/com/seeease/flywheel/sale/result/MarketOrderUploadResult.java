package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/4/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketOrderUploadResult implements Serializable {
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 提示消息
     */
    private String msg;
}
