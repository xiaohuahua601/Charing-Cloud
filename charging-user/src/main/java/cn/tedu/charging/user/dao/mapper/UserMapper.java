package cn.tedu.charging.user.dao.mapper;

import cn.tedu.charging.user.pojo.po.UserInfoPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: goodman
 * @Date: 2025/2/23 21:28
 */
@Mapper
public interface UserMapper extends BaseMapper<UserInfoPO> {
}
