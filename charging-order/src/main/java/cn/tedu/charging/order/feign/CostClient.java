package cn.tedu.charging.order.feign;

import cn.tedu.charging.common.pojo.JsonResult;
import cn.tedu.charging.common.pojo.param.ChargingProcessParam;
import cn.tedu.charging.common.pojo.vo.ChargingProcessVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 订单服务 调用 计价服务的 客户端
 * 基于open-feign 做服务之间的通信
 * @Author: goodman
 * @Date: 2025/3/7 20:18
 */
@FeignClient("charging-cost")
public interface CostClient {

    @PostMapping("/cost/calculateCost")
    JsonResult<ChargingProcessVO> calculateCost(@RequestBody ChargingProcessParam chargingProcessParam);

}
