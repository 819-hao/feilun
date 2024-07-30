package com.seeease.flywheel.purchase.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/19 14:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseByNameRequest {

    private List<Integer> stockIdList;
}
