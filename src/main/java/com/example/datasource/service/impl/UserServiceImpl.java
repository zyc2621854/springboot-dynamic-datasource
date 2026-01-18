package com.example.datasource.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datasource.datasource.DataSource;
import com.example.datasource.datasource.DataSourceType;
import com.example.datasource.entity.User;
import com.example.datasource.mapper.UserMapper;
import com.example.datasource.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户 Service 实现类
 * 使用 @DataSource 注解动态切换数据源
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    @DataSource(DataSourceType.MYSQL)
    public List<User> getUsersFromMysql() {
        return userMapper.selectList(null);
    }

    @Override
    @DataSource(DataSourceType.ORACLE)
    public List<User> getUsersFromOracle() {
        return userMapper.selectList(null);
    }

    @Override
    @DataSource(DataSourceType.MYSQL)
    public Page<User> getUsersFromMysqlByPage(int page, int size) {
        Page<User> userPage = new Page<>(page, size);
        return userMapper.selectPage(userPage, null);
    }

    @Override
    @DataSource(DataSourceType.ORACLE)
    public Page<User> getUsersFromOracleByPage(int page, int size) {
        Page<User> userPage = new Page<>(page, size);
        return userMapper.selectPage(userPage, null);
    }

    @Override
    @DataSource(DataSourceType.MYSQL)
    public boolean saveUserToMysql(User user) {
        return userMapper.insert(user) > 0;
    }

    @Override
    @DataSource(DataSourceType.ORACLE)
    public boolean saveUserToOracle(User user) {
        return userMapper.insert(user) > 0;
    }

    @Override
    @DataSource(DataSourceType.MYSQL)
    public User getUserByIdFromMysql(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    @DataSource(DataSourceType.ORACLE)
    public User getUserByIdFromOracle(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    @DataSource(DataSourceType.MYSQL)
    public boolean updateUserToMysql(User user) {
        return userMapper.updateById(user) > 0;
    }

    @Override
    @DataSource(DataSourceType.ORACLE)
    public boolean updateUserToOracle(User user) {
        return userMapper.updateById(user) > 0;
    }

    @Override
    @DataSource(DataSourceType.MYSQL)
    public boolean deleteUserFromMysql(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    @DataSource(DataSourceType.ORACLE)
    public boolean deleteUserFromOracle(Long id) {
        return userMapper.deleteById(id) > 0;
    }
}