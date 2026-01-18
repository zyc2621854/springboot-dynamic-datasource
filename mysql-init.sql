-- MySQL 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS test_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE test_db;

-- 创建 user 表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `age` INT COMMENT '年龄',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试数据
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `age`) VALUES
('zhangsan', '123456', 'zhangsan@example.com', '13800138001', 25),
('lisi', '123456', 'lisi@example.com', '13800138002', 30),
('wangwu', '123456', 'wangwu@example.com', '13800138003', 28),
('zhaoliu', '123456', 'zhaoliu@example.com', '13800138004', 35),
('qianqi', '123456', 'qianqi@example.com', '13800138005', 22);