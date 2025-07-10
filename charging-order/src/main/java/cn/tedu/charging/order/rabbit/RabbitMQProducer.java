package cn.tedu.charging.order.rabbit;

import cn.tedu.charging.order.config.RabbitMQConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ 消费发送的工具类
 * 业务用来发送消息
 * @Author: goodman
 * @Date: 2025/2/27 20:02
 */
@Component
public class RabbitMQProducer {


    /**
     * Spring封装好的用来发送消息的工具类
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到RabbitMQ
     * @param msg
     * @return
     */
    public Boolean send(String msg){
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.exchangeName,RabbitMQConfiguration.routingKey,msg);
        return true;
    }

}
