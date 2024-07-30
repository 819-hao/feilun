package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/19 14:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseByNameResult implements Serializable {

    private Integer stockId;

    private String purchaseName;

}
