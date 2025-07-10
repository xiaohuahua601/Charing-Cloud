package cn.tedu.charging.order.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * mqtt发送者
 * 订单服务 用来发送消息到emqx
 * @Author: goodman
 * @Date: 2025/2/24 20:26
 */
@Slf4j
@Component
public class MqttProducer {

    @Autowired
    private MqttClient mqttClient;

    /**
     * 往  topic 发送 消息 message
     * @param topic
     * @param message
     */
    public void sendMessage(String topic, String message) {
        //public void publish(String topic, byte[] payload,int qos, boolean retained)
        try {
            mqttClient.publish(topic, message.getBytes(),0,false);
        } catch (MqttException e) {
            log.error("给emqx发送消息失败topic:{},message:{}",topic,message,e);
            throw new RuntimeException(e);
        }
    }
}
