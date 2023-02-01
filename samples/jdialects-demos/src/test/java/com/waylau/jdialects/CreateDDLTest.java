/*
 * Copyright (c) waylau.com, 2023. All rights reserved.
 */
package com.waylau.jdialects;

import com.github.drinkjava2.jdialects.Dialect;
import com.github.drinkjava2.jdialects.model.ColumnModel;
import com.github.drinkjava2.jdialects.model.TableModel;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * 从Java方法创建DDL
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 2023-02-01
 */
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


    @Test
    @Order(1)
    void testToDropDDLL() throws SQLException {
        // 使用TableModel
        TableModel t = new TableModel("user_t");

        // 使用方言来删除表
        String[] ddlArray = dialect.toDropDDL(t);

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

    @Test
    @Order(2)
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

    @Test
    @Order(3)
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

    @Test
    @Order(4)
    void testPagingQueryData() throws SQLException {
        String ddl = dialect.pagin(2, 20, "select * from user_t");

        Connection conn = null;
        try {
            conn = ds.getConnection();
            logger.info(ddl);
            execute(conn, ddl);
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
    @Order(5)
    void testToAddColumnDDL() throws SQLException {
        // 使用TableModel
        TableModel t = new TableModel("user_t");

        // 使用ColumnModel
        ColumnModel c = new ColumnModel("score").INTEGER();
        c.setTableModel(t);

        // 使用方言来创建DDL
        String[] ddlArray = dialect.toAddColumnDDL(c);

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

    @Test
    @Order(6)
    void testToDropColumnDDL() throws SQLException {
        // 使用TableModel
        TableModel t = new TableModel("user_t");

        // 使用ColumnModel
        ColumnModel c = new ColumnModel("score");
        c.setTableModel(t);

        // 使用方言来创建DDL
        String[] ddlArray = dialect.toDropColumnDDL(c);

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

    @Test
    @Order(7)
    void testToDropDDL() throws SQLException {
        // 使用TableModel
        TableModel t = new TableModel("user_t");

        // 使用方言来创建DDL
        String[] ddlArray = dialect.toDropDDL(t);

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

}