package cn.tedu.charging.user.dao.mapper;

import cn.tedu.charging.user.pojo.po.UserInfoPO;
import cn.tedu.charging.user.pojo.po.UserPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: goodman
 * @Date: 2025/2/23 21:28
 */
@Mapper
public interface UserLoginMapper extends BaseMapper<UserPO> {
}
