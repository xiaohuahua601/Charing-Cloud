package cn.tedu.charging.common.pojo.param;

import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 订单服务调用计价服务 计算价格的入参
 * 缺少了哪个参数 你的业务无法正常运行
 * 计算价格 没有度数 是无法计算 价格 度数 是核心参数
 * @Author: goodman
 * @Date: 2025/3/2 21:37
 */
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ChargingProcessParam {

    /**
     * 订单号
     */
    String orderNo;

    /**
     * 充电度数 核心参数
     */
    Float chargingCapacity;

    /**
     * 设备相关 桩id
     */
    Integer pileId;

    /**
     * 设备相关 枪id
     */
    Integer gunId;

    /**
     * 设备相关 枪类型
     */
    Integer gunType;

    /**
     * 站id
     */
    Integer stationId;

    /**
     * 用户相关 用户id
     */
    Integer userId;

}
