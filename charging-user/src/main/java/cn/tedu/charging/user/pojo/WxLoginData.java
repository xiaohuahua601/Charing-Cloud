package cn.tedu.charging.user.pojo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WxLoginData {

    String session_key;
    /**
     * 微信用户在微信平台的唯一标识
     */
    String openid;
}
