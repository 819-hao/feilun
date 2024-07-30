package com.seeease.flywheel;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求
 *
 * @author trio
 * @date 2023/1/16
 */
@Data
public abstract class PageRequest implements Serializable {
    /**
     * 页码
     */
    private int page = 1;
    /**
     * 分页数量
     */
    private int limit = 10;


}
