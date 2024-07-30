package com.seeease.flywheel.web.common.work.cmd;

import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;


/**
 * @author Tiro
 * @date 2023/1/13
 */
@Data
public class SubmitCmd<T> extends BaseCmd<T> {

    /**
     * 待提交完成的用户任务列表
     */
    private List<UserTaskDto> taskList;

    /**
     * 有效的任务列表
     *
     * @return
     */
    public boolean taskListEffective() {
        return CollectionUtils.isNotEmpty(taskList) && taskList.stream().allMatch(t -> Objects.nonNull(t) && t.effective());
    }
}
