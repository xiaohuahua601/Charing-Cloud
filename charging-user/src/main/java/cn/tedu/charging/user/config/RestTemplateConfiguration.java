package cn.tedu.charging.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration //spring 在启动的时候 会扫描 Configuration
public class RestTemplateConfiguration {

    @Bean //告诉IOC容器,让容器帮忙创建一个Bean对象,他的类型是 RestTemplate
    //具体的创建方法 restTemplate() IOC容器会调用 restTemplate 执行里面的代码
    //来创建 RestTemplate
    public RestTemplate restTemplate() {
        //可以对RestTemplate 进行一些配置
        //错误处理,消息转换,可以配置网络相关的 超时配置
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}
