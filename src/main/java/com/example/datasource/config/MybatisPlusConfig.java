package com.example.datasource.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.example.datasource.datasource.DataSourceContextHolder;
import com.example.datasource.datasource.DataSourceType;
import com.example.datasource.datasource.DynamicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * MyBatis Plus 配置类
 * 配置动态数据源和 MyBatis Plus 插件
 */
@Configuration
public class MybatisPlusConfig {

    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    @Autowired
    @Qualifier("oracleDataSource")
    private DataSource oracleDataSource;

    /**
     * 创建动态数据源
     */
    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        // 设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(mysqlDataSource);

        // 配置多数据源映射
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        targetDataSources.put(DataSourceType.MYSQL.getName(), mysqlDataSource);
        targetDataSources.put(DataSourceType.ORACLE.getName(), oracleDataSource);
        dynamicDataSource.setTargetDataSources(targetDataSources);

        return dynamicDataSource;
    }

    /**
     * 配置 SqlSessionFactory
     */
    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dynamicDataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);

        // 设置 MyBatis Plus 拦截器
        sqlSessionFactoryBean.setPlugins(mybatisPlusInterceptor());

        // 设置实体类别名包路径
        sqlSessionFactoryBean.setTypeAliasesPackage("com.example.datasource.entity");

        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 配置 SqlSessionTemplate
     */
    @Bean
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 配置 MyBatis Plus 拦截器
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加分页插件,支持多种数据库
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置数据库类型,这里使用 MySQL 作为默认类型
        // 实际使用时需要根据动态数据源切换
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        // 设置请求的页面大于最大页后操作,true 调回首页,false 继续请求
        paginationInnerInterceptor.setOverflow(false);
        // 单页分页条数限制,默认无限制
        paginationInnerInterceptor.setMaxLimit(500L);

        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;
    }
}