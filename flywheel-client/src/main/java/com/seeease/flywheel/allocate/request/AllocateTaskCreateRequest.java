package com.seeease.flywheel.allocate.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 调拨任务创建
 *
 * @author Tiro
 * @date 2023/8/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateTaskCreateRequest implements Serializable {

    /**
     * 调拨商品列表
     */
    private List<TaskDto> allocateStockList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskDto implements Serializable {

        /**
         * 调入方
         */
        private Integer toId;

        /**
         * 调入仓库
         */
        private Integer toStoreId;

        /**
         * 库存id
         */
        private Integer stockId;

        /**
         * 经营权
         */
        private Integer rightOfManagement;

        /**
         * 表身号
         */
        private String stockSn;

    }
}
