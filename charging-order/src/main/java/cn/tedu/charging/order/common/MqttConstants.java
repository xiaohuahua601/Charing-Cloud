package cn.tedu.charging.order.common;

/**
 * mqtt
 * 订单服务和设备 常量
 */
public class MqttConstants {

    /**
     * 订单服务 给 设备发送 开始充电指令 的topic
     */
    public static final String TOPIC_CHARGING_START = "/topic/charging/start/";

    /**
     *  设备给订单返回 开始充电结果的 topic
     */
    public static final String TOPIC_CHARGING_RESULT = "/topic/charging/result";

    /**
     * 设备定时 同步充电进度的 topic
     */
    public static final String TOPIC_CHARGING_PROCESS = "/topic/charging/process";


    /**
     * 开始充电指令
     */
    public static final String START_CHARGING_COMMAND = "start";

    /**
     * 停止充电指令
     */
    public static final String STOP_CHARGING_COMMAND = "stop";

    /**
     * 开始充电成功结果
     */
    public static final String START_CHARGING_SUCCESS = "start_success";
}
