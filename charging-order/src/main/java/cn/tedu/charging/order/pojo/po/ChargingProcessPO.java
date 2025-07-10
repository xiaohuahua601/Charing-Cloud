package cn.tedu.charging.order.pojo.po;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;

/**
 * ChargingProcessPO 用来和ES表 进行映射
 * 和mysql 表进行映射 类似
 * mybaits-plus @TableName("charging_bill_fail") 对象和具体的表进行映射
 * Es @Document(indexName = "charging_process") 对象和具体的表进行映射
 *
 *
 * @Author: goodman
 * @Date: 2025/3/9 21:14
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(indexName = "charging_process")
public class ChargingProcessPO {

    @Id
    Long id;

    /**
     * 订单号
     */
    String orderNo;

    /**
     * 用户id
     */
    Integer userId;

    /**
     * 车辆id
     */
    Integer vehicleId;

    /**
     * 充电站id
     */
    Integer stationId;

    /**
     * 充电枪id
     */
    Integer gunId;

    /**
     * 充电桩id
     */
    Integer pileId;

    /**
     * 充电量
     */
    Float chargingCapacity;

    /**
     * 总费用
     */
    BigDecimal totalCost;

}
