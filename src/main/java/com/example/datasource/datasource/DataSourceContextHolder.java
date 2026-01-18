package com.example.datasource.datasource;

/**
 * 数据源上下文持有者
 * 使用 ThreadLocal 存储当前线程的数据源类型
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<DataSourceType> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前线程的数据源类型
     *
     * @param dataSourceType 数据源类型
     */
    public static void setDataSource(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            throw new IllegalArgumentException("数据源类型不能为空");
        }
        CONTEXT_HOLDER.set(dataSourceType);
    }

    /**
     * 获取当前线程的数据源类型
     *
     * @return 数据源类型
     */
    public static DataSourceType getDataSource() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除当前线程的数据源类型
     */
    public static void clearDataSource() {
        CONTEXT_HOLDER.remove();
    }
}