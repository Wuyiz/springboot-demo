package com.example.wsmq.rabbitMq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class RabbitManage {
    @Resource
    private RabbitAdmin rabbitAdmin;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private SimpleMessageListenerContainer listenerContainer;

    public RabbitTemplate getRabbitTemplate() {
        return rabbitTemplate;
    }

    public void declareQueue(String exchangeName, String queueName, String routeKey) {
        if (!existQueue(queueName)) {
            // 声明一个持久化、非独占、自动删除的队列
            Queue queue = new Queue(queueName, true, false, true);
            Binding userBind = BindingBuilder.bind(queue).to(new TopicExchange(exchangeName)).with(routeKey);
            rabbitAdmin.declareQueue(queue);
            rabbitAdmin.declareBinding(userBind);
            // 动态监听队列
            listenerContainer.addQueues(queue);
        }
    }

    public void deleteQueue(String queueName) {
        if (existQueue(queueName)) {
            listenerContainer.removeQueueNames(queueName);
            rabbitAdmin.deleteQueue(queueName);
        }
    }

    public boolean existQueue(String queueName) {
        return rabbitAdmin.getQueueInfo(queueName) != null;
    }

}