package cn.tedu.charging.order.common;

/**
 * 订单状态的常量
 * @Author: goodman
 * @Date: 2025/2/25 21:08
 */
public class OrderStatusConstants {

    /**
     * 订单状态 充电中
     */
    public static final Integer ORDER_STATUS_CHARGING_PROCESS = 1;

    /**
     * 订单状态 充电完成
     */
    public static final Integer ORDER_STATUS_CHARGING_END = 2;

    /**
     * 订单状态 充电异常结束
     */
    public static final Integer ORDER_STATUS_EXCEPTION_END = 3;
}
