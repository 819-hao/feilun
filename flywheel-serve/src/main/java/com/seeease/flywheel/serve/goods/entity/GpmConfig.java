package com.seeease.flywheel.serve.goods.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 毛利率配置
 * */
@Data
public class GpmConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * C-toC;B-toB
     * */
    private String toTarget;

    /**
     * 开始时间
     * */
    private Date startDateTime;

    /**
     * 结束时间
     */
    private Date endDateTime;

    /**
     * 目标毛利率
     * */
    private BigDecimal gpmTarget;

}
