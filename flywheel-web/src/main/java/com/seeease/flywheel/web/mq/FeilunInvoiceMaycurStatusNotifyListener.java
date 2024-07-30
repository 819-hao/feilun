package com.seeease.flywheel.web.mq;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.IFinancialInvoiceFacade;
import com.seeease.flywheel.financial.request.FinancialInvoiceMaycurRequest;
import com.seeease.flywheel.financial.result.FinancialInvoiceMaycurResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.FinancialInvoiceNotice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 监听每刻创建信息
 *
 * @Auther Gilbert
 * @Date 2023/8/7 13:45
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "feilunInvoiceMaycurStatusNotify", consumerGroup = "feilunInvoiceMaycurGroup_StatusNotify", selectorExpression = "*",
        messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY, nameServer = FlywheelConstant.MQ_NAME_SERVE)
public class FeilunInvoiceMaycurStatusNotifyListener implements RocketMQListener<JSONObject> {

    @DubboReference(check = false, version = "1.0.0")
    private IFinancialInvoiceFacade facade;
    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @Override
    public void onMessage(JSONObject message) {
        log.info("onMessage function of feilunInvoiceMaycurStatusNotify start and request = {}", message);
        String serialNo = message.getString("serialNo");
        String invoiceStatus = message.getString("invoiceStatus");
        String rejectReason = message.getString("rejectReason");
        Integer successFlag = message.getInteger("successFlag");
        String pdfUrl = message.getString("pdfUrl");
        String invoiceNumber = message.getString("invoiceNumber");
        Date openTicketTime = message.getDate("openTicketTime");

        //成功
        boolean flag = NumberUtils.INTEGER_ONE.equals(successFlag);
        if (!flag) {
            facade.maycurInvoice(FinancialInvoiceMaycurRequest.builder()
                    .serialNo(serialNo)
                    .rejectReason(rejectReason)
                    .invoiceStatus(invoiceStatus)
                    .useScenario(FinancialInvoiceMaycurRequest.UseScenario.STATUS_NOTIFY_FAIL)
                    .build());
        } else {
            FinancialInvoiceMaycurResult result = facade.maycurInvoice(FinancialInvoiceMaycurRequest.builder()
                    .serialNo(serialNo)
                    .rejectReason(rejectReason)
                    .invoiceStatus(invoiceStatus)
                    .pdfUrl(pdfUrl)
                    .openTicketTime(openTicketTime)
                    .invoiceNumber(invoiceNumber)
                    .useScenario(FinancialInvoiceMaycurRequest.UseScenario.STATUS_NOTIFY_SUCCEED)
                    .build());
            FinancialInvoiceNotice notice = new FinancialInvoiceNotice();
            notice.setId(result.getId());
            notice.setSerialNo(result.getSerialNo());
            notice.setCreatedBy(result.getCreatedBy());
            notice.setCreatedTime(result.getCreatedTime());
            notice.setState(FlywheelConstant.COMPLETE_INVOICE);
            notice.setShopId(result.getShopId());
            notice.setToUserIdList(Lists.newArrayList(result.getCreatedId()));
            wxCpMessageFacade.send(notice);
            log.info("发送通知消息 {}", notice);
        }
    }
}
