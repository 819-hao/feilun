package com.seeease.flywheel.web.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author trio
 * @date 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageData<T> implements Serializable {

    private List<T> result;

    private Long totalCount;
    private Long totalPage;

}
