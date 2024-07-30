package com.seeease.flywheel.serve.recycle.mq;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.recycle.entity.RecycleMessage;
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
 * 销售物流发货的mq通知
 */
@Slf4j
@Component
public class MallLogisticsDeliveryProducers {
    public static final String NAME_SERVER = "mq.local.com:9876";
    private static final String MALL_LOGISTICS_DELIVERY_NOTICE_TOPIC = "mallLogisticsDeliveryNotice";
    public static final String MQ_SEND_SUCCEED = "mq发送消息成功,消息内容为%s";
    public static final String MQ_SEND_FAIL = "mq发送消息失败,消息内容为：%s,发送结果：%s";


    private DefaultMQProducer producer;

    @PostConstruct
    public void init() throws MQClientException {
        producer = new DefaultMQProducer(MALL_LOGISTICS_DELIVERY_NOTICE_TOPIC);
        // 设置实例化名称
        producer.setInstanceName("MallLogisticsDeliveryProducers");
        producer.setNamesrvAddr(NAME_SERVER);
        producer.start();
    }

    /**
     * @param recycleMessage
     */
    public void sendMsg(RecycleMessage recycleMessage) {

        byte[] body = JSONObject.toJSONBytes(recycleMessage);

        Message message = new Message(MALL_LOGISTICS_DELIVERY_NOTICE_TOPIC, body);
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
