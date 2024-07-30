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
public class QueryListResult implements QueryResult {
    /**
     * 结果
     */
    private List<QuerySingleResult> resultList;
}
