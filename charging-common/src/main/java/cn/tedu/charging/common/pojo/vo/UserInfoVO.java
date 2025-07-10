package cn.tedu.charging.common.pojo.vo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 获取用户车辆信息的出参
 * @Author: goodman
 * @Date: 2025/2/23 21:23
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoVO {

    /**
     * 车辆id
     */
    Integer vehicleId;
}
