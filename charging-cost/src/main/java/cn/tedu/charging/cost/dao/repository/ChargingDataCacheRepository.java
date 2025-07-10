package cn.tedu.charging.cost.dao.repository;

import cn.tedu.charging.cost.pojo.po.ChargingData;

/**
 *
 * 操作 充电数据到 Redis的数据层
 * * @Author: goodman
 * @Date: 2025/3/6 19:58
 */
public interface ChargingDataCacheRepository {
    ChargingData getChargingDataByOrderNo(String orderNo);

    void saveChargingData(String orderNo, ChargingData chargingData);
}
