package cn.tedu.charging.order.pojo.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 开始充电指令
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingStartCommandDto {

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
     * 开始充电指令
     */
    String command;
}
