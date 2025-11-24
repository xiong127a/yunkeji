package org.yun.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.yun.dao.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}