package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.wx.IWxFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Mr. Du
 * @Description 微信鉴权api
 * @Date create in 2023/5/12 18:31
 */
@RestController
@RequestMapping("/wx/cp/portal/{agentId}")
@Slf4j
public class WxCpController {

    @DubboReference(check = false, version = "1.0.0")
    private IWxFacade wxFacade;

    /**
     * 鉴权api
     *
     * @param agentId
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@PathVariable Integer agentId,
                          @RequestParam(name = "msg_signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) {
        log.info("\n接收到来自微信服务器的认证消息：signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]",
                signature, timestamp, nonce, echostr);

        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }

        return wxFacade.check(agentId, signature, timestamp, nonce, echostr);
    }

    /**
     * 接收消息推送
     *
     * @param agentId
     * @param requestBody
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable Integer agentId,
                       @RequestBody String requestBody,
                       @RequestParam("msg_signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce) {
        log.info("\n接收微信请求：[signature=[{}], timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature, timestamp, nonce, requestBody);
        return wxFacade.checkPost(agentId, requestBody, signature, timestamp, nonce);
    }

    /**
     * 路由不使用
     */
//    private WxCpXmlOutMessage route(Integer agentId, WxCpXmlMessage message) {
//        try {
//            return WxCpConfiguration.getRouters().get(agentId).route(message);
//        } catch (Exception e) {
//            this.logger.error(e.getMessage(), e);
//        }
//
//        return null;
//    }
}
