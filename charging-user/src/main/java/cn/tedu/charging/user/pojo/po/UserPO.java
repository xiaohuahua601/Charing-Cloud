package cn.tedu.charging.user.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @Author: goodman
 * @Date: 2025/3/14 20:18
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@TableName("charging_user_info")
public class UserPO {

    @TableId(type = IdType.AUTO)
    Integer id;

    String wxOpenId;

    String nickName;
}
