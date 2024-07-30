package com.seeease.flywheel.recycle.result;

import com.seeease.flywheel.purchase.request.PurchaseLoadRequest;
import com.seeease.flywheel.sale.request.SaleLoadRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 商城回购一次确认和商城回收二次确认返回参数
 * @Date create in 2023/9/7 16:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessVerifyResult implements Serializable {

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
