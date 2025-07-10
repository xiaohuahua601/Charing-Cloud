package cn.tedu.charging.order.config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.impl.AMQImpl;
import io.swagger.models.auth.In;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * RabbitMQ 消息中间件的配置类
 * @Author: goodman
 * @Date: 2025/2/27 19:46
 */
@Configuration
public class RabbitMQConfiguration {

    /**
     *  queue 名称
     */
    public static final String queueName = "charging-order-queue";
    /**
     *  exchange 名称
     */
    public static final String exchangeName = "charging-order-exchange";
    /**
     *  routingKey 名称 用来绑定 queue和exchange
     */
    public static final String routingKey = "charging-order-routingKey";


    /**
     *  queue 名称
     */
    public static final String DEAD_LETTER_QUEUE_NAME = "dead-letter-charging-order-queue";
    /**
     *  exchange 名称
     */
    public static final String DEAD_LETTER_EXCHANGE_NAME = "dead-letter-charging-order-exchange";
    /**
     *  routingKey 名称 用来绑定 queue和exchange
     */
    public static final String DEAD_LETTER_ROUTING_KEY = "dead-letter-charging-order-routingKey";


    /**
     * 通过@Bean注解的方式创建Queue
     * @return
     */
    @Bean
    public Queue queue(){
        // 是否持久化
        Boolean durable = true;
        // 是否独占
        Boolean exclusive = false;
        // 是否自动删除
        Boolean autoDelete = false;

        // 其他参数
        Map<String,Object> arguments = new HashMap<>();
        //设置正常队列里 消息的生存时间 TTL TIME-TO-LIVE 生存时间 2分钟
        Integer ttl = 2 * 60 * 1000;
        arguments.put("x-message-ttl",ttl);
        //设置正常队列的死信队列
        arguments.put("x-dead-letter-exchange",DEAD_LETTER_EXCHANGE_NAME);
        //设置正常队列的死信队列的routingKey
        arguments.put("x-dead-letter-routing-key",DEAD_LETTER_ROUTING_KEY);

        return new Queue(queueName,durable,exclusive,autoDelete,arguments);
    }

    /**
     * 通过@Bean注解的方式创建DirectExchange
     * Exchange 的 类型 Direct Topic Header Fanout
     * @return
     */
    @Bean
    public DirectExchange directExchange(){
        // 是否持久化
        Boolean durable = true;
        // 是否自动删除
        Boolean autoDelete = false;
        return new DirectExchange(exchangeName,durable,autoDelete);
    }

    /**
     * 通过@Bean注解的方式创建Binding
     * 把 queue 和 exchange 进行绑定 通过 routingKey 进行绑定
     * @return
     */
    @Bean
    public Binding orderBinding(){
        return BindingBuilder.bind(queue()).to(directExchange()).with(routingKey);
    }

    /**
     * 通过@Bean注解的方式创建死信Queue
     * @return
     */
    @Bean
    public Queue deadLetterQueue(){
        // 是否持久化
        Boolean durable = true;
        // 是否独占
        Boolean exclusive = false;
        // 是否自动删除
        Boolean autoDelete = false;
        // 其他参数
        Map<String,Object> arguments = new HashMap<>();
        return new Queue(DEAD_LETTER_QUEUE_NAME,durable,exclusive,autoDelete,arguments);
    }

    /**
     * 通过@Bean注解的方式创建死信 DirectExchange
     * Exchange 的 类型 Direct Topic Header Fanout
     * @return
     */
    @Bean
    public DirectExchange deadLetterDirectExchange(){
        // 是否持久化
        Boolean durable = true;
        // 是否自动删除
        Boolean autoDelete = false;
        return new DirectExchange(DEAD_LETTER_EXCHANGE_NAME,durable,autoDelete);
    }

    /**
     * 通过@Bean注解的方式创建Binding
     * 把 queue 和 exchange 进行绑定 通过 routingKey 进行绑定
     * @return
     */
    @Bean
    public Binding deadLetterOrderBinding(){
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterDirectExchange()).with(DEAD_LETTER_ROUTING_KEY);
    }


}
