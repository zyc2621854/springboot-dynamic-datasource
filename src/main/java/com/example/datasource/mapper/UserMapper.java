package com.example.datasource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datasource.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 * 继承 MyBatis Plus 的 BaseMapper,自动获得 CRUD 方法
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}