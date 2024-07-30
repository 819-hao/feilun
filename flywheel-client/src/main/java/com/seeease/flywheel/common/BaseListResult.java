package com.seeease.flywheel.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 生命周期列表
 * @Date create in 2023/3/27 16:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseListResult implements Serializable {

    private List<StockLifeCycleResult> stockLifeCycleResultList;
}
