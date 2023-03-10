# 简介


jDialects是一个Java数据库方言工具，支持80多种数据库方言，具有分页、函数变换、类型变换、DDL生成、JPA注解解析等功能。它通常与JDBC工具组合使用，也可以作为其它Dao工具(如jSqlBox)的一个组成部分。

## 优点

* 无侵入：jDialects原理基于对SQL文本进行变换，对您现有的JDBC持久层工具无侵入
* 依赖少：不依赖任何第三方库，基至把源码拷到项目里也能工作
* 从Annotation创建DDL：提供对一些主要的JPA注解的解析，为开发ORM工具提供便利
* 从Java方法创建DDL：提供Java方法配置来创建DDL，同样的语法也可以在运行期修改配置。
* 从数据库生成实体类源码：可以读取数据库结构，生成各种可定制格式的实体类源码。
* 主键生成器：提供十多种主键生成器，和一个分布式主键生成器，为开发ORM工具提供便利
* 分页：提供跨数据库的分页方法
* 函数变换：对不同的数据库解析成对应方言的函数，尽量做到一次SQL到处运行。
* 类型变换：对不同的数据库字段类型，提供与Java类型的互相变换。
* 保留字检查：提供数据库保留字检查功能。


## 目的

jDialects起初是为了jSqlBox项目而开发的，用于支持跨数据库开发。但在编写过程中发现，不光是jSqlBox,其它一些ORM项目以及非ORM项目都有类似的跨数据库需求，这是所有持久层工具必须解决的一个问题，纯Jdbc、JdbcTemplate、DbUtils、MyBatis等工具都不具备跨数据库支持功能，需要针对每一种数据库写不同的SQL，增加了开发工作量。因以上考虑，决定将它抽取出来做成一个独立的项目。jDialects项目是目前所知唯一通用的、可用于所有持久层工具的数据库方言工具，实现了比较齐全的分页、函数变换、DDL生成、实体源码生成、JPA解析、主键生成等多项与数据库方言相关的功能。它的主要数据来源是基于Hibernate，这是考虑到Hibernate的方言比较齐全和成熟(但是Hibernate本身的方言功能与Hibernate内核捆绑在一起，不能单独使用)，而且一直都在更新。

jDialect是一个独立的小项目，发布包约260K大小，没有任何第三方库依赖，支持Java8或以上。只要用到了原生SQL，就可以利用它来实现跨数据库开发。对于一些ORM项目来说，也可以考虑引入jDialects来避免重复发明轮子。jDialects项目的主体资料部分是由代码生成工具从Hibernate中抽取自动生成，这从一定程度上也保证了它的代码质量。