package com.seeease.flywheel.serve.wx.rpc;

import com.seeease.flywheel.wx.IWxFacade;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/5/12 18:38
 */
@DubboService(version = "1.0.0")
public class WxFacade implements IWxFacade {

    @Resource
    private WxCpService wxCpService;

    @Override
    public String check(Integer agentId, String signature, String timestamp, String nonce, String echostr) {
        if (wxCpService == null) {
            throw new IllegalArgumentException(String.format("未找到对应agentId=[%d]的配置，请核实！", agentId));
        }

        if (wxCpService.checkSignature(signature, timestamp, nonce, echostr)) {
            return new WxCpCryptUtil(wxCpService.getWxCpConfigStorage()).decrypt(echostr);
        }
        return "非法请求";
    }

    @Override
    public String checkPost(Integer agentId, String requestBody, String signature, String timestamp, String nonce) {
        WxCpXmlMessage inMessage = WxCpXmlMessage.fromEncryptedXml(requestBody, wxCpService.getWxCpConfigStorage(),
                timestamp, nonce, signature);

        return "";
    }
}
