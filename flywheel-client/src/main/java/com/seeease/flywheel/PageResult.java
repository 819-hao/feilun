package com.seeease.flywheel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 *
 * @author trio
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    /**
     * 结果
     */
    private List<T> result;
    /**
     * 总数量
     */
    private long totalCount;
    /**
     * 总页数
     */
    private long totalPage;

    private Object ext;
}
