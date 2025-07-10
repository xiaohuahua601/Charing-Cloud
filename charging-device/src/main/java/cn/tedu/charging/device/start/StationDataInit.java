package cn.tedu.charging.device.start;

import cn.tedu.charging.device.dao.mapper.StationMapper;
import cn.tedu.charging.device.dao.repository.StationRepository;
import cn.tedu.charging.device.pojo.po.StationPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 缓存预热
 * 在项目启动的时候
 * 从数据库查询 场站的信息
 * 保存到Redis的GEO 数据类型
 */
@Slf4j
@Component
public class StationDataInit implements ApplicationRunner {

    @Autowired
    private StationRepository stationRepository;


    //@Autowired
    //private StationMapper stationMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("Redis预热程序开始运行");
        //1 从数据库查询 场站信息
        List<StationPO> stationPOS = stationRepository.selectAllStation();
        log.debug("从数据库查询场站信息：{}",stationPOS);
        //2 保存到RedisGEO
        stationRepository.saveAllStation2Cache(stationPOS);
        log.debug("保存到RedisGEO：{} 成功",stationPOS);
    }
}
