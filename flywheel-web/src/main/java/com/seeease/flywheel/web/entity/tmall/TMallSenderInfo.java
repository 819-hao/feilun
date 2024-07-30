package com.seeease.flywheel.web.entity.tmall;

import lombok.Data;

/**
 * 天猫发件信息
 *
 * @author Tiro
 * @date 2023/3/24
 */
@Data
public class TMallSenderInfo {
    /**
     * 发件方名称
     */
    private String senderName;
    /**
     * 发件方国家
     */
    private String senderCountry;
    /**
     * 发件方省
     */
    private String senderProvince;

    /**
     * 发件方市
     */
    private String senderCity;
    /**
     * 发件方区
     */
    private String senderArea;
}
