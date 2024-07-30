package com.seeease.flywheel.serve.goods.mq;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.goods.entity.GoodsMessage;
import com.seeease.flywheel.serve.goods.entity.GoodsMetaInfoSync;
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
import java.util.Date;

/**
 * @author Tiro
 * @date 2023/2/21
 */
@Slf4j
@Component
public class GoodsMetaInfoSyncProducer {
    public static final String NAME_SERVER = "mq.local.com:9876";
    private static final String GOODS_META_INFO_SYNC_NOTICE_TOPIC = "goodsMetaInfoSyncNotice";
    public static final String MQ_SEND_SUCCEED = "mq发送消息成功,消息内容为%s";
    public static final String MQ_SEND_FAIL = "mq发送消息失败,消息内容为：%s,发送结果：%s";

    private TransactionMQProducer producer;

    @Resource
    private GoodsMetaInfoSyncTransactionListener goodsMetaInfoSyncTransactionListener;


    @PostConstruct
    public void init() throws MQClientException {
        producer = new TransactionMQProducer(GOODS_META_INFO_SYNC_NOTICE_TOPIC);
        // 设置实例化名称
        producer.setInstanceName("GoodsMetaInfoSyncProducer");
        // 设置事务监听器
        producer.setTransactionListener(goodsMetaInfoSyncTransactionListener);
        // 设置服务器地址
        producer.setNamesrvAddr(NAME_SERVER);
        // 启动实例
        producer.start();
    }


    /**
     * @param infoSync
     */
    public void sendMsg(GoodsMetaInfoSync infoSync) {
        GoodsMessage goodsMessage = new GoodsMessage();
        goodsMessage.setStockId(infoSync.getStockId());
        goodsMessage.setGoodsId(infoSync.getGoodsId());
        goodsMessage.setMessageCreateTime(new Date());
        byte[] body = JSONObject.toJSONBytes(goodsMessage);
        String keys = String.valueOf(System.currentTimeMillis()) + infoSync.getStockId();
        Message message = new Message(GOODS_META_INFO_SYNC_NOTICE_TOPIC, "", keys, body);
        try {
            SendResult sendResult = producer.sendMessageInTransaction(message, infoSync.getStockId());
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
