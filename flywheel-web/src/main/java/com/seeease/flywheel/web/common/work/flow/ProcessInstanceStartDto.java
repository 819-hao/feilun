package com.seeease.flywheel.web.common.work.flow;

import com.seeease.flywheel.web.common.work.consts.ProcessDefinitionKeyEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 流程开启参数对象
 *
 * @author Tiro
 * @date 2023/1/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceStartDto implements Serializable {
    /**
     * 流程定义key
     */
    private ProcessDefinitionKeyEnum process;
    /**
     * 单号
     */
    private String serialNo;
    /**
     * 流程参数
     */
    private Map<String, Object> variables;
}
