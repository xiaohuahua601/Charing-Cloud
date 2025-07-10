package cn.tedu.charging.order.mqtt;

import cn.tedu.charging.common.pojo.param.ChargingProcessParam;
import cn.tedu.charging.common.pojo.vo.ChargingProcessVO;
import cn.tedu.charging.common.utils.JsonUtils;
import cn.tedu.charging.order.common.MqttConstants;
import cn.tedu.charging.order.common.OrderStatusConstants;
import cn.tedu.charging.order.pojo.dto.ChargingProcessDto;
import cn.tedu.charging.order.pojo.dto.ChargingResultDto;
import cn.tedu.charging.order.pojo.po.ChargingBillFailPO;
import cn.tedu.charging.order.pojo.po.ChargingBillSuccessPO;
import cn.tedu.charging.order.pojo.po.ChargingProcessPO;
import cn.tedu.charging.order.quartz.DeviceCheckJob;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;

/**
 * eqmx 的消费者
 * 用来 消费 设备给emqx发送的消息
 *
 * @Author: goodman
 * @Date: 2025/2/24 21:25
 */
@Slf4j
public class MqttConsumer implements MqttCallbackExtended {

    private MqttClient mqttClient;

    private MqttContext mqttContext;

    /**
     * 可以通过 构造方法 把容器中的 MqttClient 传递进来
     * MqttConsumer的变量 mqttClient 来接受
     * @param mqttClient
     */
    public MqttConsumer(MqttClient mqttClient,MqttContext mqttContext) {
        this.mqttClient = mqttClient;
        this.mqttContext = mqttContext;
    }



    /**
     * 连接成功的时候
     * 订单服务和EMQX 连接成功后，会调用 connectComplete
     * @param reconnect If true, the connection was the result of automatic reconnect.
     * @param serverURI The server URI that the connection was made to.
     */
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.debug("connectComplete被调用了  表示订单服务和EMQX连接完成");
        //订单服务在和EMQX连接成功后，通过MqttClient 订阅 设备给订单发送 开始充电结果 的topic
        String chargingResultTopic = MqttConstants.TOPIC_CHARGING_RESULT;
        //订单服务在和EMQX连接成功后，通过MqttClient 订阅 设备同步充电进度的topic
        String chargingProcessTopic = MqttConstants.TOPIC_CHARGING_PROCESS;
        try {
            log.debug("订单服务订阅设备返回开始充电结果topic:{}",chargingProcessTopic);
            mqttClient.subscribe(chargingResultTopic);
            log.debug("订单服务成功订阅设备返回开始充电结果topic:{}",chargingResultTopic);

            log.debug("订单服务订阅设备同步充电进度topic:{}",chargingProcessTopic);
            mqttClient.subscribe(chargingProcessTopic);
            log.debug("订单服务成功订阅设备同步充电进度topic:{}",chargingProcessTopic);

        } catch (MqttException e) {
            log.error("订阅topic:{}失败",chargingResultTopic,e);
        }
    }

    /**
     *  连接成功后，如果连接断开，会调用 connectionLost
     * @param cause the reason behind the loss of connection.
     */
    @Override
    public void connectionLost(Throwable cause) {
        log.error("connectionLost 被调用了，连接断开:",cause);
    }

    /**
     * 设备发送消息到一个topic
     * 如果订单服务订阅了这个topic
     * 消息就会到达这个topic
     * 当消息到达后，会调用 messageArrived
     * 设备发送的消息到达订单，这个方法会被调用
     * @param topic name of the topic on the message was published to
     * @param topic name of the topic on the message was published to
     * @param message the actual message.
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        log.debug("消息到达订单服务:topic:{},message:{}",topic,message);
        //判断topic 判断消息是从哪个Topic 来的
        if (MqttConstants.TOPIC_CHARGING_RESULT.equals(topic)) {
            log.debug("订单服务收到设备返回的<开始充电结果>响应");
            handleChargingResult(message);
        }else if (MqttConstants.TOPIC_CHARGING_PROCESS.equals(topic)) {
            log.debug("订单服务收到设备同步充电进度");
            handleChargingProcess(message);
        }else {
            log.debug("订单服务收到未知topic:{}的消息",topic);
        }
    }


    /**
     * 订单服务处理设备同步的充电进度消息
     * @param message
     */
    private void handleChargingProcess(MqttMessage message) {
        //把设备同步的充电进度的json转换为 ChargingProcessDto
        ChargingProcessDto chargingProcessDto =
                JsonUtils.fromJson(message.toString(), ChargingProcessDto.class);
        log.debug("充电进度消息到达订单服务,转化为对象:{}",chargingProcessDto);
        if (chargingProcessDto != null) {
            //1 调用计价服务计算价格
            log.debug("调用计价服务计算价格:{}",chargingProcessDto);
            ChargingProcessVO chargingProcessVO = calculateCost(chargingProcessDto);
            log.debug("调用计价服务计算价格:{},结果:{}",chargingProcessDto,chargingProcessVO);
            //2 判断计算价格结果是否为空
            if (chargingProcessVO != null) {
                //3 推送充电进度给用户
                Integer userId = chargingProcessDto.getUserId();
                log.debug("给用户:{}推送充电进度:{}",userId,chargingProcessVO);
                Boolean sendResult = sendChargingProcess2User(chargingProcessDto.getUserId(),chargingProcessVO);
                log.debug("给用户:{}推送充电进度:{},推送结果:{}",userId,chargingProcessVO,sendResult);
                //如果推送结果失败 记录失败日志，重试，如果重试多长都不成功，告警 人工处理

                //4 保存充电数据
                // 类似 普通订单的 订单详情 完整的记录 充电订单 从开始充电到充电完成 完整的过程 充电度数 每次的花费
                log.debug("保存充电数据:{},价格结果:{}",chargingProcessDto,chargingProcessVO);
                saveChargingProcess(chargingProcessDto,chargingProcessVO);
                log.debug("保存充电数据:{},价格结果:{},成功",chargingProcessDto,chargingProcessVO);

                //5 判断设备温度
                // 正常 小于30  继续充电
                // 告警 大于30 小于 50 给用户推送消息，温度过高是否继续充电 让用户选择是否继续;
                // 异常 大于 50度 直接停止充电 给用户推送消息，温度过高设备异常，已经停止充电，不让用户选择，强制停止;

                //6 判断用户余额
                // 采用的方案 <<先充值后充电的业务规则>>
                //为了避免 用户充了10块钱 充电花了50块钱
                //为了避免 用户余额不足
                //每次 设备同步充电进度的时候，计算完本次花费，都去判断用户余额是否大于本次花费，如果大于 继续充，如果小于等于 停止充电
                // 给用户发送通知 余额不足 请充值 引导用户到支付页面充值

                //7 判断是否充满
                // 如果没满 继续充
                // 如果满了 设备已经自动停止充了
                // 修改订单状态为 充电完成
                // 修改枪的状态为 空闲 可选
                // 扣除用户的余额 更新用户余额 减去本次充电的花费  保证 订单状态修改充电完成 和 扣除用户余额 全部成功，全部失败 分布式事务的场景
                // 给用户推送消息，充电完成，推送账单
                // 给用户发送优惠券
            }
        }

    }

    /**
     * 保存充电进度数据 todo
     * @param chargingProcessDto
     * @param chargingProcessVO
     */
    private void saveChargingProcess(ChargingProcessDto chargingProcessDto, ChargingProcessVO chargingProcessVO){
        ChargingProcessPO chargingProcessPO = new ChargingProcessPO();
        //把充电进度数据的id设置为当前时间戳 todo
        chargingProcessPO.setId(System.currentTimeMillis());
        //把设备同步的充电进度数据 赋值 到 chargingProcessPO
        //copyProperties 使用的时候要注意 字段的类型
        BeanUtils.copyProperties(chargingProcessDto,chargingProcessPO);
        //把调用计价服务的计算价格接口的返回结果 赋值 到 chargingProcessPO
        BeanUtils.copyProperties(chargingProcessVO,chargingProcessPO);
        log.debug("保存充电进度数据:{}",chargingProcessPO);
        mqttContext.getChargingProcessESRepository().save(chargingProcessPO);
        log.debug("保存充电进度数据成功:{}",chargingProcessPO);
    }

    /**
     * 订单服务 主动 推送消息给 用户todo
     * 理解 推送的概念 推送就是 订单服务 主动 推送消息给用户
     * @param chargingProcessVO
     */
    private Boolean sendChargingProcess2User(Integer userId,ChargingProcessVO chargingProcessVO){
        //通过JsonUtils 把 chargingProcessVO 转为json
        String message = JsonUtils.toJson(chargingProcessVO);
        //推送消息给用户
        mqttContext.getWebSocketServer().sendMessage(userId,message);
        return true;
    }

    /**
     * 调用计价服务计算价格
     * @param chargingProcessDto
     * @return ChargingProcessVO
     */
    private ChargingProcessVO calculateCost(ChargingProcessDto chargingProcessDto) {
        ChargingProcessParam chargingProcessParam = new ChargingProcessParam();
        //把设备同步的充电数据 chargingProcessDto 复制 到 调用计价服务计算价格接口的入参 chargingProcessParam
        BeanUtils.copyProperties(chargingProcessDto,chargingProcessParam);
        //调用计价服务
        return mqttContext.getCostClient().calculateCost(chargingProcessParam).getData();
    }

    /**
     * 处理设备返回的 开始充电结果响应
     * @param message
     */
    private void handleChargingResult(MqttMessage message) {
        ChargingResultDto chargingResultDto =
                JsonUtils.fromJson(message.toString(), ChargingResultDto.class);
        log.debug("消息到达订单服务,转化为对象:{}",chargingResultDto);
        if (chargingResultDto != null) {
            String result = chargingResultDto.getResult();
            if (MqttConstants.START_CHARGING_SUCCESS.equals(result)) {
                log.debug("订单服务收到设备返回的<开始充电成功>响应");
                handleChargingSuccess(chargingResultDto);
            } else {
                log.debug("订单服务收到设备返回的<开始充电失败>响应");
                handlerChargingFail(chargingResultDto);
            }
        }
    }

    /**
     * 处理设备返回的 开始充电失败响应
     * @param chargingResultDto
     */
    private void handlerChargingFail(ChargingResultDto chargingResultDto) {
        ChargingBillFailPO chargingBillFailPO = new ChargingBillFailPO();
        chargingBillFailPO.setBillId(chargingResultDto.getOrderNo());
        chargingBillFailPO.setFailDesc("设备有响应-无法开始充电");
        log.debug("保存失败订单记录:{}",chargingBillFailPO);
        Integer row = mqttContext.getChargingBillRepository().saveFailOrder(chargingBillFailPO);
        log.debug("保存失败订单记录:{},影响行数:{}",chargingBillFailPO,row);
        log.debug("修改枪的状态为故障");
        //调用 deviceClient.updateGunStatus方法 修改枪的状态
        //参考 OrderServiceImpl#updateGunStatus 把使用中修改为 故障
        log.debug("通知 充电用户 更换设备");
        log.debug("给 充电用户 发送优惠券 表示歉意");
        log.debug("通知 设备管理员 检修设备");
    }

    /**
     * 处理设备返回的开始充电成功响应
     * @param chargingResultDto
     */
    private void handleChargingSuccess(ChargingResultDto chargingResultDto) {
        ChargingBillSuccessPO chargingBillSuccessPO = new ChargingBillSuccessPO();
        BeanUtils.copyProperties(chargingResultDto, chargingBillSuccessPO);
        String orderNo = chargingResultDto.getOrderNo();
        chargingBillSuccessPO.setBillId(orderNo);
        //订单状态为 充电中
        chargingBillSuccessPO.setBillStatus(OrderStatusConstants.ORDER_STATUS_CHARGING_PROCESS);
        log.debug("保存成功订单记录:{}",chargingBillSuccessPO);
        Integer row = mqttContext.getChargingBillRepository().saveSuccessOrder(chargingBillSuccessPO);
        log.debug("保存成功订单记录:{},影响行数:{}",chargingBillSuccessPO,row);

        //订单服务 收到 设备返回的开始充电成功 指令后 ，这种情况说明 <<设备有响应 开始充电成功>>
        //才有必要 去创建定时任务 用来检测 充电过程中设备故障，其他情况 没有开始充电成功，也没有必要检测  充电过程中设备故障
        log.debug("创建定时任务，用来检测充电过程中设备故障,订单号:{}",orderNo);
        try {
            //创建订单的接口 每次被调用 都会创建一个定时任务，
            //从业务上讲 没有问题 但是有优化的点 应该是尽量避免少创建对象 DeviceCheckJob 导致浪费资源
            DeviceCheckJob deviceCheckJob = new DeviceCheckJob(orderNo);
        } catch (SchedulerException e) {
            log.error("创建定时任务失败，用来检测充电过程中设备故障,订单号:{}",orderNo,e);
        }
        log.debug("创建定时任务成功，用来检测充电过程中设备故障,订单号:{}",orderNo);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.debug("deliveryComplete 投递成功");
    }
}
