package cn.tedu.charging.device.dao.repository;

import cn.tedu.charging.device.pojo.po.StationPO;
import cn.tedu.charging.device.pojo.vo.StationInfoVO;

import java.util.List;

/**
 * 数据仓库
 * 数据的操作层
 */
public interface StationRepository {

    List<StationPO> selectAllStation();

    void saveAllStation2Cache(List<StationPO> stationPOS);

    List<StationPO> nearStations(Double longitude, Double latitude, Double radius);

    StationPO getStationInfoByCache(Integer stationId);

    StationPO getStationInfoByDB(Integer stationId);

    void saveStation2Cache(StationPO dbStation);
}
