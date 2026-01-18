package com.example.datasource.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datasource.entity.User;

import java.util.List;

/**
 * 用户 Service 接口
 */
public interface UserService {

    /**
     * 从 MySQL 数据库获取所有用户
     *
     * @return 用户列表
     */
    List<User> getUsersFromMysql();

    /**
     * 从 Oracle 数据库获取所有用户
     *
     * @return 用户列表
     */
    List<User> getUsersFromOracle();

    /**
     * 从 MySQL 数据库分页查询用户
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    Page<User> getUsersFromMysqlByPage(int page, int size);

    /**
     * 从 Oracle 数据库分页查询用户
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    Page<User> getUsersFromOracleByPage(int page, int size);

    /**
     * 保存用户到 MySQL 数据库
     *
     * @param user 用户信息
     * @return 保存成功返回 true
     */
    boolean saveUserToMysql(User user);

    /**
     * 保存用户到 Oracle 数据库
     *
     * @param user 用户信息
     * @return 保存成功返回 true
     */
    boolean saveUserToOracle(User user);

    /**
     * 根据用户 ID 从 MySQL 数据库查询用户
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    User getUserByIdFromMysql(Long id);

    /**
     * 根据用户 ID 从 Oracle 数据库查询用户
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    User getUserByIdFromOracle(Long id);

    /**
     * 根据 ID 更新 MySQL 数据库中的用户信息
     *
     * @param user 用户信息
     * @return 更新成功返回 true
     */
    boolean updateUserToMysql(User user);

    /**
     * 根据 ID 更新 Oracle 数据库中的用户信息
     *
     * @param user 用户信息
     * @return 更新成功返回 true
     */
    boolean updateUserToOracle(User user);

    /**
     * 根据 ID 从 MySQL 数据库删除用户
     *
     * @param id 用户 ID
     * @return 删除成功返回 true
     */
    boolean deleteUserFromMysql(Long id);

    /**
     * 根据 ID 从 Oracle 数据库删除用户
     *
     * @param id 用户 ID
     * @return 删除成功返回 true
     */
    boolean deleteUserFromOracle(Long id);
}