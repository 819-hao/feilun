package com.seeease.flywheel.web.controller.xianyu;

import com.seeease.flywheel.web.controller.xianyu.enums.XianYuMessageTopicEnum;
import com.seeease.flywheel.web.controller.xianyu.message.MessageListener;
import com.taobao.api.internal.tmc.Message;
import com.taobao.api.internal.tmc.MessageHandler;
import com.taobao.api.internal.tmc.MessageStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TOP商家处理消息同步
 *
 * @author Tiro
 * @date 2023/10/16
 */
@Slf4j
@Component
public class FlywheelMessageHandler implements MessageHandler {
    private final Map<XianYuMessageTopicEnum, MessageListener> listenerCache;

    public FlywheelMessageHandler(List<MessageListener> listenerList) {
        listenerCache = listenerList.stream()
                .filter(t -> Objects.nonNull(t.getTopic()))
                .collect(Collectors.toMap(MessageListener::getTopic, Function.identity()));
    }

    /**
     * 消息处理
     *
     * @param topic
     * @param content
     */
    private void handle(String topic, String content) {
        MessageListener listener = listenerCache.get(XianYuMessageTopicEnum.fromTopic(topic));
        if (Objects.isNull(listener)) {
            log.warn("【闲鱼消息】，未定义消息处理! {}", topic);
            return;
        }
        //消息处理
        listener.handle(content);
    }

    @Override
    public void onMessage(Message message, MessageStatus status) throws Exception {
        try {
            // 注意处理时间耗时, 收到消息后若阻塞分钟级将导致确认超时, 会重发消息, 根据处理tps设置分组流控!
            // 注意日志级别 或主动打点日志 避免收到消息而不能断定是否收到了消息
            log.info("【闲鱼消息】接收:{}-{}", message.getTopic(), message.getContent());
            // 测试用 不确认消息 等待重发
            // status.fail();
            this.handle(message.getTopic(), message.getContent());
        } catch (Exception e) {
            log.error("【闲鱼消息】处理异常:{}-{}", message.getContent(), e.getMessage(), e);
            status.fail(); // 消息处理失败回滚，服务端会延时约6分钟后重发
            // 重试注意：不是所有的异常都需要系统重试。
            // 对于字段不全、主键冲突问题，导致写DB异常，不可重试，否则消息会一直重发
            // 对于，由于网络问题，权限问题导致的失败，可重试。
            // 重试时间 6分钟不等，不要滥用，否则会引起雪崩
        }
    }
}
