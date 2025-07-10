package cn.tedu.charging.user.dao.repository;

import cn.tedu.charging.user.pojo.po.UserInfoPO;
import cn.tedu.charging.user.pojo.po.UserPO;

/**
 * @Author: goodman
 * @Date: 2025/2/23 21:30
 */
public interface UserRepository {
    UserInfoPO getUserInfo(Integer userId);

    UserPO getUserInfoByOpenId(String openId);

    Integer saveUser(UserPO userPO);
}
