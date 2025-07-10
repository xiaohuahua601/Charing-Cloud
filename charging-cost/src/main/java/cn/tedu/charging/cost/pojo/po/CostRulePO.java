package cn.tedu.charging.cost.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * 计价规则的PO
 * @Author: goodman
 * @Date: 2025/3/3 20:16
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@TableName("charging_cost_rule")
public class CostRulePO {


    @TableId(type = IdType.AUTO)
    Integer id;

    /**
     * 站点id
     */
    Integer stationId;

    /**
     * 枪类型
     * 1：快充  2：慢充 电费不一样
     */
    Integer gunType;

    /**
     * 名称
     */
    String name;

    /**
     * 开始时间
     */
    Integer startTime;

    /**
     * 结束时间
     */
    Integer endTime;

    /**
     *
     *  电费
     * BigDecimal java提供的工具类 更加方便的进行计算
     */
    BigDecimal powerFee;

    /**
     * 服务费
     */
    BigDecimal serviceFee;

}
