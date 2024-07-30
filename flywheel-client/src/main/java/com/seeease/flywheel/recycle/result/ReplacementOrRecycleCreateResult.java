package com.seeease.flywheel.recycle.result;

import com.seeease.flywheel.purchase.request.PurchaseLoadRequest;
import com.seeease.flywheel.sale.request.SaleLoadRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/9/9 13:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
public class ReplacementOrRecycleCreateResult implements Serializable {

    //主键id
    private Integer id;
    /**
     * 单号
     */
    private String serialNo;

    /**
     * todo
     */
    /**
     * 工作流开启参数 建单
     */
    private ProcessDTO process;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessDTO implements Serializable {

        /**
         * 采购开启流程对象
         */
        private PurchaseLoadRequest purchaseLoadRequest;

        /**
         * 销售开启流程对象
         */
        private SaleLoadRequest saleLoadRequest;
    }
}
