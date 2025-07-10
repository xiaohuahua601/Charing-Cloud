package cn.tedu.charging.cost.service;

import cn.tedu.charging.common.pojo.param.ChargingProcessParam;
import cn.tedu.charging.common.pojo.vo.ChargingProcessVO;

/**
 * @Author: goodman
 * @Date: 2025/3/2 21:34
 */
public interface CostService {
    ChargingProcessVO calculateCost(ChargingProcessParam chargingProcessParam);
}
