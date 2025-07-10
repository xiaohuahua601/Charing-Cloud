package cn.tedu.charging.user.dao.repository.impl;

import cn.tedu.charging.user.dao.mapper.UserLoginMapper;
import cn.tedu.charging.user.dao.mapper.UserMapper;
import cn.tedu.charging.user.dao.repository.UserRepository;
import cn.tedu.charging.user.pojo.po.UserInfoPO;
import cn.tedu.charging.user.pojo.po.UserPO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @Author: goodman
 * @Date: 2025/2/23 21:30
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserLoginMapper userLoginMapper;


    @Override
    public UserInfoPO getUserInfo(Integer userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public UserPO getUserInfoByOpenId(String openId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("wx_open_id", openId);
        return userLoginMapper.selectOne(queryWrapper);
    }

    @Override
    public Integer saveUser(UserPO userPO) {
        Integer row = userLoginMapper.insert(userPO);
        //row 是影响行数 修改行数 插入一条 就返回1
        //保存成功后，plus 会自动填充 自增的id 到 userPO
        return userPO.getId();
    }
}
