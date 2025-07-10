package cn.tedu.charging.user.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @Author: goodman
 * @Date: 2025/2/23 21:28
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@TableName("charging_user_vehicle_bind")
public class UserInfoPO {

    Integer id;

    Integer userId;

    Integer vehicleId;

}
