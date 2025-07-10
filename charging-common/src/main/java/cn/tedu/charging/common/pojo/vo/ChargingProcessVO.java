package cn.tedu.charging.common.pojo.vo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * 订单服务 调用计价服务 计算价格的出参
 *
 * 出参 用户更关心什么 我们就应该返回什么
 *
 * @Author: goodman
 * @Date: 2025/3/2 21:39
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingProcessVO {

    /**
     * 订单号
     */
    String orderNo;

    /**
     * 总金额
     */
    BigDecimal totalCost;

    /**
     * 总度数
     */
    Float chargingCapacity;


    /**
     * 总时长
     */
    Long chargingTime;


}
