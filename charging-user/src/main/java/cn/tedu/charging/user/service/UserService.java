package cn.tedu.charging.user.service;

import cn.tedu.charging.common.pojo.vo.UserInfoVO;

/**
 * @Author: goodman
 * @Date: 2025/2/23 21:27
 */
public interface UserService {
    UserInfoVO getUserInfo(Integer userId);

    Integer login(String code, String nickName);
}
