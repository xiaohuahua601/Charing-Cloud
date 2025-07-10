package cn.tedu.charging.order.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 失败订单记录
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@TableName("charging_bill_fail")
public class ChargingBillFailPO {

    @TableId(type= IdType.AUTO)
    Integer id;

    /**
     * 订单编号
     */
    String billId;

    /**
     * 失败原因
     */
    String failDesc;

}
