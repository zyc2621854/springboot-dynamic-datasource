package com.example.datasource.datasource;

import java.lang.annotation.*;

/**
 * 数据源切换注解
 * 标记在 Service 方法上,指定该方法使用的数据源
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {

    /**
     * 数据源类型
     * 默认为 MySQL
     *
     * @return 数据源类型
     */
    DataSourceType value() default DataSourceType.MYSQL;
}