package com.example.datasource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 动态数据源启动类
 */
@SpringBootApplication
@MapperScan("com.example.datasource.mapper")
public class DynamicDatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicDatasourceApplication.class, args);
        System.out.println("\n========================================\n" +
                " Spring Boot 动态数据源 Demo 启动成功!\n" +
                " 访问地址: http://localhost:8080\n\n" +
                " 测试接口:\n" +
                " - GET  /api/users/mysql       - 从 MySQL 获取所有用户\n" +
                " - GET  /api/users/oracle      - 从 Oracle 获取所有用户\n" +
                " - POST /api/users/mysql       - 保存用户到 MySQL\n" +
                " - POST /api/users/oracle      - 保存用户到 Oracle\n" +
                "========================================\n");
    }
}