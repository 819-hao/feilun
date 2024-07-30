package com.seeease.flywheel.goods.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/10/10 10:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectInsertPurchaseLineRequest implements Serializable {

    private Integer id;

    private Integer purchaseId;

    private String wno;
}
