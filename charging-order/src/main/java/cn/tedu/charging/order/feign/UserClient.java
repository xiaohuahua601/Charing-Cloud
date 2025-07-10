package cn.tedu.charging.order.feign;

import cn.tedu.charging.common.pojo.JsonResult;
import cn.tedu.charging.common.pojo.vo.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 订单服务访问用户服务的客户端
 * @Author: goodman
 * @Date: 2025/2/23 21:38
 */
@FeignClient("charging-user")
public interface UserClient {

    @GetMapping("/user/info/{userId}")
    JsonResult<UserInfoVO> getUserInfo(@PathVariable("userId") Integer userId);
}
