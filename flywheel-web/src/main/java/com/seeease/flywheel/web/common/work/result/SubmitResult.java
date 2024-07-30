package com.seeease.flywheel.web.common.work.result;

import com.seeease.flywheel.common.StockLifeCycleResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitResult implements Serializable {
    /**
     * 业务结果
     */
    private Object bizResult;

    /**
     * 工作流新增参数
     */
    private Map<String, Object> workflowVar;

    private List<StockLifeCycleResult> stockLifeCycleResultList;
}
