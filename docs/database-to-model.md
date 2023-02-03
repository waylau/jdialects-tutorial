# 数据库生成模型


以下语句会读取数据库所有表格并在并转为TableModel模型：

```java
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

@Test
void testDb2Models() {
    Connection conn = null;
    try {
        conn = ds.getConnection();

        TableModel[] tableModels = TableModelUtils.db2Models(conn, dialect);
        for (TableModel tableModel : tableModels) {
            for (ColumnModel columnModel : tableModel.getColumns()) {
                logger.info("table: {}, column: {} ", tableModel.getTableName(), columnModel.getColumnName());
            }
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
```


可以先在数据库里面创建了表结构user_t，比如执行以下方法：

```java
void testToCreateDDL() throws SQLException {
    // 使用TableModel
    TableModel t = new TableModel("user_t");
    t.column("first_name").VARCHAR(20).pkey();
    t.column("last_name").VARCHAR(20).pkey();
    t.column("age").INTEGER();

    // 使用方言来创建DDL
    String[] ddlArray = dialect.toCreateDDL(t);

    Connection conn = null;
    try {
        conn = ds.getConnection();


        for (String ddl : ddlArray) {
            execute(conn, ddl);
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
```

把TableModel的核心内容通过控制台日志输出出来，内容如下：

```
table: USER_T, column: FIRST_NAME 
table: USER_T, column: LAST_NAME 
table: USER_T, column: AGE 
```

## 源码

完整源码见[《跟老卫学jDialects开发》](https://github.com/waylau/jdialects-tutorial)中的“TableModelUtilsTest.java”。