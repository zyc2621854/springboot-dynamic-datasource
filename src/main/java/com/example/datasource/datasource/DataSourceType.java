package com.example.datasource.datasource;

/**
 * 数据源类型枚举
 */
public enum DataSourceType {

    /**
     * MySQL 数据源
     */
    MYSQL("mysql"),

    /**
     * Oracle 数据源
     */
    ORACLE("oracle");

    private final String name;

    DataSourceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}