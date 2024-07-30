package com.seeease.flywheel.web.controller.xianyu.request;

import lombok.Data;

/**
 * @author Tiro
 * @date 2023/10/13
 */
@Data
public class RecycleQuoteGetRequest extends QiMenBaseRequest {
    /**
     * 用户id，必填
     */
    private String userId;
    /**
     * idle渠道，必填
     */
    private String channel;
    /**
     * 问卷 spuId，必填
     */
    private String spuId;
    /**
     * 投放业务，必填
     */
    private String bizType;
    /**
     * 问卷版本，必填
     */
    private String version;
    /**
     * 问卷内容，json字符串数据，必填
     */
    private String questionnaire;
    /**
     * 估价id
     */
    private String quoteId;
}
