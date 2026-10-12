# Charing-Cloud｜充电桩云端业务服务原型

> 一个 Java 多模块充电业务练习项目，拆分设备、订单、计价、用户和网关服务，展示充电指令、设备回报、进度计价与超时检查的代码路径。

## 项目概览

| 项目要点 | 说明 |
| --- | --- |
| 业务场景 | 用户选择充电枪后发起充电，云端向设备发送指令，接收设备结果与进度，按站点和时段规则计算费用。 |
| 服务划分 | `charging-device` 管理站点与充电枪；`charging-order` 处理订单和设备消息；`charging-cost` 负责计价；`charging-user` 处理用户；`charging-gateway` 提供网关入口。 |
| 技术栈 | Spring Boot、Spring Cloud/Nacos、OpenFeign、MyBatis-Plus、MySQL、Redis、MQTT、RabbitMQ、Elasticsearch、WebSocket。 |
| 完成度 | 这是业务原型，部分链路保留占位实现与 TODO，不能视为可直接上线的完整充电平台。 |

## 可追踪的业务链路

1. [订单接口](charging-order/src/main/java/cn/tedu/charging/order/controller/OrderController.java) 接收创建请求。
2. [订单服务](charging-order/src/main/java/cn/tedu/charging/order/service/impl/OrderServiceImpl.java) 调用设备服务修改枪状态，通过 MQTT 发送开始指令，并向 RabbitMQ 发送超时检查消息。
3. [MQTT 消费者](charging-order/src/main/java/cn/tedu/charging/order/mqtt/MqttConsumer.java) 接收设备响应和充电进度；进度到达后调用计价服务、尝试推送并保存进度。
4. [计价服务](charging-cost/src/main/java/cn/tedu/charging/cost/service/impl/CostServiceImpl.java) 根据站点、枪类型和当前时段匹配规则，并基于累计电量计算本次与累计费用；相关进度与规则使用 Redis 缓存。
5. [RabbitMQ 消费者](charging-order/src/main/java/cn/tedu/charging/order/rabbit/RabbitMQConsumer.java) 检查延迟消息对应订单是否收到设备结果；无结果时记录失败订单。

## 当前边界与运行条件

- `OrderServiceImpl#getOrderNo()` 当前返回固定值 `666`，尚未实现唯一订单号生成。
- 设备高温、余额不足、充满后的结算处理等在 MQTT 消费者中仍是注释说明；超时后修改充电枪状态和通知也尚未落地。
- 仓库有 Maven 多模块工程和 [数据库脚本](doc/charging.sql)，但未提供一键部署文件。启动需要自行准备并配置 MySQL、Nacos、Redis、RabbitMQ、MQTT Broker 等依赖；配置文件中的本地地址与示例凭据需要替换。
- 本介绍基于静态源码核查，未完成设备联调或全链路运行验证。

## 建议阅读顺序

[根 Maven 工程](pom.xml) → [订单创建](charging-order/src/main/java/cn/tedu/charging/order/service/impl/OrderServiceImpl.java) → [设备消息处理](charging-order/src/main/java/cn/tedu/charging/order/mqtt/MqttConsumer.java) → [计价逻辑](charging-cost/src/main/java/cn/tedu/charging/cost/service/impl/CostServiceImpl.java)。
