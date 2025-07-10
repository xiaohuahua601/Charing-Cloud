package cn.tedu.charging.common.pojo.param;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 修改枪状态的入参
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateGunStatusParam {

    /**
     * 枪id
     */
    Integer gunId;

    /**
     * 状态
     * 空闲
     * 使用中
     * 故障
     */
    Integer status;


}
