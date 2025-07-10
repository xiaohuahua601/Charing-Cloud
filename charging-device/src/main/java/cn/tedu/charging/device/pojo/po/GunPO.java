package cn.tedu.charging.device.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @Author: goodman
 * @Date: 2025/2/23 21:08
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@TableName("charging_gun_info")
public class GunPO {

    Integer id;

    Integer gunStatus;
}
