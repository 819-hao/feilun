package com.seeease.flywheel.serve.recycle.mq;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.recycle.entity.RecycleMessage;
import com.seeease.flywheel.sale.entity.SaleOrderDeliveryMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 通知商城上传打款信息
 */
@Slf4j
@Component
public class RecycleOrderProducers {
    public static final String NAME_SERVER = "mq.local.com:9876";
    private static final String RECYCLE_ORDER_NOTICE_TOPIC = "recycleOrderNotice";
    public static final String MQ_SEND_SUCCEED = "mq发送消息成功,消息内容为%s";
    public static final String MQ_SEND_FAIL = "mq发送消息失败,消息内容为：%s,发送结果：%s";


    private DefaultMQProducer producer;

    @PostConstruct
    public void init() throws MQClientException {
        producer = new DefaultMQProducer(RECYCLE_ORDER_NOTICE_TOPIC);
        // 设置实例化名称
        producer.setInstanceName("RecycleOrderProducers");
        producer.setNamesrvAddr(NAME_SERVER);
        producer.start();
    }

    /**
     * @param recycleMessage
     */
    public void sendMsg(RecycleMessage recycleMessage) {

        byte[] body = JSONObject.toJSONBytes(recycleMessage);

        Message message = new Message(RECYCLE_ORDER_NOTICE_TOPIC, body);
        try {
            SendResult sendResult = producer.send(message, 3000);
            if (sendResult != null && sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                log.info(String.format(MQ_SEND_SUCCEED, JSONObject.parse(message.getBody())));
            } else {
                log.warn(String.format(MQ_SEND_FAIL, JSONObject.parse(message.getBody()), JSONObject.toJSONBytes(sendResult)));
            }
        } catch (Exception e) {
            log.error("mq_error{}:{}", JSONObject.parse(message.getBody()), e.getMessage(), e);
        }
    }

    @PreDestroy
    public void shutdown() {
        this.producer.shutdown();
    }

}
