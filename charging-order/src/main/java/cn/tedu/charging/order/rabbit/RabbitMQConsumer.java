package cn.tedu.charging.order.rabbit;

import cn.tedu.charging.order.config.RabbitMQConfiguration;
import cn.tedu.charging.order.dao.repository.ChargingBillRepository;
import cn.tedu.charging.order.pojo.po.ChargingBillFailPO;
import cn.tedu.charging.order.pojo.po.ChargingBillSuccessPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ的消费者
 * 用来消费消息
 * @Author: goodman
 * @Date: 2025/2/27 20:03
 */
@Slf4j
@Component
public class RabbitMQConsumer {

    @Autowired
    private ChargingBillRepository chargingBillRepository;

    /**
     * @RabbitListener 注解指定要消费的 队列名称 死信队列
     * 实现延迟消息
     * @param orderNo
     */
    @RabbitListener(queues = RabbitMQConfiguration.DEAD_LETTER_QUEUE_NAME)
    public void receive(String orderNo){
        //设备正常情况下 假定 2分钟之内肯定会返回结果 可能是开始成功 也可能是开始失败
        //在给设备发送完开始充电指令后，两分钟后收到的延迟消息
        //可以通过延迟消息的订单号 来检查订单记录

        //1 检查是否有成功订单记录
        //2 如果有成功订单记录 说明 设备在2分钟之内已经返回了 开始充电成功的结果 设备是正常的
        //  开始充电 消息无需处理  <<设备有响应 可以开始充电>>
        //3 如果没有成功订单记录

        //4 检查是否有失败订单记录
        //5 如果有失败订单记录 说明 设备在2分钟之内已经返回了 开始充电成功的结果 设备是异常
        //  无法开始充电 消息无需处理 <<设备有响应 无法开始充电>>
        //6 如果没有失败订单记录

        //7 没有成功订单记录 也 没有失败订单记录 说明设备在2分钟之内没有返回 开始充电的结果
        //8 订单服务可以判断 <<设备无响应>> 因为2分钟之内没有返回 开始充电结果 保存失败订单记录 失败原因 设备无响应

        log.debug("接收到延迟消息:{}",orderNo);
        //检查是否有成功订单记录
        log.debug("检查是否有成功订单记录:{}",orderNo);
        ChargingBillSuccessPO chargingBillSuccessPO = chargingBillRepository.getSuccessOrder(orderNo);
        log.debug("检查是否有成功订单记录:{}",chargingBillSuccessPO);
        if (chargingBillSuccessPO != null) {
            //如果有成功订单记录
            log.debug("<<设备有响应 可以开始充电>>,无需处理");
        }else {
            //如果没有成功订单记录
            //检查是否有失败订单记录
            log.debug("检查是否有失败订单记录:{}",orderNo);
            ChargingBillFailPO chargingBillFailPO = chargingBillRepository.getFailOrder(orderNo);
            log.debug("检查是否有失败订单记录:{}",chargingBillFailPO);
            if (chargingBillFailPO != null) {
                //如果有失败订单记录
                log.debug("<<设备有响应 无法开始充电>>,无需处理");
            }else {
                //没有失败订单记录
                log.debug("没有成功订单记录 也 没有失败订单记录");
                log.debug("设备无响应 无法开始充电,保存失败订单记录，记录失败原因 <<设备无响应>>");
                ChargingBillFailPO chargingBillFailPO1 = new ChargingBillFailPO();
                chargingBillFailPO1.setBillId(orderNo);
                chargingBillFailPO1.setFailDesc("设备无响应-无法开始充电");
                log.debug("保存失败订单记录:{}",chargingBillFailPO1);
                Integer row = chargingBillRepository.saveFailOrder(chargingBillFailPO1);
                log.debug("保存失败订单记录:{},影响行数:{}",chargingBillFailPO1,row);
                log.debug("修改枪的状态为故障");
                //调用 deviceClient.updateGunStatus方法 修改枪的状态
                //参考 OrderServiceImpl#updateGunStatus 把使用中修改为 故障
                log.debug("通知 充电用户 更换设备");
                log.debug("给 充电用户 发送优惠券 表示歉意");
                log.debug("通知 设备管理员 检修设备");
            }
        }
    }
}
