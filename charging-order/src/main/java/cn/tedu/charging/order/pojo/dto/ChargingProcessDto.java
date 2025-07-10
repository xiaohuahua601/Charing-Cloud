package cn.tedu.charging.order.pojo.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 设备给 订单发送的 充电进度的数据
 * DTO 数据传输对象
 *
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingProcessDto {
    /**
     * 订单号
     */
    String orderNo;

    /**
     *  场站id
     */
    Integer stationId;

    /**
     * 充电度数 核心参数
     */
    Float chargingCapacity;

    /**
     * 枪id
     */
    Integer gunId;

    /**
     * 枪类型
     */
    Integer gunType;

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
     * 是否充满
     */
    Boolean isFull;

}
