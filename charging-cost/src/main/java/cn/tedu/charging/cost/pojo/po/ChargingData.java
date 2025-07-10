package cn.tedu.charging.cost.pojo.po;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * 充电数据
 * @Author: goodman
 * @Date: 2025/3/4 20:29
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingData {


    /**
     * 充电度数
     * 计算实际充电度数的时候 需要 用本次度数  减去  上次度数
     * 初始化的时候 总度数为 0
     */
    Float chargingCapacity = 0f;

    /**
     *  获取充电度数
     *  如果是第一次 直接返回 设备同步的充电度数
     *  如果不是第一次 计算 设备同步的充电度数 - 上次的充电度数
     *
     *  次数          度数       实际度数        本地变量 chargingCapacity
     *  初始化         0                               0
     *  第一次         3        3-0 = 3              保存 3
     *  第二次         5        5-3 = 2              保存 5
     *
     * @param chargingCapacity  设备同步的充电度数
     * @return
     */
    public Float getChargingCapacity(Float chargingCapacity) {
        return chargingCapacity - this.chargingCapacity;
    }

    /**
     * 总花费
     * 计算总花费的时候 需要 用本次的花费 加上 上次的花费
     * 初始化的时候 总花费为0
     */
    BigDecimal totalCost = BigDecimal.ZERO;

    /**
     * 保存总花费
     *    次数          总花费       实际花费        本地变量 totalCost
     *    初始化         0                               0
     *    第一次         3        3 + 0 = 3            保存 3
     *    第二次         5        5 + 3 = 8            保存 8
     *
     * @param totalCost
     */
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = this.totalCost.add(totalCost);
    }

    /**
     * 总时长
     * 记录第一次同步的时间
     * 充电完成后 用充电完成的时间 减去 第一次同步的时间 = 总时长
     */
    Long startTime;
}
