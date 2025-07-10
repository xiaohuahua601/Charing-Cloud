package cn.tedu.charging.cost.dao.repository.impl;

import cn.tedu.charging.cost.dao.mapper.CostRuleMapper;
import cn.tedu.charging.cost.dao.repository.CostRuleRepository;
import cn.tedu.charging.cost.pojo.po.CostRulePO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: goodman
 * @Date: 2025/3/3 20:22
 */
@Repository
public class CostRuleRepositoryImpl implements CostRuleRepository {

    @Autowired
    private CostRuleMapper costRuleMapper;

    @Override
    public List<CostRulePO> getCostRuleListByStationId(Integer stationId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("station_id", stationId);
        //where station_id = stationId
        return costRuleMapper.selectList(queryWrapper);
    }
}
