package cn.tedu.charging.user.controller;

import cn.tedu.charging.common.pojo.JsonResult;
import cn.tedu.charging.common.pojo.vo.UserInfoVO;
import cn.tedu.charging.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: goodman
 * @Date: 2025/3/14 19:41
 */
@Slf4j
@RestController
@RequestMapping("user")
@Api(tags = "登录服务")
public class LoginController {


    @Autowired
    private UserService userService;


    /**
     * /user/wx/login?code="+code+"&nickName="+this.nickName  queryString
     *
     * /user/wx/login/code/nickName
     *
     * @param code
     * @param nickName
     * @return
     */
    @ApiOperation("登录")
    @GetMapping("/wx/login")
    public JsonResult<Integer> getUserInfo(@RequestParam("code") String code,@RequestParam("nickName") String nickName) {
        log.debug("微信登录:code:{},nickName:{}",code,nickName);
        Integer userId = userService.login(code,nickName);
        return JsonResult.ok(userId);
    }

}
