package cn.tedu.charging.device.service.impl;

import cn.tedu.charging.device.dao.repository.GunRepository;
import cn.tedu.charging.device.dao.repository.StationRepository;
import cn.tedu.charging.common.pojo.param.UpdateGunStatusParam;
import cn.tedu.charging.device.pojo.po.StationPO;
import cn.tedu.charging.device.pojo.vo.StationInfoVO;
import cn.tedu.charging.device.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService {


    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private GunRepository gunRepository;


    @Override
    public List<StationInfoVO> nearStation(Double longitude, Double latitude, Double radius) {
        List<StationPO> stationPOS  = stationRepository.nearStations(longitude,latitude,radius);
        List<StationInfoVO> result = new ArrayList<>();
        if(!CollectionUtils.isEmpty(stationPOS)) {
            for (StationPO stationPO : stationPOS) {
                StationInfoVO stationInfoVO = new StationInfoVO();
                //工具类 stationPO里的数据 copy 赋值 给 stationInfoVO
                BeanUtils.copyProperties(stationPO, stationInfoVO);
                stationInfoVO.setStationLng(Double.valueOf(stationPO.getStationLng().toEngineeringString()));
                stationInfoVO.setStationLat(Double.valueOf(stationPO.getStationLat().toEngineeringString()));
                result.add(stationInfoVO);
            }
        }
        return result;
    }

    @Override
    public StationInfoVO getStationInfoByStationId(Integer stationId) {
        log.debug("1 读取缓存");
        StationPO cacheStation = stationRepository.getStationInfoByCache(stationId);
        log.debug("2 判断缓存中是否有数据 是否命中");
        if (cacheStation == null) {
            log.debug("3 缓存中没有有数据 没有命中 从数据库查询");
            StationPO dbStation = stationRepository.getStationInfoByDB(stationId);
            if (dbStation != null) {
                log.debug("4 数据库不为空，保存数据库的数据到缓存 返回");
                stationRepository.saveStation2Cache(dbStation);
                StationInfoVO result = new StationInfoVO();
                BeanUtils.copyProperties(dbStation, result);
                return result;
            }
        }else {
            log.debug("5 缓存中有数据 命中 直接返回");
            StationInfoVO result = new StationInfoVO();
            BeanUtils.copyProperties(cacheStation, result);
            return result;
        }
        return null;
    }

    @Override
    public Boolean updateGunStatus(UpdateGunStatusParam param) {
        //通过操作数据库修改枪的状态
        gunRepository.updateGunStatus(param.getGunId(),param.getStatus());
        return true;
    }
}
