package cn.tedu.charging.order.feign;

import cn.tedu.charging.common.pojo.JsonResult;
import cn.tedu.charging.common.pojo.param.UpdateGunStatusParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 订单服务
 * 调用
 * 设备服务的客户端  Client
 * 是通过 open-feign 实现的
 *
 * @FeignClient("charging-device")
 * @FeignClient 表示定义一个feign的客户端
 * "charging-device" 表示 这个客户端要访问的 服务
 */
@FeignClient("charging-device")
public interface DeviceClient {

    /** 更新枪的状态
     * @param param
     * @return
     */
    @PostMapping("/device/station/gun/status/update")
    JsonResult<Boolean> updateGunStatus(@RequestBody UpdateGunStatusParam param);
}
