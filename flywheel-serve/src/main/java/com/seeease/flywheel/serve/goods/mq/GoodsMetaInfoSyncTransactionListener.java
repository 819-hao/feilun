package com.seeease.flywheel.serve.goods.mq;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.goods.entity.GoodsMessage;
import com.seeease.flywheel.serve.goods.service.GoodsMetaInfoSyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tiro
 * @date 2023/2/21
 */
@Slf4j
@Component
public class GoodsMetaInfoSyncTransactionListener implements TransactionListener {

    @Resource
    private GoodsMetaInfoSyncService goodsMetaInfoSyncService;

    private ConcurrentHashMap<String, LocalTransactionState> LOCAL_TRANS = new ConcurrentHashMap<>();

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String msgKey = msg.getKeys();
        GoodsMessage goodsMessage = JSONObject.parseObject(msg.getBody(), GoodsMessage.class);
        LocalTransactionState state;
        try {
            if (1 != goodsMetaInfoSyncService.updatePropertyChange(goodsMessage.getStockId(), goodsMessage.getMessageCreateTime())) {
                throw new RuntimeException("MQ消息发送mysql事务失败");
            }
            state = LocalTransactionState.COMMIT_MESSAGE;
        } catch (Exception e) {
            state = LocalTransactionState.ROLLBACK_MESSAGE;
            log.error("MQ消息事务异常:{}", e.getMessage(), e);
        }
        LOCAL_TRANS.put(msgKey, state);
        return state;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        String msgKey = msg.getKeys();
        //如果查不到默认事务成功，避免重启服务丢失缓存在
        return Optional.ofNullable(LOCAL_TRANS.get(msgKey))
                .orElse(LocalTransactionState.COMMIT_MESSAGE);
    }
}