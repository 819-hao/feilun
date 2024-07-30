package com.seeease.flywheel.fix.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 维修甘特图
 * @Date create in 2023/11/18 15:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixGanttChartResult implements Serializable {


    private Integer maintenanceMasterId;

    /**
     * 维修师
     */
    private String maintenanceMasterName;

    /**
     * 总任务数量
     */
    private Integer currentTask;

    /**
     * 当前到期数量
     */
    private Integer todayTask;

    /**
     * 紧急数量
     */
    private Integer specialTask;

    /**
     * 维修任务
     */
    private List<StockTaskMapper> stockTaskList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockTaskMapper implements Serializable {

        /**
         * 品牌
         */
        private String brandName;

        /**
         * 表身号
         */
        private String stockSn;

        /**
         * 所占空格
         */
        private List<String> fixDays;

        /**
         * 维修项目名称 字符串
         */
        private String projectContent;

        /**
         * 是否加急
         */
        private Boolean special = Boolean.FALSE;

        /**
         * 是否当前
         */
        private Boolean today = Boolean.FALSE;

        private Integer id;

        private String serialNo;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class FixProjectMapper implements Serializable {

            private Integer fixProjectId;

            private BigDecimal fixMoney;
        }
    }
}
