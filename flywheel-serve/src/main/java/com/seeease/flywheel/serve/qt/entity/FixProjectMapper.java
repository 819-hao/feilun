package com.seeease.flywheel.serve.qt.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description 维修项目
 * @Date create in 2023/2/1 17:25
 */
@Data
public class FixProjectMapper implements Serializable {

    private Integer fixProjectId;

    private BigDecimal fixMoney;

}
