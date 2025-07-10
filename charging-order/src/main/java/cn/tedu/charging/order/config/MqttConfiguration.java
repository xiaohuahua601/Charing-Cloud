package cn.tedu.charging.order.config;

import cn.tedu.charging.order.mqtt.MqttConsumer;
import cn.tedu.charging.order.mqtt.MqttContext;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * 配置mqtt的连接
 */
@Configuration
public class MqttConfiguration {

    @Autowired
    private MqttContext mqttContext;

    /**
     * @Bean 告诉Spring容器创建一个MqttClient 对象的实例
     * 放到容器，让容器帮忙管理  MqttClient
     * 后续 别的 在容器中 的类 注入 MqttClient 使用
     * @return
     * @throws MqttException
     */
    @Bean
    public MqttClient mqttClient() throws MqttException {
        //定义连接地址 tcp协议 端口号 1883
        String address = "tcp://localhost:1883";
        String userName = "admin";
        String password = "public";
        //emqx规定 同一时刻不能有重复的clientId 同时连接
        //UUID 随机生成一个clientID
        String clientId = UUID.randomUUID().toString().replace("-","");
        MqttClient mqttClient = new MqttClient(address, clientId);
        //创建连接的配置对象
        MqttConnectOptions options = new MqttConnectOptions();
        //设置用户名
        options.setUserName(userName);
        //设置密码 String 转化为 char 数组
        options.setPassword(password.toCharArray());
        //设置超时时间
        options.setConnectionTimeout(10);
        //设置会话心跳时间
        options.setKeepAliveInterval(20);
        //自动重连
        options.setAutomaticReconnect(true);
        //把 连接的配置对象 设置到mqttClient，同时连接
        // MqttConsumer  用来订阅topic 和 消费消息
        mqttClient.setCallback(new MqttConsumer(mqttClient,mqttContext));
        mqttClient.connect(options);
        return mqttClient;

    }
}
