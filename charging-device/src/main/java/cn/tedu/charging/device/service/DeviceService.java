package cn.tedu.charging.device.service;

import cn.tedu.charging.common.pojo.param.UpdateGunStatusParam;
import cn.tedu.charging.device.pojo.vo.StationInfoVO;

import java.util.List;

/**
 * @Author: goodman
 * @Date: 2025/2/18 21:38
 */
public interface DeviceService {
    List<StationInfoVO> nearStation(Double longitude, Double latitude, Double radius);

    StationInfoVO getStationInfoByStationId(Integer stationId);

    Boolean updateGunStatus(UpdateGunStatusParam param);
}
