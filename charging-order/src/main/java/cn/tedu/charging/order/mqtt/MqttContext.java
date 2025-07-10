package cn.tedu.charging.order.mqtt;

import cn.tedu.charging.order.dao.repository.ChargingBillRepository;
import cn.tedu.charging.order.dao.repository.ChargingProcessESRepository;
import cn.tedu.charging.order.feign.CostClient;
import cn.tedu.charging.order.websocket.WebSocketServer;
import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: goodman
 * @Date: 2025/2/25 21:12
 */
@Component
public class MqttContext {


    @Autowired
    private ChargingProcessESRepository chargingProcessESRepository;


    public ChargingProcessESRepository getChargingProcessESRepository() {
        return chargingProcessESRepository;
    }

    @Autowired
    private WebSocketServer webSocketServer;

    public WebSocketServer getWebSocketServer() {
        return webSocketServer;
    }

    @Autowired
    private ChargingBillRepository chargingBillRepository;

    @Autowired
    private CostClient costClient;

    public CostClient getCostClient() {
        return costClient;
    }

    //@Autowired
    //private xxx

    public ChargingBillRepository getChargingBillRepository() {
        return chargingBillRepository;
    }

}
