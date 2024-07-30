package com.seeease.flywheel.sale.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
@Data
public class SaleOrderTmallSettleAccountsRequest implements Serializable {


    private Integer saleId;

    private String phone;

    private String userName;

    private Integer userId;

    private List<Integer> stockIdList;
}
