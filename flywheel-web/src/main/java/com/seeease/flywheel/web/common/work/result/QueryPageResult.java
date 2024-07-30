package com.seeease.flywheel.web.common.work.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryPageResult implements QueryResult {
    /**
     * 总数量
     */
    private long totalCount;
    /**
     * 总页数
     */
    private long totalPage;

    /**
     * 结果
     */
    private List<QuerySingleResult> resultList;

}
