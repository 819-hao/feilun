package com.seeease.flywheel.recycle.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 协议信息
 * @Auther Gilbert
 * @Date 2023/9/1 10:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MallAgreement implements Serializable {

    private String id;

    /**
     * 协议名称
     */
    private String name;

    /**
     * 服务内容文本框
     */
    private String content;

    /**
     * 公司
     */
    private String company;

    /**
     * 协议版本
     */
    private Integer version;

    /**
     * 是否启用 0否1是
     */
    private Boolean isEnable;

    /**
     * 过期天数
     */
    private Integer expireDays;
}
