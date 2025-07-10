package cn.tedu.charging.cost.dao.repository.impl;

import cn.tedu.charging.cost.dao.repository.CostRuleCacheRepository;
import cn.tedu.charging.cost.dao.repository.CostRuleRepository;
import cn.tedu.charging.cost.pojo.po.ChargingData;
import cn.tedu.charging.cost.pojo.po.CostRulePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * @Author: goodman
 * @Date: 2025/3/6 21:10
 */
@Repository
public class CostRuleCacheRepositoryImpl implements CostRuleCacheRepository {

    @Autowired
    private RedisTemplate<String, List<CostRulePO>> redisTemplate;

    private static final String COST_RULE_KEY = "cost_rule_";


    @Override
    public List<CostRulePO> getCostRuleListByStationId(Integer stationId) {
        //用Redis种String类型来计价规则数据
        ValueOperations<String, List<CostRulePO>> valueOperations = redisTemplate.opsForValue();
        //拼接key cost_rule_stationId
        // cost_rule_5
        // cost_rule_6
        //每一个场站一个计价规则 之间不影响
        String key = COST_RULE_KEY + stationId;
        //通过 valueOperations.get  获取计价规则
        return valueOperations.get(key);
    }

    @Override
    public void saveCostRules(Integer stationId, List<CostRulePO> dbCostRules) {
        //用Redis种String类型来计价规则数据
        ValueOperations<String, List<CostRulePO>> valueOperations = redisTemplate.opsForValue();
        //拼接key cost_rule_stationId
        // cost_rule_5
        // cost_rule_6
        //每一个场站一个计价规则 之间不影响
        String key = COST_RULE_KEY + stationId;
        //通过 valueOperations.set  保存计价规则
        valueOperations.set(key,dbCostRules);
    }
}
