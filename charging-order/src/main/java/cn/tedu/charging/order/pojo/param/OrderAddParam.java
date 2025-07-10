package cn.tedu.charging.order.pojo.param;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 创建订单的入参
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderAddParam {

    /**
     * 用户id
     */
    Integer userId;

    /**
     * 桩id
     */
    Integer pileId;

    /**
     * 枪id
     */
    Integer gunId;
}
