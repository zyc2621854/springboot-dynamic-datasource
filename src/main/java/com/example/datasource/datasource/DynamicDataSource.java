package com.example.datasource.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源
 * 继承 AbstractRoutingDataSource,通过 determineCurrentLookupKey 方法动态返回数据源 key
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType dataSourceType = DataSourceContextHolder.getDataSource();
        if (dataSourceType == null) {
            // 默认使用 MySQL
            return DataSourceType.MYSQL.getName();
        }
        return dataSourceType.getName();
    }
}