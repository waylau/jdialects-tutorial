/*
 * Copyright (c) waylau.com, 2023. All rights reserved.
 */
package com.waylau.jdialects;

import com.github.drinkjava2.jdialects.Dialect;
import com.github.drinkjava2.jdialects.TableModelUtils;
import com.github.drinkjava2.jdialects.model.ColumnModel;
import com.github.drinkjava2.jdialects.model.TableModel;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * TableModelUtilsOfJavaSrc test
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 2023-02-01
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 与@BeforeAll搭配使用
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 指定方法的执行顺序
class TableModelUtilsTest {
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
    @Order(1)
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

    @Test
    @Order(2)
    void testDb2JavaSrcFiles() throws SQLException {
        Map<String, Object> setting = new HashMap<String, Object>();
        setting.put(TableModelUtils.OPT_EXCLUDE_TABLES, Arrays.asList("Dbsample")); // 排除个别表名
        setting.put(TableModelUtils.OPT_PACKAGE_NAME, "somepackage");// 包名
        setting.put(TableModelUtils.OPT_IMPORTS, "import java.util.Map;\n"); // 追加新的imports
        setting.put(TableModelUtils.OPT_REMOVE_DEFAULT_IMPORTS, false); // 是否去除自带的imports
        setting.put(TableModelUtils.OPT_CLASS_DEFINITION, "public class $ClassName extends ActiveRecord<$ClassName> {");// 类定义模板
        setting.put(TableModelUtils.OPT_FIELD_FLAGS, true); // 全局静态属性字段标记
        setting.put(TableModelUtils.OPT_FIELD_FLAGS_STATIC, true); // 全局静态属性字段标记
        setting.put(TableModelUtils.OPT_FIELD_FLAGS_STYLE, "upper"); // 全局静态属性字段标记可以有upper,lower,normal,camel几种格式
        setting.put(TableModelUtils.OPT_FIELDS, true); // 是否生成JavaBean属性
        setting.put(TableModelUtils.OPT_GETTER_SETTERS, true); // 是否生成getter setter
        setting.put(TableModelUtils.OPT_PUBLIC_FIELD, false); // JavaBean属性是否定义成public
        setting.put(TableModelUtils.OPT_LINK_STYLE, true); // getter/setter是否生成为链式风格

        TableModelUtils.db2JavaSrcFiles(ds, dialect, "c:/temp", setting);
    }

    @Test
    @Order(3)
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

    // 执行SQL
    private int execute(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement pst = null;
        try {
            int i = 1;
            pst = conn.prepareStatement(sql);// NOSONAR
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
}