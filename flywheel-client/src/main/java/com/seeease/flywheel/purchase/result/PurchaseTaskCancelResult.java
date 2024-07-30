package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/3/4 14:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseTaskCancelResult implements Serializable {

    /**
     * id
     */
    private Integer id;

    private String serialNo;
}
