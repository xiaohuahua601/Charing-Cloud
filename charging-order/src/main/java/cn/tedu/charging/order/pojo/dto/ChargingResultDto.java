package cn.tedu.charging.order.pojo.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 设备给订单发送的 开始充电结果
 * DTO 数据传输对象
 *
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingResultDto {
    /**
     * 订单号
     */
    String orderNo;

    /**
     * 枪id
     */
    Integer gunId;

    /**
     * 桩id
     */
    Integer pileId;

    /**
     * 用户id
     */
    Integer userId;

    /**
     * 车辆id
     */
    Integer vehicleId;

    /**
     * 开始结果
     */
    String result;
}
