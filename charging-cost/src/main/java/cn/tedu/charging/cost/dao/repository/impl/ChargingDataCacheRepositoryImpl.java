package cn.tedu.charging.cost.dao.repository.impl;

import cn.tedu.charging.cost.dao.repository.ChargingDataCacheRepository;
import cn.tedu.charging.cost.pojo.po.ChargingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

/**
 * @Author: goodman
 * @Date: 2025/3/6 19:59
 */
@Repository
public class ChargingDataCacheRepositoryImpl implements ChargingDataCacheRepository {

    @Autowired
    private RedisTemplate<String,ChargingData> redisTemplate;

    private static final String CHARGING_DATA_KEY = "charging_data_";

    @Override
    public ChargingData getChargingDataByOrderNo(String orderNo) {
        //用Redis种String类型来存取充电数据
        ValueOperations<String, ChargingData> valueOperations = redisTemplate.opsForValue();
        //拼接key charging_data_订单号
        // charging_data_666
        // charging_data_777
        //每一个订单 一个充电数据相互之间不影响
        String key = CHARGING_DATA_KEY + orderNo;
        //通过 valueOperations.get  获取充电数据
        return valueOperations.get(key);
    }

    @Override
    public void saveChargingData(String orderNo, ChargingData chargingData) {
        //用Redis种String类型来存取充电数据
        ValueOperations<String, ChargingData> valueOperations = redisTemplate.opsForValue();
        //拼接key charging_data_订单号
        // charging_data_666
        // charging_data_777
        //每一个订单 一个充电数据相互之间不影响
        String key = CHARGING_DATA_KEY + orderNo;
        //通过 valueOperations.set  保存充电数据
        valueOperations.set(key,chargingData);
    }
}
