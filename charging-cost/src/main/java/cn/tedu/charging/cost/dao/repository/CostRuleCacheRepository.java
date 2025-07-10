package cn.tedu.charging.cost.dao.repository;

import cn.tedu.charging.cost.pojo.po.CostRulePO;

import java.util.List;

/**
 * @Author: goodman
 * @Date: 2025/3/3 20:22
 */
public interface CostRuleCacheRepository {
    List<CostRulePO> getCostRuleListByStationId(Integer stationId);

    void saveCostRules(Integer stationId, List<CostRulePO> dbCostRules);
}
