package cn.tedu.charging.order;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 在启动类 ChargingOrderApplication 上加
 * @EnableFeignClients
 * 项目启动的时候 创建Feign的客户端
 * 目前 是 DeviceClient、
 * 设备服务的客户端
 */
@EnableFeignClients
@SpringBootApplication
public class ChargingOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChargingOrderApplication.class, args);
    }
}