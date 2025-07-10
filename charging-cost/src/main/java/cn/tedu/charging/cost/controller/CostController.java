package cn.tedu.charging.cost.controller;

import cn.tedu.charging.common.pojo.JsonResult;
import cn.tedu.charging.common.pojo.param.ChargingProcessParam;
import cn.tedu.charging.common.pojo.vo.ChargingProcessVO;
import cn.tedu.charging.cost.service.CostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: goodman
 * @Date: 2025/3/2 21:33
 */
@Slf4j
@RequestMapping("cost")
@RestController
@Api(tags = "计价服务")
public class CostController {

    @Autowired
    private CostService costService;

    /**
     * 设备同步充电进度数据到订单服务后
     * 订单服务 调用 计价服务 计算价格 获取充电的价格花费
     */
    @ApiOperation("计算价格")
    @PostMapping("/calculateCost")
    public JsonResult<ChargingProcessVO> calculateCost(@RequestBody ChargingProcessParam chargingProcessParam){
        log.debug("计算花费入参:{}",chargingProcessParam);
        ChargingProcessVO chargingProcessVO = costService.calculateCost(chargingProcessParam);
        JsonResult result = JsonResult.ok(chargingProcessVO);
        log.debug("计算花费入参入参:{},出参:{}",chargingProcessParam,result);
        return result;
    }

}
