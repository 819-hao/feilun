package com.seeease.flywheel.express.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单发货通知
 *
 * @author Tiro
 * @date 2023/4/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeilunInvoiceMaycurQryMessage implements Serializable {

    /**
     * 飞轮开票号
     */
    private String serialNo;

}
