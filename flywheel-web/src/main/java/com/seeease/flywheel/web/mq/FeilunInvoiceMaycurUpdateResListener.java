package com.seeease.flywheel.web.mq;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.IFinancialInvoiceFacade;
import com.seeease.flywheel.financial.request.FinancialInvoiceMaycurRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * 监听每刻创建信息
 *
 * @Auther Gilbert
 * @Date 2023/8/7 13:45
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "feilunInvoiceMaycurUpdateRes", consumerGroup = "feilunInvoiceMaycurGroup_UpdateRes", selectorExpression = "*",
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY, nameServer = FlywheelConstant.MQ_NAME_SERVE)
public class FeilunInvoiceMaycurUpdateResListener implements RocketMQListener<JSONObject> {

    @DubboReference(check = false, version = "1.0.0")
    private IFinancialInvoiceFacade facade;

    @Override
    @Transactional
    public void onMessage(JSONObject message) {
        log.info("onMessage function of feilunInvoiceMaycurUpdateRes start and request = {}", message);
        String serialNo = message.getString("serialNo");
        Integer successFlag = message.getInteger("successFlag");
        String rejectReason = message.getString("rejectReason");
        String invoiceStatus = message.getString("invoiceStatus");
        String invoiceTitle = message.getString("invoiceTitle");
        String unitTaxNumber = message.getString("unitTaxNumber");
        Integer invoiceOrigin = message.getInteger("invoiceOrigin");
        Integer invoiceType = message.getInteger("invoiceType");

        //成功
        boolean flag = NumberUtils.INTEGER_ONE.equals(successFlag);
        if (!flag) {
            facade.maycurInvoice(FinancialInvoiceMaycurRequest.builder()
                    .serialNo(serialNo)
                    .rejectReason(rejectReason)
                    .invoiceStatus(invoiceStatus)
                    .invoiceTitle(invoiceTitle)
                    .unitTaxNumber(unitTaxNumber)
                    .invoiceOrigin(invoiceOrigin)
                    .invoiceType(invoiceType)
                    .useScenario(FinancialInvoiceMaycurRequest.UseScenario.UPDATE)
                    .build());

        }
    }
}
