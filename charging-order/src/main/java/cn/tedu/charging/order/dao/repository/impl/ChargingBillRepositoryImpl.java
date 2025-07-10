package cn.tedu.charging.order.dao.repository.impl;

import cn.tedu.charging.order.common.OrderStatusConstants;
import cn.tedu.charging.order.dao.mapper.ChargingBillFailMapper;
import cn.tedu.charging.order.dao.mapper.ChargingBillSuccessMapper;
import cn.tedu.charging.order.dao.repository.ChargingBillRepository;
import cn.tedu.charging.order.pojo.po.ChargingBillFailPO;
import cn.tedu.charging.order.pojo.po.ChargingBillSuccessPO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @Author: goodman
 * @Date: 2025/2/25 21:05
 */
@Repository
public class ChargingBillRepositoryImpl  implements ChargingBillRepository {

    @Autowired
    private ChargingBillSuccessMapper chargingBillSuccessMapper;

    @Autowired
    private ChargingBillFailMapper chargingBillFailMapper;

    @Override
    public Integer saveSuccessOrder(ChargingBillSuccessPO chargingBillSuccessPO) {
        return chargingBillSuccessMapper.insert(chargingBillSuccessPO);
    }

    @Override
    public Integer saveFailOrder(ChargingBillFailPO chargingBillFailPO) {
        return chargingBillFailMapper.insert(chargingBillFailPO);
    }

    @Override
    public ChargingBillSuccessPO getSuccessOrder(String orderNo) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("bill_id", orderNo);
        return chargingBillSuccessMapper.selectOne(queryWrapper);
    }

    @Override
    public ChargingBillFailPO getFailOrder(String orderNo) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("bill_id", orderNo);
        return chargingBillFailMapper.selectOne(queryWrapper);
    }

    @Override
    public Integer updateOrderStatusFromChargingToExceptionEnd(String orderNo) {
        //应该完成的 没有完成 直接改为异常结束
        // update xxx set order_status = 异常结束 where order_status = 充电中 and bill_id = orderNo

        //要修改的目标值  update xxx set order_status = 异常结束
        ChargingBillSuccessPO chargingBillSuccessPO = new ChargingBillSuccessPO();
        //异常结束
        chargingBillSuccessPO.setBillStatus(OrderStatusConstants.ORDER_STATUS_EXCEPTION_END);

        //修改的条件  where order_status = 充电中 and bill_id = orderNo
        UpdateWrapper queryWrapper = new UpdateWrapper();
        queryWrapper.eq("bill_id", orderNo);
        queryWrapper.eq("bill_status", OrderStatusConstants.ORDER_STATUS_CHARGING_PROCESS);
        return chargingBillSuccessMapper.update(chargingBillSuccessPO,queryWrapper);
    }
}
