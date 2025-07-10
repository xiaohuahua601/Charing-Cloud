package cn.tedu.charging.order.service.impl;

import cn.tedu.charging.common.pojo.JsonResult;
import cn.tedu.charging.common.pojo.param.UpdateGunStatusParam;
import cn.tedu.charging.common.pojo.vo.UserInfoVO;
import cn.tedu.charging.common.utils.JsonUtils;
import cn.tedu.charging.order.common.GunStatusConstants;
import cn.tedu.charging.order.common.MqttConstants;
import cn.tedu.charging.order.feign.DeviceClient;
import cn.tedu.charging.order.feign.UserClient;
import cn.tedu.charging.order.mqtt.MqttProducer;
import cn.tedu.charging.order.pojo.param.OrderAddParam;
import cn.tedu.charging.order.pojo.dto.ChargingStartCommandDto;
import cn.tedu.charging.order.quartz.DeviceCheckJob;
import cn.tedu.charging.order.rabbit.RabbitMQProducer;
import cn.tedu.charging.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public String create(OrderAddParam orderAddParam) {

        log.debug("生成唯一的订单号");
        String orderNo = getOrderNo();
        log.debug("生成唯一的订单号:{}",orderNo);

        log.debug("修改枪的状态，把枪的状态从 空闲 修改为 使用中,枪id:{}",orderAddParam.getGunId());
        Boolean updateResult = updateGunStatus(orderAddParam.getGunId());
        log.debug("修改枪的状态，把枪的状态从 空闲 修改为 使用中,枪id:{},结果:{}",orderAddParam.getGunId(),updateResult);

        log.debug("查询用户信息用户id:{}",orderAddParam.getUserId());
        Integer vehicleId = getUserInfo(orderAddParam.getUserId());
        log.debug("查询用户信息用户id:{},车辆id:{}",orderAddParam.getUserId(),vehicleId);

        log.debug("开始充电,给EMQX发送开始充电指令");
        Boolean send2EMQXResult = startCharging(orderNo,orderAddParam.getPileId(), orderAddParam.getGunId());
        log.debug("开始充电,给EMQX发送开始充电指令,结果:{}",send2EMQXResult);

        log.debug("发送消息到RabbitMQ,订单号:{}",orderNo);
        Boolean send2RabbitMQResult = sendMessage(orderNo);
        log.debug("发送消息到RabbitMQ,订单号:{},结果:{}",orderNo,send2RabbitMQResult);

//        log.debug("创建定时任务，用来检测充电过程中设备故障,订单号:{}",orderNo);
//        try {
//            //创建订单的接口 每次被调用 都会创建一个定时任务，
//            //从业务上讲 没有问题 但是有优化的点 应该是尽量避免少创建对象 DeviceCheckJob 导致浪费资源
              //应该在设备有响应的时候 开始充电 再去创建定时任务
//            DeviceCheckJob deviceCheckJob = new DeviceCheckJob(orderNo);
//        } catch (SchedulerException e) {
//            log.error("创建定时任务失败，用来检测充电过程中设备故障,订单号:{}",orderNo,e);
//        }
//        log.debug("创建定时任务成功，用来检测充电过程中设备故障,订单号:{}",orderNo);

        return orderNo;
    }


    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    /**
     * 发送消息
     */
    private Boolean sendMessage(String orderNo){
       return rabbitMQProducer.send(orderNo);
    }

    @Autowired
    private MqttProducer mqttProducer;

    /**
     * 开始充电
     * 业务目标
     * 告诉设备 具体的设备 哪个桩，哪个枪 给 哪个订单 开始充电
     */
    private Boolean startCharging(String orderNo,Integer pileId,Integer gunId){
        //1>定义Topic
        // 区分消息要发给不同的设备 /topic/charging/start/桩id  /topic/charging/start/1
        String topic = MqttConstants.TOPIC_CHARGING_START + pileId;
        //1>定义消息
        ChargingStartCommandDto chargingCommand = new ChargingStartCommandDto();
        chargingCommand.setOrderNo(orderNo);
        chargingCommand.setPileId(pileId);
        //一个桩上可能有多个充电枪，发送消息的时候带着枪id，表示用具体的哪个枪开始充电
        chargingCommand.setGunId(gunId);
        //开始充电指令
        chargingCommand.setCommand(MqttConstants.START_CHARGING_COMMAND);
        //把开始充电的对象 转化为 json 字符串
        String json = JsonUtils.toJson(chargingCommand);
        mqttProducer.sendMessage(topic,json);
        return true;
    }

    @Autowired
    private UserClient userClient;

    /**
     * 查询用户信息
     */
    private Integer getUserInfo(Integer userId){
        JsonResult<UserInfoVO> userInfo = userClient.getUserInfo(userId);
        if (userInfo != null) {
            return userInfo.getData().getVehicleId();
        }
        return  -1;
    }

    @Autowired
    private DeviceClient deviceClient;


    /**
     *  修改枪的状态
     *  订单服务
     *  调用
     *  设备服务
     *  修改枪的状态
     */
    private Boolean updateGunStatus(Integer gunId){
        UpdateGunStatusParam gunStatusParam = new UpdateGunStatusParam();
        gunStatusParam.setGunId(gunId);
        //把枪的状态修改为 使用中
        gunStatusParam.setStatus(GunStatusConstants.GUN_STATUS_USED);//2 不推荐在代码里写死 推荐使用枚举或者常量
        JsonResult<Boolean> jsonResult =
                deviceClient.updateGunStatus(gunStatusParam);
        if (jsonResult != null){
            return jsonResult.getData();
        }
        return false;
    }


    /**
     * 生成唯一的订单号
     * @return
     */
    private String getOrderNo(){
        return "666";
    }
}
