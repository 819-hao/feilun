package com.seeease.flywheel.web.common.work.flow;

import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tiro
 * @date 2023/1/19
 */
@Getter
@AllArgsConstructor
public enum WorkflowStateEnum implements IStateEnum<Integer> {
    ERROR(-1, "异常"),
    INIT(0, "初始状态"),
    COMPLETE(1, "已完成"),
    CANCEL(2, "已取消"),
    ;

    private Integer value;
    private String desc;
}
