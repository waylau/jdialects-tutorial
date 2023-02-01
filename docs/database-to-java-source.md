# 数据库生成实体源码


以下语句会读取数据库所有表格并在指定的"c:\temp"目录下生成与所有数据库表格对应的实体类源码：

```java
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
```


如果先在数据库里面创建了表结构user_t，比如执行以下方法：

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

则在在"c:\temp"目录下会生成UserT.java类文件，内容如下：

```java
package somepackage;

import static com.github.drinkjava2.jsqlbox.JAVA8.*;
import static com.github.drinkjava2.jsqlbox.SQL.*;
import static com.github.drinkjava2.jsqlbox.DB.*;
import com.github.drinkjava2.jdbpro.SqlItem;
import com.github.drinkjava2.jdialects.annotation.jdia.*;
import com.github.drinkjava2.jdialects.annotation.jpa.*;
import com.github.drinkjava2.jsqlbox.*;

import java.util.Map;

public class UserTName extends ActiveRecord<UserTName> {
	public static final String FIRST_NAME = "FIRST_NAME";

	public static final String LAST_NAME = "LAST_NAME";

	public static final String AGE = "AGE";

	@Id
	@Column(name="FIRST_NAME", length=20)
	private String firstName;
	@Id
	@Column(name="LAST_NAME", length=20)
	private String lastName;

	private Integer age;

	public String getFirstName(){
		return firstName;
	}

	public UserT setFirstName(String firstName){
		this.firstName=firstName;
		return this;
	}

	public String getLastName(){
		return lastName;
	}

	public UserT setLastName(String lastName){
		this.lastName=lastName;
		return this;
	}

	public Integer getAge(){
		return age;
	}

	public UserT setAge(Integer age){
		this.age=age;
		return this;
	}

}
```



## 源码

完整源码见[《跟老卫学jDialects开发》](https://github.com/waylau/jdialects-tutorial)中的“TableModelUtilsTest.java”。