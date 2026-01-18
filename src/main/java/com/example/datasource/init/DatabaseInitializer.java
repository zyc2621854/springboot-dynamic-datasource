package com.example.datasource.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * 数据库初始化器 - 应用启动时自动创建 user 表
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    @Autowired
    @Qualifier("oracleDataSource")
    private DataSource oracleDataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========================================");
        System.out.println(" 正在初始化数据库表...");
        System.out.println("========================================");

        // 初始化 MySQL 数据库
        initMysqlDatabase();

        // 初始化 Oracle 数据库
        initOracleDatabase();

        System.out.println("========================================");
        System.out.println(" 数据库表初始化完成!");
        System.out.println("========================================");
    }

    /**
     * 初始化 MySQL 数据库
     */
    private void initMysqlDatabase() {
        try (Connection conn = mysqlDataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("正在初始化 MySQL 数据库...");

            // 创建 T_USER 表
            String createTableSql = "CREATE TABLE IF NOT EXISTS `T_USER` (" +
                    "`id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID'," +
                    "`username` VARCHAR(50) NOT NULL COMMENT '用户名'," +
                    "`password` VARCHAR(100) NOT NULL COMMENT '密码'," +
                    "`email` VARCHAR(100) COMMENT '邮箱'," +
                    "`phone` VARCHAR(20) COMMENT '手机号'," +
                    "`age` INT COMMENT '年龄'," +
                    "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                    "PRIMARY KEY (`id`)," +
                    "UNIQUE KEY `uk_username` (`username`)," +
                    "KEY `idx_email` (`email`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表'";

            stmt.execute(createTableSql);

            // 检查是否已有数据，如果没有则插入测试数据
            int count = 0;
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM T_USER");
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();

            if (count == 0) {
                System.out.println("MySQL T_USER 表为空，正在插入测试数据...");
                String insertSql = "INSERT INTO `T_USER` (`username`, `password`, `email`, `phone`, `age`) VALUES " +
                        "('zhangsan', '123456', 'zhangsan@example.com', '13800138001', 25)," +
                        "('lisi', '123456', 'lisi@example.com', '13800138002', 30)," +
                        "('wangwu', '123456', 'wangwu@example.com', '13800138003', 28)," +
                        "('zhaoliu', '123456', 'zhaoliu@example.com', '13800138004', 35)," +
                        "('qianqi', '123456', 'qianqi@example.com', '13800138005', 22)";
                stmt.execute(insertSql);
                System.out.println("MySQL 测试数据插入完成!");
            } else {
                System.out.println("MySQL T_USER 表已有 " + count + " 条数据，跳过插入测试数据");
            }

            System.out.println("MySQL 数据库初始化完成!");

        } catch (Exception e) {
            System.err.println("MySQL 数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 初始化 Oracle 数据库
     */
    private void initOracleDatabase() {
        try (Connection conn = oracleDataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("正在初始化 Oracle 数据库...");

            // 删除已存在的旧表（如果存在）
            try {
                stmt.execute("DROP TABLE \"user\"");
            } catch (Exception e) {
                // 表不存在，忽略错误
            }

            // 删除已存在的新表（如果存在）
            try {
                stmt.execute("DROP TABLE \"T_USER\"");
            } catch (Exception e) {
                // 表不存在，忽略错误
            }

            // 创建 T_USER 表
            String createTableSql = "CREATE TABLE \"T_USER\" (" +
                    "id NUMBER(20) NOT NULL," +
                    "username VARCHAR2(50) NOT NULL," +
                    "password VARCHAR2(100) NOT NULL," +
                    "email VARCHAR2(100)," +
                    "phone VARCHAR2(20)," +
                    "age NUMBER(3)," +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (id))";

            stmt.execute(createTableSql);

            // 创建自增序列
            try {
                stmt.execute("DROP SEQUENCE \"T_USER_SEQ\"");
            } catch (Exception e) {
                // 序列不存在，忽略错误
            }

            stmt.execute("CREATE SEQUENCE \"T_USER_SEQ\" " +
                    "INCREMENT BY 1 " +
                    "START WITH 1 " +
                    "NOMAXVALUE " +
                    "NOCACHE");

            // 创建触发器实现自增
            String createTriggerSql = "CREATE OR REPLACE TRIGGER \"T_USER_TRIGGER\" " +
                    "BEFORE INSERT ON \"T_USER\" " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "SELECT \"T_USER_SEQ\".NEXTVAL INTO :NEW.id FROM dual; " +
                    "END;";
            stmt.execute(createTriggerSql);

            // 创建唯一索引
            try {
                stmt.execute("DROP INDEX \"uk_username\" ON \"T_USER\"");
            } catch (Exception e) {
                // 索引不存在，忽略错误
            }
            try {
                stmt.execute("CREATE UNIQUE INDEX \"uk_username\" ON \"T_USER\"(username)");
            } catch (Exception e) {
                // 索引可能已存在，忽略错误
            }

            // 创建普通索引
            try {
                stmt.execute("DROP INDEX \"idx_email\" ON \"T_USER\"");
            } catch (Exception e) {
                // 索引不存在，忽略错误
            }
            try {
                stmt.execute("CREATE INDEX \"idx_email\" ON \"T_USER\"(email)");
            } catch (Exception e) {
                // 索引可能已存在，忽略错误
            }

            // 插入测试数据
            String[] insertStatements = {
                    "INSERT INTO \"T_USER\" (id, username, password, email, phone, age) VALUES (1, 'tom', '123456', 'tom@example.com', '13900139001', 26)",
                    "INSERT INTO \"T_USER\" (id, username, password, email, phone, age) VALUES (2, 'jerry', '123456', 'jerry@example.com', '13900139002', 32)",
                    "INSERT INTO \"T_USER\" (id, username, password, email, phone, age) VALUES (3, 'mike', '123456', 'mike@example.com', '13900139003', 29)",
                    "INSERT INTO \"T_USER\" (id, username, password, email, phone, age) VALUES (4, 'jack', '123456', 'jack@example.com', '13900139004', 38)",
                    "INSERT INTO \"T_USER\" (id, username, password, email, phone, age) VALUES (5, 'rose', '123456', 'rose@example.com', '13900139005', 24)"
            };

            for (String sql : insertStatements) {
                stmt.execute(sql);
            }

            System.out.println("Oracle 数据库初始化完成!");

        } catch (Exception e) {
            System.err.println("Oracle 数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}