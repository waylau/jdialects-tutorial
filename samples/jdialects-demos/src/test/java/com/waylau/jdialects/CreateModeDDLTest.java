/*
 * Copyright (c) waylau.com, 2023. All rights reserved.
 */
package com.waylau.jdialects;

import com.github.drinkjava2.jdialects.Dialect;
import com.github.drinkjava2.jdialects.model.TableModel;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * 从Java方法创建模型的DDL，包括TableModel、ColumnModel、FKeyModel、IndexModel、UniqueModel
 * 涉及保留字的处理
 * check -> check_attribute
 * unique -> unique_attribute
 * precision -> precision_attribute
 * comment -> comment_attribute
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 2023-02-03
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 与@BeforeAll搭配使用
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 指定方法的执行顺序
class CreateModeDDLTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Dialect dialect = Dialect.MySQL8Dialect;

    private BasicDataSource ds = null;

    @BeforeAll
    void initData() {
        // 启用日志
        Dialect.setGlobalAllowShowSql(true);

        // 使用H2数据库
        ds = new BasicDataSource();// DataSource
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/test_jdialects?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&serverTimezone=Asia/Shanghai");
        ds.setUsername("root");
        ds.setPassword("123456");
    }

    @AfterAll
    void deleteData() throws SQLException {
        if (ds != null) {
            ds.close();
        }
    }

    @Test
    @Order(1)
    void testToDropDDLL() {
        // 使用TableModel
        TableModel table = new TableModel("model_table_t");
        TableModel column = new TableModel("model_column_t");
        TableModel fkey = new TableModel("model_fkey_t");
        TableModel index = new TableModel("model_index_t");
        TableModel unique = new TableModel("model_unique_t");

        // 执行DDL
        executeDDL(dialect.toDropDDL(column));
        // 执行DDL
        executeDDL(dialect.toDropDDL(fkey));
        // 执行DDL
        executeDDL(dialect.toDropDDL(index));
        // 执行DDL
        executeDDL(dialect.toDropDDL(unique));

        // 因为表被引用，所以最后被删除
        executeDDL(dialect.toDropDDL(table));
    }

    @Test
    @Order(2)
    void testCreateTableModelDDL() {
        // 使用TableModel
        TableModel table = new TableModel("model_table_t");
        // 执行DDL
        executeDDL(dialect.toDropDDL(table));

        // 使用TableModel
        TableModel t = new TableModel("model_table_t");
        t.column("model_table_id").LONG().pkey().autoId();
        t.column("table_name").VARCHAR(50);
        t.column("check_attribute").VARCHAR(50);
        t.column("comment_attribute").VARCHAR(100);
        t.column("engine_tail").VARCHAR(50);
        t.column("table_tail").VARCHAR(50);
        // 使用方言来创建DDL
        String[] ddlArray = dialect.toCreateDDL(t);

        // 执行DDL
        executeDDL(ddlArray);
    }

    @Test
    @Order(3)
    void testCreateColumnModelDDL() {
        TableModel column = new TableModel("model_column_t");

        // 执行DDL
        executeDDL(dialect.toDropDDL(column));

        // 使用TableModel
        TableModel t = new TableModel("model_column_t");
        t.column("model_column_id").LONG().pkey().autoId();
        t.column("column_name").VARCHAR(50);
        t.column("column_type").VARCHAR(50);
        t.column("column_definition").VARCHAR(50);
        t.column("pkey").BOOLEAN();
        t.column("nullable").BOOLEAN();
        t.column("check_attribute").VARCHAR(50);
        t.column("default_value").VARCHAR(50);
        t.column("tail").VARCHAR(50);
        t.column("comment_attribute").VARCHAR(50);
        t.column("create_timestamp").BOOLEAN();
        t.column("update_timestamp").BOOLEAN();
        t.column("created_by").BOOLEAN();
        t.column("last_modified_by").BOOLEAN();
        t.column("id_generation_type").VARCHAR(50);
        t.column("id_generation_name").VARCHAR(50);
        t.column("converter_class_or_name").VARCHAR(350);
        t.column("entity_field").VARCHAR(50);
        t.column("length").INTEGER();
        t.column("precision_attribute").INTEGER();
        t.column("scale").INTEGER();
        t.column("insertable").BOOLEAN();
        t.column("updatable").BOOLEAN();
        t.column("transientable").BOOLEAN();
        t.column("model_table_id").LONG();
        t.fkey("fk_model_table_id_model_column_t_model_table_t").columns("model_table_id").refs("model_table_t", "model_table_id");

        // 使用方言来创建DDL
        String[] ddlArray = dialect.toCreateDDL(t);

        // 执行DDL
        executeDDL(ddlArray);
    }

    @Test
    @Order(4)
    void testCreateFKeyModelDDL() {
        TableModel fkey = new TableModel("model_fkey_t");
        // 执行DDL
        executeDDL(dialect.toDropDDL(fkey));

        // 使用TableModel
        TableModel t = new TableModel("model_fkey_t");
        t.column("model_fkey_id").LONG().pkey().autoId();
        t.column("name").VARCHAR(50);
        t.column("unique_attribute").BOOLEAN();
        t.column("fk_column_names").VARCHAR(250).comment("多个外键列名英文逗号隔开");
        t.column("pk_column_names").VARCHAR(250).comment("多个被引用主键列名英文逗号隔开");
        t.column("pk_table_name").VARCHAR(50).comment("被引用表的名称");
        t.column("model_table_id").LONG();
        t.fkey("fk_model_table_id_model_fkey_t_model_table_t").columns("model_table_id").refs("model_table_t", "model_table_id");

        // 使用方言来创建DDL
        String[] ddlArray = dialect.toCreateDDL(t);

        // 执行DDL
        executeDDL(ddlArray);
    }

    @Test
    @Order(5)
    void testCreateUniqueModelDDL() {
        TableModel unique = new TableModel("model_unique_t");

        // 执行DDL
        executeDDL(dialect.toDropDDL(unique));

        // 使用TableModel
        TableModel t = new TableModel("model_unique_t");
        t.column("model_unique_id").LONG().pkey().autoId();
        t.column("name").VARCHAR(50);
        t.column("column_names").VARCHAR(250).comment("多个列名英文逗号隔开");
        t.column("model_table_id").LONG();
        t.fkey("fk_model_table_id_model_unique_t_model_table_t").columns("model_table_id").refs("model_table_t", "model_table_id");

        // 使用方言来创建DDL
        String[] ddlArray = dialect.toCreateDDL(t);

        // 执行DDL
        executeDDL(ddlArray);
    }

    @Test
    @Order(6)
    void testCreateIndexModelDDL() {
        TableModel index = new TableModel("model_index_t");

        // 执行DDL
        executeDDL(dialect.toDropDDL(index));

        // 使用TableModel
        TableModel t = new TableModel("model_index_t");
        t.column("model_index_id").LONG().pkey().autoId();
        t.column("index_name").VARCHAR(50);
        t.column("unique_attribute").BOOLEAN();
        t.column("column_names").VARCHAR(250).comment("多个列名英文逗号隔开");
        t.column("model_table_id").LONG();
        t.fkey("fk_model_table_id_model_index_t_model_table_t").columns("model_table_id").refs("model_table_t", "model_table_id");

        // 使用方言来创建DDL
        String[] ddlArray = dialect.toCreateDDL(t);

        // 执行DDL
        executeDDL(ddlArray);
    }

    //@Test
    @Order(7)
    void testToDropDDL() {
        // 使用TableModel
        TableModel table = new TableModel("model_table_t");
        TableModel column = new TableModel("model_column_t");
        TableModel fkey = new TableModel("model_fkey_t");
        TableModel index = new TableModel("model_index_t");
        TableModel unique = new TableModel("model_unique_t");

        Connection conn = null;
        try {
            conn = ds.getConnection();

            for (String ddl : dialect.toDropDDL(table)) {
                logger.info(ddl);
                execute(conn, ddl);
            }

            for (String ddl : dialect.toDropDDL(column)) {
                logger.info(ddl);
                execute(conn, ddl);
            }

            for (String ddl : dialect.toDropDDL(fkey)) {
                logger.info(ddl);
                execute(conn, ddl);
            }

            for (String ddl : dialect.toDropDDL(index)) {
                logger.info(ddl);
                execute(conn, ddl);
            }

            for (String ddl : dialect.toDropDDL(unique)) {
                logger.info(ddl);
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

    // 执行SQL
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

    // 执行DDL
    private void executeDDL(String[] ddlArray) {
        Connection conn = null;
        try {
            conn = ds.getConnection();

            for (String ddl : ddlArray) {
                logger.info(ddl);
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

}