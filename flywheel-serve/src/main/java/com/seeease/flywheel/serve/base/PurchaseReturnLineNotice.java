package com.seeease.flywheel.serve.base;

import com.seeease.flywheel.serve.purchase.enums.PurchaseReturnLineStateEnum;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/13 14:32
 */
@Data
@Builder
public class PurchaseReturnLineNotice implements Serializable {

    private String serialNo;

    private Integer purchaseReturnId;

    private PurchaseReturnLineStateEnum lineState;

    private Integer stockId;

    private String expressNumber;
}
