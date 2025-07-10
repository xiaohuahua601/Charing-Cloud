package cn.tedu.charging.order.controller;


import cn.tedu.charging.common.pojo.JsonResult;
import cn.tedu.charging.order.pojo.param.OrderAddParam;
import cn.tedu.charging.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("order")
@RestController
@Api(tags = "订单服务")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping("/create")
    public JsonResult<String> create(@RequestBody OrderAddParam  orderAddParam) {
        log.debug("创建订单入参:{}",orderAddParam);
        String orderNo = orderService.create(orderAddParam);
        JsonResult result = JsonResult.ok(orderNo);
        log.debug("创建订单入参:{},出参:{}",orderAddParam,result);
        return result;
    }
}
