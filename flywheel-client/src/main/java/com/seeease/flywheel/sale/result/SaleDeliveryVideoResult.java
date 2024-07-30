package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/9/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleDeliveryVideoResult implements Serializable {
    /**
     * 存储主键
     */
    private Integer id;
}
