package com.seeease.flywheel.wx;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/5/12 18:33
 */

public interface IWxFacade {

    /**
     * 鉴权
     * @param agentId
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    String check(Integer agentId, String signature, String timestamp, String nonce, String echostr);

    /**
     * 消息推送
     * @param agentId
     * @param requestBody
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    String checkPost(Integer agentId, String requestBody, String signature, String timestamp, String nonce);
}
