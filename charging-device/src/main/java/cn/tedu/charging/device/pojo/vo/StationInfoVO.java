package cn.tedu.charging.device.pojo.vo;

import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 附近充电站接口返回的数据
 */
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class StationInfoVO {

    Integer id;

    String stationName;

    Double stationLng;

    Double stationLat;


}
