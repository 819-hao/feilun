package com.seeease.flywheel.web.mq;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.IFinancialInvoiceFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;


/**
 * 监听每刻创建信息
 *
 * @Auther Gilbert
 * @Date 2023/8/7 13:45
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "feilunInvoiceMaycurQryRes", consumerGroup = "feilunInvoiceMaycurGroup_QryRes", selectorExpression = "*",
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY, nameServer = FlywheelConstant.MQ_NAME_SERVE)
public class FeilunInvoiceMaycurQryResListener implements RocketMQListener<JSONObject> {

    @DubboReference(check = false, version = "1.0.0")
    private IFinancialInvoiceFacade facade;


    @Override
    public void onMessage(JSONObject message) {
        log.info("onMessage function of feilunInvoiceMaycurQryRes start and request = {}", message);
        String serialNo = message.getString("serialNo");
        String invoiceStatus = message.getString("invoiceStatus");
        String pdfUrl = message.getString("pdfUrl");

    }
}
