package com.seeease.flywheel.web.common.work.result;

import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateResult implements Serializable {
    /**
     * 业务结果
     */
    private Object bizResult;
    /**
     * 工作流启动参数
     */
    private List<ProcessInstanceStartDto> instanceStart;

    private List<StockLifeCycleResult> stockLifeCycleResultList;
}
