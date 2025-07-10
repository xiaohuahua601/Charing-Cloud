package cn.tedu.charging.user.controller;

import cn.tedu.charging.common.pojo.JsonResult;
import cn.tedu.charging.common.pojo.vo.UserInfoVO;
import cn.tedu.charging.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: goodman
 * @Date: 2025/2/23 21:21
 */
@Slf4j
@RestController
@RequestMapping("user")
@Api(tags = "用户服务")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("获取用户信息")
    @GetMapping("/info/{userId}")
    public JsonResult<UserInfoVO> getUserInfo(@PathVariable("userId") Integer userId) {
        log.debug("获取用户信息入参:id:{}",userId);
        UserInfoVO userInfoVO = userService.getUserInfo(userId);
        JsonResult result = JsonResult.ok(userInfoVO);
        log.debug("获取用户信息入参:id:{},出参:{}",userId,result);
        return result;
    }


}
