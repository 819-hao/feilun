package com.seeease.flywheel.serve.sale.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wbh
 * @date 2023/3/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillSaleReturnOrderLineDto implements Serializable {

    private List<Integer> stockIdList;

    private Boolean whetherChangeOrderState;

    private String deliveryExpressNumber;

    private String serialNo;

    private Integer saleReturnId;
}
