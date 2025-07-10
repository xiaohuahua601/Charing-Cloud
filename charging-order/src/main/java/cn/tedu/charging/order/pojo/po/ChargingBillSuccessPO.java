package cn.tedu.charging.order.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 *  成功订单记录的PO
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@TableName("charging_bill_succes")
public class ChargingBillSuccessPO {

    @TableId(type=IdType.AUTO)
    Integer id;

    /**
     * 订单号
     */
    String billId;

    /**
     * 用户id
     */
    Integer userId;

    /**
     * 车辆id
     */
    Integer vehicleId;

    /**
     * 充电桩id
     */
    Integer gunId;

    /**
     *  订单状态
     *  1 充电中
     *  2 充电完成
     */
    Integer billStatus;
}
