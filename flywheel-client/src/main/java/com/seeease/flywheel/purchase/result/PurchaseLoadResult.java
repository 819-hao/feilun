package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/9/7 15:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseLoadResult implements Serializable {

    private Boolean success;

    private String msg;
}
