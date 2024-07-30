package com.seeease.flywheel.web.controller.xianyu.request;

import lombok.Data;

/**
 * @author Tiro
 * @date 2023/10/17
 */
@Data
public class RecycleOrderPrepayCheckRequest extends QiMenBaseRequest {
    /**
     * 芝麻分
     */
    private String zhimaCode;
    /**
     * true：安全的 false：不安全
     */
    private Boolean zhimaRisk;
    /**
     * 报价id
     */
    private String apprizeId;
    /**
     * 卖家支付宝id
     */
    private String sellerAlipayUserId;
    /**
     * 600以下为Z5，600-649为Z4，650-699为Z3，700-749为Z2，750及以上为Z1
     */
    private String zhimaLevel;
    /**
     * 交付方式 1:快递邮寄 2:上门质检 3:到店
     */
    private String shipType;
    /**
     * 渠道，与估价id对应的相同，天猫以旧换新场景可能会要求不同的预付额度
     */
    private String channel;
    /**
     * 估价版本，第几次估价
     */
    private String quoteVersion;
}
