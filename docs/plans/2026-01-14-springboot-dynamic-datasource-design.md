# Spring Boot 动态数据源连接设计文档

## 概述

本设计实现了一个基于 Spring Boot 的动态数据源切换 demo,支持在 Oracle 和 MySQL 数据库之间通过注解方式切换连接。

## 目标

- 运行时动态切换 Oracle 和 MySQL 数据库
- 使用方法注解 `@DataSource` 触发数据库切换
- 实现基本的 CRUD 操作验证连接切换
- 使用 MyBatis 和 MyBatis Plus 作为 ORM 框架

## 架构设计

### 整体架构

采用经典的分层架构,核心是利用 Spring 的 `AbstractRoutingDataSource` 实现数据源的动态路由:

1. **数据源配置层** - 配置 Oracle 和 MySQL 两个独立的 DataSource,使用 `AbstractRoutingDataSource` 作为动态代理,内部维护一个 `ThreadLocal` 变量来存储当前线程的数据源标识。

2. **注解层** - 定义 `@DataSource` 注解,支持传入数据源名称(如"oracle"或"mysql")。该注解可以应用在 Service 层方法上,标记该方法应该使用哪个数据库。

3. **AOP 切面层** - 创建切面拦截带有 `@DataSource` 注解的方法,在方法执行前将数据源标识存入 `ThreadLocal`,方法执行后清理标识。这是切换数据库的核心机制。

4. **业务层** - 包含 UserRepository 接口和实现类,通过 MyBatis 和 MyBatis Plus 操作数据库。Service 层的方法使用 `@DataSource` 注解来指定使用哪个数据库,实现 CRUD 操作。

## 核心组件设计

### 1. DynamicDataSource

继承 `AbstractRoutingDataSource` 的核心路由数据源类。

**职责:**
- 重写 `determineCurrentLookupKey()` 方法
- 从 `ThreadLocal` 中获取当前数据源标识
- 内部维护 `Map<Object, Object> targetDataSources`,存储所有实际的数据源

### 2. DataSourceType (枚举)

定义支持的数据源类型:
- `ORACLE`: Oracle 数据库
- `MYSQL`: MySQL 数据库

### 3. DataSourceContextHolder (工具类)

使用 `ThreadLocal` 管理当前线程的数据源类型。

**方法:**
- `setDataSource(DataSourceType type)`: 设置当前数据源类型
- `getDataSource()`: 获取当前数据源类型
- `clearDataSource()`: 清理当前数据源类型

### 4. @DataSource (自定义注解)

用于标记 Service 方法,指定使用的数据源。

**属性:**
- `value()`: DataSourceType 类型,默认值为 MYSQL
- 作用域: METHOD 级别

### 5. DataSourceAspect (AOP 切面)

拦截带 `@DataSource` 注解的方法。

**职责:**
- 在 `@Around` 环绕通知中处理数据源切换
- 执行前: 调用 `DataSourceContextHolder.setDataSource()` 设置数据源
- 执行后: 调用 `DataSourceContextHolder.clearDataSource()` 清理数据源
- 确保即使方法抛出异常也能正确清理

### 6. MyBatis Plus 配置

使用 `MybatisPlusInterceptor` 配置分页插件,统一管理 Oracle 和 MySQL 的分页语法差异。创建 `SqlSessionFactory` 时指定动态数据源。

### 7. 数据库配置

在 `application.yml` 中配置两个数据源的连接信息:
- driver: 数据库驱动
- url: 数据库连接 URL
- username: 用户名
- password: 密码

使用 `@ConfigurationProperties` 绑定到配置类。

## 数据流设计

### 请求执行流程

1. **请求进入** → Controller 接收 HTTP 请求,调用 Service 层方法

2. **AOP 拦截** → DataSourceAspect 检测到方法上有 `@DataSource` 注解
   - 从注解中获取指定的数据源类型(ORACLE 或 MYSQL)
   - 调用 `DataSourceContextHolder.setDataSource()` 将类型存入 ThreadLocal

3. **数据源路由** → Service 方法调用 MyBatis Plus 的 Mapper 方法
   - MyBatis 创建 SqlSession 时,会通过 DynamicDataSource 获取连接
   - DynamicDataSource 调用 `determineCurrentLookupKey()` 从 ThreadLocal 读取数据源类型
   - 根据返回的 key,从 `targetDataSources` Map 中获取对应的真实 DataSource
   - 返回正确的数据库连接

4. **SQL 执行** → MyBatis Plus 执行 SQL 语句
   - 如果是分页查询,通过 `PaginationInnerInterceptor` 自动添加分页 SQL
   - 根据 Mapper 接口类型,使用对应的数据库语法

5. **结果返回** → 查询结果封装成对象返回给 Service 层

6. **AOP 清理** → DataSourceAspect 的 `@Around` 通知执行完毕
   - 调用 `DataSourceContextHolder.clearDataSource()` 清理 ThreadLocal
   - 避免线程复用时数据源标识残留

### 关键点

- 整个切换过程对 Service 层完全透明
- 数据源类型绑定在当前线程,保证并发安全
- ThreadLocal 清理是必须的,否则会导致数据源切换失效

## 实现步骤

### 步骤 1: 项目初始化

创建新项目目录 `springboot-dynamic-datasource`,添加 Spring Boot 依赖:
- `spring-boot-starter-web`
- `mybatis-plus-boot-starter`
- `mysql-connector-j`
- `ojdbc8` (Oracle 驱动)
- `lombok`
- `spring-boot-starter-aop`

### 步骤 2: 配置数据源

在 `application.yml` 中配置 Oracle 和 MySQL 连接信息,创建 `DataSourceProperties` 配置类绑定配置。

### 步骤 3: 实现核心组件

- 创建 `DataSourceType` 枚举
- 创建 `DataSourceContextHolder` 工具类
- 创建 `DynamicDataSource` 继承 `AbstractRoutingDataSource`
- 创建 `@DataSource` 注解
- 创建 `DataSourceAspect` 切面

### 步骤 4: 配置 MyBatis Plus

创建 `MybatisPlusConfig` 配置类:
- 配置 `SqlSessionFactory` 使用 DynamicDataSource
- 添加分页插件 `PaginationInnerInterceptor`
- 配置类型别名和 Mapper 扫描路径

### 步骤 5: 创建实体和 Mapper

- 创建 `User` 实体类(使用 `@TableName` 指定表名)
- 创建 `UserMapper` 接口继承 `BaseMapper<User>`

### 步骤 6: 创建 Service 层

- 创建 `UserService` 接口,定义 CRUD 方法
- 创建 `UserServiceImpl` 实现类,使用 `@DataSource` 注解标记方法

### 步骤 7: 创建 Controller 层

创建 `UserController`,提供 REST API 端点测试不同数据库的 CRUD 操作

### 步骤 8: 准备测试数据

在 Oracle 和 MySQL 中分别创建 user 表,添加测试数据

## 技术栈

- Spring Boot 2.7.18
- MyBatis Plus 3.5.3.1
- MySQL 8.x
- Oracle (任意版本,使用 ojdbc8 驱动)
- Spring AOP
- Lombok

## 项目结构

```
springboot-dynamic-datasource/
├── src/main/java/
│   ├── com/example/datasource/
│   │   ├── config/          # 配置类
│   │   │   ├── DataSourceConfig.java
│   │   │   └── MybatisPlusConfig.java
│   │   ├── datasource/      # 数据源相关
│   │   │   ├── DynamicDataSource.java
│   │   │   ├── DataSourceAspect.java
│   │   │   ├── DataSourceContextHolder.java
│   │   │   ├── DataSourceType.java
│   │   │   └── DataSource.java (注解)
│   │   ├── entity/          # 实体类
│   │   │   └── User.java
│   │   ├── mapper/          # Mapper 接口
│   │   │   └── UserMapper.java
│   │   ├── service/         # Service 层
│   │   │   ├── UserService.java
│   │   │   └── impl/
│   │   │       └── UserServiceImpl.java
│   │   └── controller/      # Controller 层
│   │       └── UserController.java
├── src/main/resources/
│   ├── application.yml       # 配置文件
│   └── mapper/              # Mapper XML (可选)
└── pom.xml                  # Maven 配置
```

## 注意事项

1. **ThreadLocal 清理**: 必须在方法执行完成后清理 ThreadLocal,否则会导致数据源切换失效

2. **并发安全**: 数据源类型绑定在当前线程,天然保证并发安全

3. **事务管理**: 如需在切换数据源的方法上使用 `@Transactional`,需要确保事务管理器使用的是 DynamicDataSource

4. **数据库差异**: Oracle 和 MySQL 在 SQL 语法上有差异(如分页、序列等),MyBatis Plus 的分页插件可以处理部分差异

5. **连接池**: 建议为每个数据源配置独立的连接池(如 HikariCP)
