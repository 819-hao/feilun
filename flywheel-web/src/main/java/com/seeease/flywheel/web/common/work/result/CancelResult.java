package com.seeease.flywheel.web.common.work.result;

import com.seeease.flywheel.common.StockLifeCycleResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelResult implements Serializable {
    /**
     * 业务结果
     */
    private Object bizResult;
    /**
     * 业务key-订单号
     */
    private String businessKey;

    private List<StockLifeCycleResult> stockLifeCycleResultList;
}
