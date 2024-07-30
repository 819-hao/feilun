package com.seeease.flywheel.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 生命周期单个
 * @Date create in 2023/3/27 16:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseSingleResult implements Serializable {

    private StockLifeCycleResult stockLifeCycleResult;
}
