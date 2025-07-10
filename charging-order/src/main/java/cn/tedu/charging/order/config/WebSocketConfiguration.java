package cn.tedu.charging.order.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Author: goodman
 * @Date: 2025/3/9 19:52
 */
@Configuration
public class WebSocketConfiguration {

    /**
     * 用户和服务进行连接 是通过  ServerEndpoint 建立连接
     * 具体暴露的地址 在哪
     * @ServerEndpoint("/ws/server/{userId}")
     * public class WebSocketServer
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
