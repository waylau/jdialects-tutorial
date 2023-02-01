# jDialects配置、入门

从一个简单的例子入手。


## 初始化Maven项目

pom.xml配置如下：


```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>${maven.compiler.source}</maven.compiler.target>
    <maven-compiler-plugin.version>3.8.1</maven-compiler-plugin.version>
    <maven-surefire-plugin.version>2.22.2</maven-surefire-plugin.version>
    <maven-failsafe-plugin.version>2.22.2</maven-failsafe-plugin.version>
    <jdialects.version>5.0.13.jre8</jdialects.version>
    <dbcp2.version>2.9.0</dbcp2.version>
    <logback-classic.version>1.4.5</logback-classic.version>
    <junit-jupiter.version>5.9.2</junit-jupiter.version>
    <h2.version>2.1.214</h2.version>
</properties>
<build>
    <plugins>
        <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>${maven-compiler-plugin.version}</version>
        </plugin>
        <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>${maven-surefire-plugin.version}</version>
        </plugin>
        <plugin>
            <artifactId>maven-failsafe-plugin</artifactId>
            <version>${maven-failsafe-plugin.version}</version>
        </plugin>
    </plugins>
</build>
<dependencies>
    <!-- https://mvnrepository.com/artifact/com.github.drinkjava2/jdialects -->
    <dependency>
        <groupId>com.github.drinkjava2</groupId>
        <artifactId>jdialects</artifactId>
        <version>${jdialects.version}</version>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.apache.commons/commons-dbcp2 -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-dbcp2</artifactId>
        <version>${dbcp2.version}</version>
    </dependency>

    <!-- 日志相关 -->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>${logback-classic.version}</version>
    </dependency>

    <!-- H2Database memory database for unit test -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>${h2.version}</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>${junit-jupiter.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

严格来说，jDialects配置依赖于`com.github.drinkjava2/jdialects`，但为了方便测试，还是需要提前加入数据库连接池、日志、H2、JUnit5、JSON等框架。



## 启用日志

jDialects自带日志输出功能，不需要添加任何第三方库依赖。jDialects的日志功能默认是关闭的，调用Dialect.setGlobalAllowShowSql(true)可打开日志输出开关，所有经过jDialects生成或变换的SQL如DDL、分页、函数变换等SQL文本都将在日志里输出。setGlobalAllowShowSql()是一个全局静态开关，只应该被调用一次。


Dialect的部分方法没有日志，比如toAddColumnDDL、toDropColumnDDL，此时就用到了自己的引入的logback。

## logback配置

logback.xml配置如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <layout class="ch.qos.logback.classic.PatternLayout">
      <Pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} %line - %msg%n</Pattern>
    </layout>
  </appender>

  <root level="info">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```

## 示例

完整示例代码如下：



### JUnit 5的语法规则

本示例采用了JUnit 5的语法规则。


```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 与@BeforeAll搭配使用
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 指定方法的执行顺序
class CreateDDLTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    BasicDataSource ds = null;

    Dialect dialect = Dialect.H2Dialect;

    @BeforeAll
    void initData() {
        // 启用日志
        Dialect.setGlobalAllowShowSql(true);

        // 使用H2数据库
        ds = new BasicDataSource();// DataSource
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:test_db");
        ds.setUsername("sa");
        ds.setPassword("");
    }

    @AfterAll
    void deleteData() throws SQLException {
        if (ds != null) {
            ds.close();
        }
    }

    ...
```

在示例的开始阶段，需要初始化BasicDataSource和Dialect，因此，是放在了`@BeforeAll`注解的initData()方法里面。同样的，释放资源放在`@AfterAll`注解的deleteData()方法里面。

为了配合`@BeforeAll`注解，需要在测试类上面添加`@TestInstance(TestInstance.Lifecycle.PER_CLASS)`注解。

测试类上面的另外一个注解`@TestMethodOrder(MethodOrderer.OrderAnnotation.class)`是为了指定方法的按照顺序执行。其顺序定义在方法的`@Order`注解上。


### 使用H2内存数据库

为了方面测试，采用了内嵌在应用里面的H2内存数据库。

同时也选用了H2的方言“Dialect.H2Dialect”。


### 测试删表结构

toDropDDL方法用于生成删除表结构的DDL，用法如下：

```java
// 使用TableModel
TableModel t = new TableModel("user_t");

// 使用方言来删除表
String[] ddlArray = dialect.toDropDDL(t);

for (String ddl : ddlArray) {
    logger.info(ddl);
}
```


执行上述方法，控制台日志输出如下：

```
drop table if exists user_t CASCADE 
```


### 测试建表结构

toCreateDDL方法用于生成创建表结构的DDL，用法如下：

```java
// 使用TableModel
TableModel t = new TableModel("user_t");
t.column("first_name").VARCHAR(20).pkey();
t.column("last_name").VARCHAR(20).pkey();
t.column("age").INTEGER();

// 使用方言来创建DDL
String[] ddlArray = dialect.toCreateDDL(t);

for (String ddl : ddlArray) {
    logger.info(ddl);
}
```


执行上述方法，控制台日志输出如下：

```
create table user_t ( first_name varchar(20),last_name varchar(20),age integer, primary key (first_name,last_name))
```


### 插入数据


要插入数据，可以编写一段“"insert into user_t ...” 的SQL语句，而后通过JDBC的方式插入到数据库中，代码如下：


```java
void testAddData() throws SQLException {
    Connection conn = null;
    try {
        conn = ds.getConnection();

        for (int i = 1; i <= 100; i++) {
            execute(conn, "insert into user_t (first_name, last_name, age) values(?,?,?)", "Foo" + i, "Bar" + i, i);
        }
    } catch (
            SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

private int execute(Connection conn, String sql, Object... params) throws SQLException {
    PreparedStatement pst = null;
    try {
        int i = 1;
        pst = conn.prepareStatement(sql);
        for (Object obj : params) {
            pst.setObject(i++, obj);
        }

        pst.execute();
        return 1;
    } catch (SQLException e) {
        e.printStackTrace();
        return 0;
    } finally {
        if (pst != null)
            try {
                pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
```

### 测试分页查询

pagin方法用于生成分页查询的DDL，用法如下：

```java
String ddl = dialect.pagin(2, 20, "select * from user_t");

logger.info(ddl);
```

其中，pagin方法的第一个参数为页码，第二个参数为页面大小。


执行上述方法，控制台日志输出如下：

```
select * from user_t limit 20 offset 20
```


### 添加列

toAddColumnDDL方法用于添加列的DDL，用法如下：

```java
// 使用TableModel
TableModel t = new TableModel("user_t");

// 使用ColumnModel
ColumnModel c = new ColumnModel("score").INTEGER();
c.setTableModel(t);

// 使用方言来创建DDL
String[] ddlArray = dialect.toAddColumnDDL(c);


for (String ddl : ddlArray) {
    logger.info(ddl);
}
```

上述方法需要构建TableModel、ColumnModel对象。

执行上述方法，控制台日志输出如下：

```
alter table user_t add column score integer
```


### 删除列

toDropColumnDDL方法用于删除列的DDL，用法如下：

```java
// 使用TableModel
TableModel t = new TableModel("user_t");

// 使用ColumnModel
ColumnModel c = new ColumnModel("score");
c.setTableModel(t);

// 使用方言来创建DDL
String[] ddlArray = dialect.toDropColumnDDL(c);


for (String ddl : ddlArray) {
    logger.info(ddl);
}
```


执行上述方法，控制台日志输出如下：

```
alter table user_t drop column score
```


### 删除表结构

toDropDDL方法用于删除表结构的DDL，用法如下：

```java
// 使用TableModel
TableModel t = new TableModel("user_t");

// 使用方言来创建DDL
String[] ddlArray = dialect.toDropDDL(t);


for (String ddl : ddlArray) {
    logger.info(ddl);
}
```


执行上述方法，控制台日志输出如下：

```
drop table if exists user_t CASCADE
```




## 源码

完整源码见[《跟老卫学jDialects开发》](https://github.com/waylau/jdialects-tutorial)中的“CreateDDLTest.java”。