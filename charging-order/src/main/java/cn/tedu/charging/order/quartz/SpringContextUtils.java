package cn.tedu.charging.order.quartz;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring 的钩子函数
 *
 * implements ApplicationContextAware
 * 可以得到
 * applicationContext
 * 通过 applicationContext 获取 bean
 *
 */
@Slf4j
@Component
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 通过 类型 获取bean byType
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过 名字 获取bean byName
     */
    public static <T> T getBean(String name,Class<T> clazz) {
        return applicationContext.getBean(name,clazz);
    }

}
