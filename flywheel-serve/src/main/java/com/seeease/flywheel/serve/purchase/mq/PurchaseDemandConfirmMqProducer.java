package com.seeease.flywheel.serve.purchase.mq;

import com.alibaba.fastjson.JSONObject;

import com.seeease.flywheel.purchase.dto.PurchaseDemandConfirmMqPushDto;
import com.seeease.flywheel.serve.goods.mq.GoodsMetaInfoSyncTransactionListener;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseDemand;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;



@Slf4j
@Component
public class PurchaseDemandConfirmMqProducer {
    public static final String NAME_SERVER = "mq.local.com:9876";
    private static final String GOODS_META_INFO_SYNC_NOTICE_TOPIC = "purchaseDemandConfirmMq";
    public static final String MQ_SEND_SUCCEED = "mq发送消息成功,消息内容为%s";
    public static final String MQ_SEND_FAIL = "mq发送消息失败,消息内容为：%s,发送结果：%s";

    private TransactionMQProducer producer;

    @Resource
    private GoodsMetaInfoSyncTransactionListener goodsMetaInfoSyncTransactionListener;


    @PostConstruct
    public void init() throws MQClientException {
        producer = new TransactionMQProducer(GOODS_META_INFO_SYNC_NOTICE_TOPIC);
        // 设置实例化名称
        producer.setInstanceName("purchaseDemandConfirmProducer");
        // 设置服务器地址
        producer.setNamesrvAddr(NAME_SERVER);
        // 启动实例
        producer.start();
    }


    /**
     * @param infoSync
     */
    public void sendMsg(PurchaseDemandConfirmMqPushDto dto) {

        byte[] body = JSONObject.toJSONBytes(dto);
        Message message = new Message(GOODS_META_INFO_SYNC_NOTICE_TOPIC, body);
        try {
            SendResult sendResult = producer.send(message);
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
