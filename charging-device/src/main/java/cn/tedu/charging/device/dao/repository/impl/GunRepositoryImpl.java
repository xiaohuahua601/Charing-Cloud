package cn.tedu.charging.device.dao.repository.impl;

import cn.tedu.charging.device.dao.mapper.GunMapper;
import cn.tedu.charging.device.dao.repository.GunRepository;
import cn.tedu.charging.device.pojo.po.GunPO;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: goodman
 * @Date: 2025/2/23 21:05
 */
@Service
public class GunRepositoryImpl implements GunRepository {


    @Autowired
    private GunMapper gunMapper;


    @Override
    public void updateGunStatus(Integer gunId, Integer status) {
        GunPO gunPO = new GunPO();
        //要修改的目标值 set status = status
        gunPO.setGunStatus(status);
        //更新的条件  where id =  gunId
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id",gunId);
        //update xxx set status = status where id =  gunId
        gunMapper.update(gunPO,updateWrapper);
    }
}
