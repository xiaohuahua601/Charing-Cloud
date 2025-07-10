package cn.tedu.charging.order.dao.repository;

import cn.tedu.charging.order.pojo.po.ChargingBillFailPO;
import cn.tedu.charging.order.pojo.po.ChargingBillSuccessPO;

/**
 * @Author: goodman
 * @Date: 2025/2/25 21:05
 */
public interface ChargingBillRepository {
    Integer saveSuccessOrder(ChargingBillSuccessPO chargingBillSuccessPO);

    Integer saveFailOrder(ChargingBillFailPO chargingBillFailPO);

    ChargingBillSuccessPO getSuccessOrder(String orderNo);

    ChargingBillFailPO getFailOrder(String orderNo);

    Integer updateOrderStatusFromChargingToExceptionEnd(String orderNo);
}
