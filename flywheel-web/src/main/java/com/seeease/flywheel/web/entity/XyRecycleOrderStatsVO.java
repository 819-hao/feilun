package com.seeease.flywheel.web.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/10/24
 */
@Data
public class XyRecycleOrderStatsVO implements Serializable {
    private Integer quoteOrderState;
    private Integer countNumber;
}
