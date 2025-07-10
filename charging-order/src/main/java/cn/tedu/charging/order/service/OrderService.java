package cn.tedu.charging.order.service;


import cn.tedu.charging.order.pojo.param.OrderAddParam;

public interface OrderService {
    String create(OrderAddParam orderAddParam);
}
