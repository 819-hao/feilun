package com.seeease.flywheel.serve.sale.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillSaleOrderDTO implements Serializable {
    private BillSaleOrder order;
    private List<BillSaleOrderLine> lines;
}
