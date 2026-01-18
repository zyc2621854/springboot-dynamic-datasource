# Spring Boot 动态数据源切换 Demo

这是一个基于 Spring Boot 的动态数据源切换示例项目,支持通过注解方式在 Oracle 和 MySQL 数据库之间动态切换。

## 技术栈

- Spring Boot 2.7.18
- MyBatis Plus 3.5.3.1
- MySQL 8.x
- Oracle (任意版本)
- HikariCP 连接池
- Spring AOP
- Lombok

## 项目结构

```
springboot-dynamic-datasource/
├── src/main/java/com/example/datasource/
│   ├── config/                 # 配置类
│   │   ├── DataSourceConfig.java      # 数据源配置
│   │   └── MybatisPlusConfig.java      # MyBatis Plus 配置
│   ├── datasource/              # 数据源相关
│   │   ├── DynamicDataSource.java       # 动态数据源
│   │   ├── DataSourceAspect.java        # 数据源切换切面
│   │   ├── DataSourceContextHolder.java # 数据源上下文持有者
│   │   ├── DataSourceType.java         # 数据源类型枚举
│   │   └── DataSource.java              # 数据源注解
│   ├── entity/                  # 实体类
│   │   └── User.java
│   ├── mapper/                  # Mapper 接口
│   │   └── UserMapper.java
│   ├── service/                 # Service 层
│   │   ├── UserService.java
│   │   └── impl/
│   │       └── UserServiceImpl.java
│   ├── controller/              # Controller 层
│   │   └── UserController.java
│   └── DynamicDatasourceApplication.java  # 启动类
├── src/main/resources/
│   ├── application.yml           # 配置文件
│   └── mapper/                  # Mapper XML (可选)
├── mysql-init.sql              # MySQL 数据库初始化脚本
├── oracle-init.sql             # Oracle 数据库初始化脚本
└── pom.xml                     # Maven 配置
```

## 快速开始

### 1. 配置数据库

#### MySQL 配置

在 `application.yml` 中修改 MySQL 连接信息:

```yaml
spring:
  datasource:
    mysql:
      url: jdbc:mysql://localhost:3306/test_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
      username: root
      password: your_password
```

执行 `mysql-init.sql` 初始化数据库:

```bash
mysql -u root -p < mysql-init.sql
```

#### Oracle 配置

在 `application.yml` 中修改 Oracle 连接信息:

```yaml
spring:
  datasource:
    oracle:
      url: jdbc:oracle:thin:@localhost:1521:orcl
      username: system
      password: your_password
```

执行 `oracle-init.sql` 初始化数据库:

```bash
sqlplus system/password@localhost:1521/orcl @ oracle-init.sql
```

### 2. 构建项目

```bash
mvn clean package
```

### 3. 运行项目

```bash
mvn spring-boot:run
```

或直接运行 jar 包:

```bash
java -jar target/springboot-dynamic-datasource-1.0.0.jar
```

### 4. 访问测试

启动成功后,访问以下接口测试:

- **GET** `http://localhost:8080/api/users/mysql` - 从 MySQL 获取所有用户
- **GET** `http://localhost:8080/api/users/oracle` - 从 Oracle 获取所有用户
- **GET** `http://localhost:8080/api/users/mysql/page?page=1&size=5` - MySQL 分页查询
- **GET** `http://localhost:8080/api/users/oracle/page?page=1&size=5` - Oracle 分页查询
- **POST** `http://localhost:8080/api/users/mysql` - 保存用户到 MySQL
- **POST** `http://localhost:8080/api/users/oracle` - 保存用户到 Oracle

## 使用说明

### 1. 使用 @DataSource 注解切换数据源

在 Service 方法上使用 `@DataSource` 注解指定使用的数据源:

```java
@Service
public class UserServiceImpl implements UserService {

    @Override
    @DataSource(DataSourceType.MYSQL)  // 使用 MySQL 数据库
    public List<User> getUsersFromMysql() {
        return userMapper.selectList(null);
    }

    @Override
    @DataSource(DataSourceType.ORACLE)  // 使用 Oracle 数据库
    public List<User> getUsersFromOracle() {
        return userMapper.selectList(null);
    }
}
```

### 2. 数据源类型

支持两种数据源类型:

- `DataSourceType.MYSQL` - MySQL 数据库
- `DataSourceType.ORACLE` - Oracle 数据库

### 3. 工作原理

1. 当调用带有 `@DataSource` 注解的方法时,`DataSourceAspect` 切面会拦截该方法
2. 从注解中获取数据源类型,存入 `ThreadLocal`
3. `DynamicDataSource` 从 `ThreadLocal` 中读取数据源类型,返回对应的数据源连接
4. 方法执行完成后,清理 `ThreadLocal` 中的数据源类型

## 测试示例

### 使用 curl 测试

```bash
# 从 MySQL 获取所有用户
curl http://localhost:8080/api/users/mysql

# 从 Oracle 获取所有用户
curl http://localhost:8080/api/users/oracle

# 保存用户到 MySQL
curl -X POST http://localhost:8080/api/users/mysql \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456","email":"test@example.com","age":20}'

# 保存用户到 Oracle
curl -X POST http://localhost:8080/api/users/oracle \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456","email":"test@example.com","age":20}'
```

## 注意事项

1. **ThreadLocal 清理**: 切面会自动清理 `ThreadLocal`,确保不会影响后续请求
2. **事务管理**: 如需在带有 `@DataSource` 注解的方法上使用 `@Transactional`,需要确保事务管理器使用的是 `DynamicDataSource`
3. **数据库差异**: Oracle 和 MySQL 在 SQL 语法上有差异(如分页、序列等),本 demo 使用 MyBatis Plus 的分页插件处理部分差异
4. **连接池**: 每个数据源使用独立的 HikariCP 连接池,需要根据实际需求调整连接池配置

## 核心组件

- **DynamicDataSource**: 继承 `AbstractRoutingDataSource`,实现数据源动态路由
- **DataSourceAspect**: AOP 切面,拦截 `@DataSource` 注解,切换数据源
- **DataSourceContextHolder**: 使用 `ThreadLocal` 管理当前线程的数据源类型
- **@DataSource**: 自定义注解,用于标记使用哪个数据源

## 许可证

MIT License