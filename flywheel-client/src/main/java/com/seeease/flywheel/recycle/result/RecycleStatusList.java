package com.seeease.flywheel.recycle.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Auther Gilbert
 * @Date 2023/9/5 17:00
 */
@Data
@Accessors(chain = true)
public class RecycleStatusList implements Serializable {
    /**
     * 回购状态列表
     */
    private List<StatusVO> buyBackStatusList;
    /**
     * 回收状态
     */
    private List<StatusVO> recycleStatusList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatusVO implements Serializable {
        /**
         * 值
         */
        private Integer value;

        private String remark;

        /**
         * 描述
         */
        private String desc;
    }
}
