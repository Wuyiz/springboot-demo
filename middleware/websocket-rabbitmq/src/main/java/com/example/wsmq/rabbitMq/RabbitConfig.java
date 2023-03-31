package com.example.wsmq.rabbitMq;

import cn.hutool.core.util.StrUtil;
import com.example.wsmq.websocket.WebSocketTest;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Configuration
public class RabbitConfig {
    @Resource
    private LabelDataMessageListener labelDataMessageListener;

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());

        // 设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        // rabbitTemplate.setMandatory(true);
        //
        // rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
        //     log.debug("ConfirmCallback[相关数据]：{}", correlationData);
        //     log.debug("ConfirmCallback[确认情况]：{}", ack);
        //     log.debug("ConfirmCallback[原因]：{}", cause);
        // });
        //
        // rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
        //     log.debug("ReturnCallback[消息]：{}", message);
        //     log.debug("ReturnCallback[回应码]：{}", replyCode);
        //     log.debug("ReturnCallback[回应信息]：{}", replyText);
        //     log.debug("ReturnCallback[交换机]：{}", exchange);
        //     log.debug("ReturnCallback[路由键]：{}", routingKey);
        // });

        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(final ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleMessageListenerContainer labelDataListenerContainer(final ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.NONE);
        // container.setDefaultRequeueRejected(false);
        container.setMessageListener(labelDataMessageListener);
        return container;
    }

    /**
     * 基于ar视频标签队列的消息监听器
     */
    @Slf4j
    @Component
    public static
    class LabelDataMessageListener implements ChannelAwareMessageListener {

        @Override
        public void onMessage(Message message, Channel channel) {
            String consumerQueue = message.getMessageProperties().getConsumerQueue();
            log.debug("队列{}监听消费：{}", consumerQueue, message);
            // 转换数据为string
            String msgStr = new String(message.getBody(), StandardCharsets.UTF_8);
            // 从队列名中获取对应的webSocket连接用户
            String userId = StrUtil.subAfter(consumerQueue, StrUtil.DOT, true);
            CopyOnWriteArraySet<WebSocketTest> userSocketSet = WebSocketTest.getUserSocketSet(userId);
            // 推送消息
            for (WebSocketTest socket : userSocketSet) {
                try {
                    socket.sendMessage(msgStr);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}