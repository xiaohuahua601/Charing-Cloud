package cn.tedu.charging.device.dao.repository.impl;

import cn.tedu.charging.device.dao.mapper.StationMapper;
import cn.tedu.charging.device.dao.repository.StationRepository;
import cn.tedu.charging.device.pojo.po.StationPO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: goodman
 * @Date: 2025/2/20 20:05
 */
@Slf4j
@Repository
public class StationRepositoryImpl implements StationRepository {


    //推荐定义一个常量
    private static final String STATION_KEY = "station_";

    @Autowired
    private StationMapper stationMapper;


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<StationPO> selectAllStation() {
        //查询所有 没有查询条件 null
        return stationMapper.selectList(null);
    }

    @Override
    public void saveAllStation2Cache(List<StationPO> stationPOS) {
        GeoOperations geoOperations = redisTemplate.opsForGeo();
        ValueOperations valueOperations = redisTemplate.opsForValue();
        for (StationPO stationPO : stationPOS) {
            Point point = new Point(stationPO.getStationLng().doubleValue(),
                    stationPO.getStationLat().doubleValue());
            //第三个参数是member 存的是场站的id 采用的是分开存的方案 位置信息存储到GEO 和 存的是场站的id
            geoOperations.add("stations", point, stationPO.getId());
            //场站的详情存储到另外的Redis数据类型 String类型
            valueOperations.set(STATION_KEY + stationPO.getId(),stationPO);
            //第一种方案 把 位置信息和场站详情存储到GEO 存到一起
            //geoOperations.add("stations", point, stationPO);
        }
    }

    @Override
    public List<StationPO> nearStations(Double longitude, Double latitude, Double distance) {
        GeoOperations<String,Integer> geoOperations = redisTemplate.opsForGeo();
        ValueOperations<String,StationPO> valueOperations = redisTemplate.opsForValue();
        //通过用户的入参 经纬度创建一个点
        Point point = new Point(longitude,latitude);
        //通过用户的入参 距离和距离的单位创建一个 距离对象
        Distance radius = new Distance(distance, Metrics.KILOMETERS);
        //通过点和距离对象创建一个圆
        Circle circle = new Circle(point, radius);
        //查询附近的场站 key  stations 入参  圆
        GeoResults<RedisGeoCommands.GeoLocation<Integer>>
                geoResults = geoOperations.radius("stations", circle);
        //遍历查询
        List<GeoResult<RedisGeoCommands.GeoLocation<Integer>>> content = geoResults.getContent();
        List<StationPO> result = new ArrayList<>();
        for (GeoResult<RedisGeoCommands.GeoLocation<Integer>> geoResult : content) {
            //name 表示的的是保存时的member。场站的id
            Integer name = geoResult.getContent().getName();
           log.debug("遍历位置结果：{}",name);
           //通过场站的id查询场站的详细信息
           StationPO stationPO = valueOperations.get(STATION_KEY + name);
           result.add(stationPO);
       }
        return result;
    }

    @Override
    public StationPO getStationInfoByCache(Integer stationId) {
        //opsForValue 是操作Redis String 数据类型的
        ValueOperations<String,StationPO> valueOperations = redisTemplate.opsForValue();
        //get String 类型 获取数据的方法 第1参数 key
        return valueOperations.get(STATION_KEY + stationId);
    }

    @Override
    public StationPO getStationInfoByDB(Integer stationId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", stationId);
        //select * from charging_station where id = stationId
        return stationMapper.selectOne(queryWrapper);
    }

    @Override
    public void saveStation2Cache(StationPO dbStation) {
        //opsForValue 是操作Redis String 数据类型的
        ValueOperations<String,StationPO> valueOperations = redisTemplate.opsForValue();
        //set String 类型 保存数据的方法 第1参数 key 第2个参数 是 value
        valueOperations.set(STATION_KEY + dbStation.getId(), dbStation);
    }
}
