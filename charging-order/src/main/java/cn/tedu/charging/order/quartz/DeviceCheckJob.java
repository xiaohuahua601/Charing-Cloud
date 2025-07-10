package cn.tedu.charging.order.quartz;

//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import cn.tedu.charging.order.dao.repository.OrderRepository;
import cn.tedu.charging.order.dao.repository.ChargingBillRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

@Slf4j
public class DeviceCheckJob implements Job {
    /**
     * 必须有一个无参数的构造器
     *  如果没有， 在初始化 new DeviceCheckJob 调用 Class newInstance 实例化，
     *  通过无参的构造器实例化对象
     *  getConstructor0() 获取无参构造器
     *  public DeviceCheckJob(String orderNo) 覆盖 默认的无参构造器
     *  找不到方法 抛异常 NoSuchMethodException  找不到构造器，创建不了 DeviceCheckJob对象
     */
   public DeviceCheckJob() {
       log.debug("DeviceCheckJob的无参构造器被调用了");
   }

    /**
     * 通过构造器创建定时任务
     * SchedulerFactory
     * scheduler 调度器
     * Trigger  触发
     * JobDetail 任务详情
     */
    public DeviceCheckJob(String orderNo) throws SchedulerException {
        log.debug("创建超时订单检查任务");
        //创建调度器工厂
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        //获取调度器
        Scheduler scheduler = schedulerFactory.getScheduler();

        //获取当前时间
        long now = System.currentTimeMillis();
        //模拟充电需要充满的时间 4分钟
        long time = 1000 * 60 * 4 ;
        long deviceCheckTime = now + time;
        //将时时间戳 转换成 Date类型
        Date date = new Date(deviceCheckTime);

        //定义触发器 4 分钟之后执行
        Trigger trigger = TriggerBuilder.newTrigger()
                .startAt(date)
                .build();

        //创建任务详情 同时 usingJobData 传入订单号
        JobDetail jobDetail = JobBuilder.newJob(DeviceCheckJob.class)
                .withIdentity("deviceCheckJob", "deviceCheckJobGroup")
                .usingJobData("orderNo", orderNo)
                .build();

        //调度器 把任务和触发器进行绑定
        //调度器 scheduler,让任务 JobDetail 和 按照 触发器的 时间 来触发
        scheduler.scheduleJob(jobDetail, trigger);
        //启动调度器
        scheduler.start();
    }


    /**
     * quartz 调度器 调用这个方法
     *      * 9. 判断订单的状态
     *      * 10. 正常情况   在消息过期之前 订单已经完成 订单的状态是 充电完成
     *      * 11. 正常情况 下 无需处理
     *      * 12. 异常情况 在消息过期时，订单还没有完成，订单的状态时 充电中
     *      * 13. 异常情况 修改订单的状态为 异常结束
     * @param context
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("超时订单任务开始执行");
        String orderNo = context.getJobDetail().getJobDataMap().getString("orderNo");
        //没有在容器中的 DeviceCheckJob 想去使用容器中的 ChargingBillRepository
        ChargingBillRepository orderRepository = SpringContextUtils.getBean(ChargingBillRepository.class);
        //超时订单 把充电中的修改为异常结束 只是判断的订单状态
        Integer row = orderRepository.updateOrderStatusFromChargingToExceptionEnd(orderNo);
        //判断 充电进度数据 todo 自己实现
        //判断影响行数
        if (row == 0){
            log.debug("影响行数为0，表示没有更新数据，表示订单正常结束，设备没有出现故障");
        }else {
            log.debug("影响行数为1，表示更新了数据，表示订单异常结束，设备出现故障");
            log.debug("<<通知充电用户>> 更换设备");
            log.debug("<<通知充电用户>> 给用户发短信，表示歉意");
            log.debug("<<通知充电用户>> 给用户发短信，发送优惠券 营销系统，给用户心灵上的安慰");
            log.debug("<<通知设备管理员>> 检修设备");
            log.debug("修改枪的状态为 故障");
        }
    }
}
