package cn.tedu.charging.user.service.impl;

import cn.tedu.charging.common.utils.JsonUtils;
import cn.tedu.charging.user.dao.repository.UserRepository;
import cn.tedu.charging.user.pojo.WxLoginData;
import cn.tedu.charging.user.pojo.po.UserInfoPO;
import cn.tedu.charging.common.pojo.vo.UserInfoVO;
import cn.tedu.charging.user.pojo.po.UserPO;
import cn.tedu.charging.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: goodman
 * @Date: 2025/2/23 21:27
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserInfoVO getUserInfo(Integer userId) {
        UserInfoPO userInfoPO = userRepository.getUserInfo(userId);
        if (userInfoPO != null) {
            UserInfoVO userInfoVO = new UserInfoVO();
            BeanUtils.copyProperties(userInfoPO, userInfoVO);
            return userInfoVO;
        }
        return null;
    }

    @Override
    public Integer login(String code, String nickName)  {
        //调用微信的API 通过code 获取 用户在微信平台的唯一标识 openId
        String openId = getOpenId(code);
        if (openId != null) {
            //通过openId 去数据库查询用户信息
            UserPO userPO =  userRepository.getUserInfoByOpenId(openId);
            if (userPO == null) {
                //用户信息为空，表示用户第一次使用我们的系统，
                // 把用户信息注册用户 把用户的昵称和openId保存到数据库中
                userPO = new UserPO();
                userPO.setWxOpenId(openId);
                userPO.setNickName(nickName);
                //保存 类似于注册功能
                return userRepository.saveUser(userPO);
            }else {
                //不为空，表示用户已经使用过我们的系统，直接返回用户id
                return userPO.getId();
            }
        }
        return -1;
    }

    /**
     * appId 替换成你的appId ,应该放到配置文件里
     */
    private String appId = "wxf7546f9b87546078";

    /**
     * 小程序密钥 替换成你的AppSecret  应该放到配置文件里
     */
    private String appSecret = "439f1504f2bf53ae5d3dcbb5657bc7e1";


    @Autowired
    private RestTemplate restTemplate;


    /**
     * 业务服务器通过code + 仅保存在服务器的appsecret参数，
     * 向：微信开放平台接口发起网络请求详情。
     * @param code
     * @return
     */
    private String getOpenId(String code) {
        //通过微信的接口地址和appidAppID(小程序ID)和 appSecret AppSecret(小程序密钥)
        String url = "https://api.weixin.qq.com/sns/jscode2session?" +
                "appid=" + appId +
                "&secret=" + appSecret +
                "&js_code=" + code +
                "&grant_type=authorization_code";
        String jsonData =
                restTemplate.getForObject(url, String.class);
        WxLoginData wxLoginData =
                JsonUtils.fromJson(jsonData, WxLoginData.class);
        log.debug("微信返回的数据:{}", wxLoginData);
        if (wxLoginData != null) {
            return wxLoginData.getOpenid();
        }
        return null;
    }
}
