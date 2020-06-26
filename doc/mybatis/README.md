# MyBatis

## Overview

MyBatis is a first class persistence framework with support for custom SQL, stored procedures and advanced mappings. MyBatis eliminates almost all of the JDBC code and manual setting of parameters and retrieval of results. MyBatis can use simple XML or Annotations for configuration and map primitives, Map interfaces and Java POJOs (Plain Old Java Objects) to database records.

- [Getting Started](#getting-started)
- [Configuration XML](#configuration-xml)
- [Mapper XML Files](#mapper-xml-files)

### Getting Started

**Installation**

pass

**Building SqlSessionFactory from XML**

`SqlSessionFactory`是很重要的一个概念, 用于build SqlSession, 而它本身可以通过`SqlSessionFactoryBuilder`来创建.

通过classpath xml resource to build `SqlSessionFactory`. 

MyBatis提供了utility class `Resources` 去加载配置文件, xml/properties.

```java
class T {
    public static void main(String[] args){
        String resource = "org/mybatis/example/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }
}
```

simple example:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="${driver}"/>
        <property name="url" value="${url}"/>
        <property name="username" value="${username}"/>
        <property name="password" value="${password}"/>
      </dataSource>
    </environment>
  </environments>
  <mappers>
    <mapper resource="org/mybatis/example/BlogMapper.xml"/>
  </mappers>
</configuration>
```

The body of the environment element contains the environment configuration for transaction management and connection pooling.

The mappers element contains a list of mappers – the XML files and/or annotated Java interface classes that contain the SQL code and mapping definitions.

**Building SqlSessionFactory without XML**

```java
class T {
    public static void main(String[] args){
        DataSource dataSource = BlogDataSourceFactory.getBlogDataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(BlogMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }
}
```

**Acquiring a SqlSession from SqlSessionFactory**

Now that you have a SqlSessionFactory, as the name suggests, you can acquire an instance of SqlSession. The SqlSession contains absolutely every method needed to execute SQL commands against the database.

You can execute mapped SQL statements directly against the SqlSession instance. For example:

```java
class T {
    public static void main(String[] args){
        try (SqlSession session = sqlSessionFactory.openSession()) {
          Blog blog = session.selectOne(
            "org.mybatis.example.BlogMapper.selectBlog", 101);
        }
    }
}
```

While this approach works, and is familiar to users of previous versions of MyBatis, there is now a cleaner approach. Using an interface (e.g. BlogMapper.class) that properly describes the parameter and return value for a given statement, you can now execute cleaner and more type safe code, without error prone string literals and casting.

For example:

```java
class T {
    public static void main(String[] args){
        try (SqlSession session = sqlSessionFactory.openSession()) {
          BlogMapper mapper = session.getMapper(BlogMapper.class);
          Blog blog = mapper.selectBlog(101);
        }
    }
}
```

**Exploring Mapped SQL Statements**

In either of the examples above, the statements could have been defined by either XML or Annotations. Let's take a look at XML first.

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.mybatis.example.BlogMapper">
  <select id="selectBlog" resultType="Blog">
    select * from Blog where id = #{id}
  </select>
</mapper>
```

```java
package org.mybatis.example;
public interface BlogMapper {
  @Select("SELECT * FROM blog WHERE id = #{id}")
  Blog selectBlog(int id);
}
```

The annotations are a lot cleaner for simple statements, however, Java Annotations are both limited and messier for more complicated statements. Therefore, if you have to do anything complicated, you're better off with XML mapped statements.

**Scope and Lifecycle**

正确的理解各种scopes and lifecycle classes. Using them incorrectly can cause severe concurrency problems.

NOTE: Object lifecycle and Dependency Injection Frameworks

Dependency Injection frameworks can create thread safe, transactional SqlSessions and mappers and inject them directly into your beans so you can just forget about their lifecycle. You may want to have a look at MyBatis-Spring or MyBatis-Guice sub-projects to know more about using MyBatis with DI frameworks.

- `SqlSessionFactoryBuilder`

This class can be instantiated, used and thrown away. There is no need to keep it around once you've created your SqlSessionFactory.

Therefore the best scope for instances of SqlSessionFactoryBuilder is method scope (i.e. a local method variable).

You can reuse the SqlSessionFactoryBuilder to build multiple SqlSessionFactory instances, but it's still best not to keep it around to ensure that all of the XML parsing resources are freed up for more important things.

- `SqlSessionFactory`

Once created, the SqlSessionFactory should exist for the duration of your application execution.

There should be little or no reason to ever dispose of it or recreate it. It's a best practice to not rebuild the SqlSessionFactory multiple times in an application run.

Therefore the best scope of SqlSessionFactory is application scope. This can be achieved a number of ways. The simplest is to use a Singleton pattern or Static Singleton pattern.

- `SqlSession`

Each thread should have its own instance of SqlSession. Instances of SqlSession are not to be shared and are not thread safe. 

Therefore the best scope is request or method scope.

Never keep references to a SqlSession instance in a static field or even an instance field of a class. 

Never keep references to a SqlSession in any sort of managed scope, such as HttpSession of the Servlet framework. If you're using a web framework of any sort, consider the SqlSession to follow a similar scope to that of an HTTP request.

Closing the session is very important. You should always ensure that it's closed within a finally block. The following is the standard pattern for ensuring that SqlSessions are closed:

```java
class T {
    public static void main(String[] args){
        try (SqlSession session = sqlSessionFactory.openSession()) {
          // do work
        }
    }
}
```
 
 Using the pattern consistently throughout your code will ensure that all database resources are properly closed.
 
- Mapper Instances

Mappers are interfaces that you create to bind to your mapped statements. Instances of the mapper interfaces are acquired from the SqlSession. As such, technically the broadest scope of any mapper instance is the same as the SqlSession from which they were requested. 

However, the best scope for mapper instances is method scope.

That is, they should be requested within the method that they are used, and then be discarded. They do not need to be closed explicitly. While it's not a problem to keep them around throughout a request, similar to the SqlSession, you might find that managing too many resources at this level will quickly get out of hand. Keep it simple, keep Mappers in the method scope. The following example demonstrates this practice.

```java
class T {
    public static void main(String[] args){
        try (SqlSession session = sqlSessionFactory.openSession()) {
          BlogMapper mapper = session.getMapper(BlogMapper.class);
          // do work
        }
    }
}
```

### Configuration XML

The MyBatis configuration contains settings and properties that have a dramatic effect on how MyBatis behaves. 

contains: properties, settings, typeAliases, typeHandlers, objectFactory, plugins, environments, databaseIdProvider, mappers

**properties**

可通过典型的 Java Properties file instance等配置 MyBatis, example:

```xml
<properties resource="org/mybatis/example/config.properties">
  <property name="username" value="dev_user"/>
  <property name="password" value="F2Fa3!33TYyg"/>
</properties>
```

The properties can then be used throughout the configuration files to "substitute value" that need to be dynamically configured, for example:

```xml
<dataSource type="POOLED">
  <property name="driver" value="${driver}"/>
  <property name="url" value="${url}"/>
  <property name="username" value="${username}"/>
  <property name="password" value="${password}"/>
</dataSource>
```

Since the MyBatis 3.4.2, you can specify a default value into placeholder as follow:

```xml
<dataSource type="POOLED">
  <!-- ... -->
  <property name="username" value="${username:ut_user}"/> <!-- If 'username' property not present, username become 'ut_user' -->
</dataSource>
```

This feature is disabled by default. you should be enable this feature by adding a special property as follow:

```xml
<properties resource="org/mybatis/example/config.properties">
  <!-- ... -->
  <property name="org.apache.ibatis.parsing.PropertyParser.enable-default-value" value="true"/> <!-- Enable this feature -->
</properties>
```

注意, 这样的配置会与原先的符号":"相冲突, 如果仍然想使用 ternary operator (e.g. `${tableName != null ? tableName : 'global_constants'}`) on a sql definition. you must change the default value separator by adding this special property, as following:

```xml
<properties resource="org/mybatis/example/config.properties">
  <!-- ... -->
  <property name="org.apache.ibatis.parsing.PropertyParser.default-value-separator" value="?:"/> <!-- Change default value of separator -->
</properties>
```

then you can use default value as follow example:

```xml
<dataSource type="POOLED">
  <!-- ... -->
  <property name="username" value="${db:username?:ut_user}"/>
</dataSource>
```

**settings**

有许多可以重要的"配置"可以modify the way that MyBatis behaves at runtime. e.g.

| Setting                   | Valid Values          | Default   |
|---------------------------|-----------------------|-----------|
| cacheEnabled              | true/false            | true      |
| lazyLoadingEnable         | true/false            | false     |
| ...                       | ...                   | ...       |

**typeAliases**

A type alias is simply a shorter name for java type. It's only relevant to the XML configuration and simply exists to reduce redundant typing of fully qualified classnames. for example:

```xml
<typeAliases>
  <typeAlias alias="Author" type="domain.blog.Author"/>
  <typeAlias alias="Blog" type="domain.blog.Blog"/>
  <typeAlias alias="Comment" type="domain.blog.Comment"/>
  <typeAlias alias="Post" type="domain.blog.Post"/>
  <typeAlias alias="Section" type="domain.blog.Section"/>
  <typeAlias alias="Tag" type="domain.blog.Tag"/>
</typeAliases>
```

You can also specify a package where MyBatis will search for beans. For example:

```xml
<typeAliases>
  <package name="domain.blog"/>
</typeAliases>
```

if `@Alias` annoation is found its value will be used as an alias.

```java
@Alias("author")
public class Author {
    // ...
}
```

There are many built-in type aliases for common java types. They are all case-insensitive, some example:

| Alias     | Mapped Type   |
|-----------|---------------|
| _byte     | byte          |
| byte      | Byte          |
| arraylist | ArrayList     |
| ...       | ...           |

**typeHandlers**

Whenever MyBatis sets a parameter on a PreparedStatement or retrieves a value from a ResultSet, a TypeHandler is used to retrieve the value in a means appropriate to the Java type.

some default TypeHandlers:

| Type Handler              | Java Types                    | JDBC Types                |
|---------------------------|-------------------------------|---------------------------|
| BooleanTypeHandler        | java.lang.Boolean, boolean    | Any compatible BOOLEAN    |
| DateTypeHandler           | java.util.Date                | TIMESTAMP                 |
| DateOnlyTypeHandler       | java.util.Date                | DATE                      |
| ...                       | ...                           | ...                       |

You can override, more info pass.

**Handling Enums**

pass

**objectFactory**

Each time MyBatis creates a new instance of a result object, is uses an ObjectFactory instance to do so.

The default ObjectFactory does little more than the target class with a default constructor, or a parameterized constructor if parameter mappings exist.

you can override this behaviour, more info pass.

**plugins**

MyBatis allows you to intercept calls to at certain points within the execution of a mapped statement. By default, MyBatis allows plug-ins intercept method calls of:

- Executor(update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)
- ParameterHandler (getParameterObject, setParameters)
- ResultSetHandler...
- StatementHandler...

more info pass.

**environments**

MyBatis can be configured with multiple environments. This helps you to apply your SQL Maps to multiple databases for any number of reasons. For example, you might have a different configuration for your Development, Test and Production environments. 

One important thing to remember though: While you can configure multiple environments, you can only choose ONE per SqlSessionFactory instance.

So if you want to connect to two databases, you need to create two instances of SqlSessionFactory, one for each. For three databases, you’d need three instances, and so on. It’s really easy to remember:

> One SqlSessionFactory instance per database

```java
class T {
    public static void main(String[] args){
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment, properties);
        // if the enviroment is omitted, then the default enviroments is loaded, as follow:
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, properties);
    }
}
```

The environments element defines how the environment is configured.

```xml
<environments default="development">
  <environment id="development">
    <transactionManager type="JDBC">
      <property name="..." value="..."/>
    </transactionManager>
    <dataSource type="POOLED">
      <property name="driver" value="${driver}"/>
      <property name="url" value="${url}"/>
      <property name="username" value="${username}"/>
      <property name="password" value="${password}"/>
    </dataSource>
  </environment>
</environments>
```

transactionManager:

There are two TransactionManager types (i.e. `type="[JDBC|MANAGED]"`) that are included with MyBatis:

- JDBC - This configuration simply makes use of the JDBC commit and rollback facilities deirectly. It relies on the connection retrieved from the dataSource to the manage the scope of the transaction.
- MANAGED - This configuration simply does almost nothing.  It never commits, or rolls back a connection. Instead, it lets the container manage the full lifecycle of the transaction (e.g. a JEE Application Server context). By default it does close the connection. However, some containers don’t expect this, and thus if you need to stop it from closing the connection, set the "closeConnection" property to false. For example:

```xml
<transactionManager type="MANAGED">
  <property name="closeConnection" value="false"/>
</transactionManager>
```

> NOTE If you are planning to use MyBatis with Spring there is no need to configure any TransactionManager because the Spring module will set its own one overriding any previously set configuration.

Using these two interfaces(`TransactionFactory`, `Transaction`), you can completely customize how MyBatis deals with Transactions.

dataSource:

There are three built-in dataSource types (i.e. `type="[UNPOOLED|POOLED|JNDI]"`):

- UNPOOLED – This implementation of DataSource simply opens and closes a connection each time it is requested. 

DataSource has the following properties to configure: driver, url, username, password, defaultTransactionIsolationLevel, defaultNetworkTimeout...

optionally, you can pass properties to the database driver as well: example: `driver.encoding=UTF8`

- POOLED - This implementation of DataSource pools JDBC Connection objects to avoid the initial connection and authentication time required to create a new Connection instance.

In addition to (UNPOOLED) properties above, there are many more properties that can be used to configure the POOLED dataSource: poolMaximumActiveConnections, poolMaximumIdleConnections, poolMaximumCheckoutTime, poolTimeToWait...etc.

- JNDI - This implementation of DataSource is intended for use with containers such as EJB or Application Servers that may configure the DataSource centrally or externally and place a reference to it in a JNDI context.

This DataSource configuration only requires two properties: initial_context, data_source, env.encoding=UTF8

You can plug any 3td party DataSource by implementing the interface `org.apache.ibatis.datasource.DataSourceFactory`:

```java
public interface DataSourceFactory {
  void setProperties(Properties props);
  DataSource getDataSource();
}
```

databaseIdProvider:

pass

mappers:

Now that the behavior of MyBatis is configured with the above configuration elements. we're ready to define our mapped SQL statement.

First, we need to tell MyBatis where to find them. You can use classpath relative resource references, fully qualified url references (including file:/// URLs), class names or package names. For example:

```xml
<!-- Using classpath relative resources -->
<mappers>
  <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
  <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
  <mapper resource="org/mybatis/builder/PostMapper.xml"/>
</mappers>
```

```xml
<!-- Using url fully qualified paths -->
<mappers>
  <mapper url="file:///var/mappers/AuthorMapper.xml"/>
  <mapper url="file:///var/mappers/BlogMapper.xml"/>
  <mapper url="file:///var/mappers/PostMapper.xml"/>
</mappers>
```

```xml
<!-- Using mapper interface classes -->
<mappers>
  <mapper class="org.mybatis.builder.AuthorMapper"/>
  <mapper class="org.mybatis.builder.BlogMapper"/>
  <mapper class="org.mybatis.builder.PostMapper"/>
</mappers>
```

```xml
<!-- Register all interfaces in a package as mappers -->
<mappers>
  <package name="org.mybatis.builder"/>
</mappers>
```

### Mapper XML Files

The true power of MyBatis is in the Mapped Statements. This is where the magic happens. For all of their power, the Mapper XML files are relatively simple.

The Mapper XML files have only a few first class elements (in the order that they should be defined):

- cache - Configuration of the cache for a given namespace.
- cache-ref - Reference to a cache configuration from another namespace.
- resultMap - The most complicated and powful element that describes how to load you objects from database result sets.
- sql - A reusable chunk of SQL that can be referenced by other statements.
- insert - A mapped INSERT statement.
- update - A mapped UPDATE statement.
- delete - A mapped DELETE statement.
- select - A mapped SELECT statement.

**select**

The select statement is one of the most popular elements that you'll use in MyBatis. For example:

```xml
<select id="selectPerson" parameterType="int" resultType="hashmap">
  SELECT * FROM PERSON WHERE ID = #{id}
</select>
```

This statement is called selectPerson, takes a parameter of type int (or Integer), and returns a HashMap keyed by column names mapped to row values.

`#{id}` this tells MyBatis to create a PreparedStatement parameter. With JDBC, such a parameter would be identified by a "?" in SQL passed to a new PreparedStatement, something like this:

```java
class T {
    public static void main(String[] args){
        // Similar JDBC code, NOT MyBatis…
        String selectPerson = "SELECT * FROM PERSON WHERE ID=?";
        PreparedStatement ps = conn.prepareStatement(selectPerson);
        ps.setInt(1,id);
    }
}
```

*Of course, there's a lot more code required by JDBC alone to extract the results and map them to an instance of an object, which is what MyBatis saves you from having to do.*

The select element has more attributes that allow you to configure the details of how each statement should behave.

```xml
<select
  id="selectPerson"
  parameterType="int"
  parameterMap="deprecated"
  resultType="hashmap"
  resultMap="personResultMap"
  flushCache="false"
  useCache="true"
  timeout="10"
  fetchSize="256"
  statementType="PREPARED"
  resultSetType="FORWARD_ONLY">
    SELECT * FROM t_user WHERE id = #{id}
</select>
```

**insert, update and delete**

The data modification statements insert, update and delete are very similar in their implementation:

```xml
<example>
<insert
  id="insertAuthor"
  parameterType="domain.blog.Author"
  flushCache="true"
  statementType="PREPARED"
  keyProperty=""
  keyColumn=""
  useGeneratedKeys=""
  timeout="20" />

<update
  id="updateAuthor"
  parameterType="domain.blog.Author"
  flushCache="true"
  statementType="PREPARED"
  timeout="20" />

<delete
  id="deleteAuthor"
  parameterType="domain.blog.Author"
  flushCache="true"
  statementType="PREPARED"
  timeout="20" />
</example>
```

The following are some examples of insert, update and delete statements.

```xml
<example>
<insert id="insertAuthor">
  insert into Author (id,username,password,email,bio)
  values (#{id},#{username},#{password},#{email},#{bio})
</insert>

<update id="updateAuthor">
  update Author set
    username = #{username},
    password = #{password},
    email = #{email},
    bio = #{bio}
  where id = #{id}
</update>

<delete id="deleteAuthor">
  delete from Author where id = #{id}
</delete>
</example>
```

for auto-generated column type for the id, the statement would be modified as follows:

```xml
<insert id="insertAuthor" useGeneratedKeys="true" keyProperty="id">
  insert into Author (username,password,email,bio)
  values (#{username},#{password},#{email},#{bio})
</insert>
```

multi-row insert, you can pass a list or an array of `Author`s and retrieve the auto-generated keys.

```xml
<insert id="insertAuthor" useGeneratedKeys="true"
    keyProperty="id">
  insert into Author (username, password, email, bio) values
  <foreach item="item" collection="list" separator=",">
    (#{item.username}, #{item.password}, #{item.email}, #{item.bio})
  </foreach>
</insert>
```

**sql**

This element can be used to define a reusable fragment of SQL code that can be included in other statements. For example:

```xml
<sql id="userColumns"> ${alias}.id,${alias}.username,${alias}.password </sql>
```

```xml
<select id="selectUsers" resultType="map">
  select
    <include refid="userColumns"><property name="alias" value="t1"/></include>,
    <include refid="userColumns"><property name="alias" value="t2"/></include>
  from some_table t1
    cross join some_table t2
</select>
```

more example pass.

**Parameters**

`#{middleInitial, mode=OUT, jdbcType=STRUCT, jdbcTypeName=MY_TYPE, resultMap=departmentResultMap}` more info pass.

**String Substitution**

By default, using the #{} syntax will cause MyBatis to generate PreparedStatement properties and set the values safely against the PreparedStatement parameters (e.g. ?). While this is safer, faster and almost always preferred, sometimes you just want to directly inject an unmodified string into the SQL Statement. For example, for ORDER BY, you might use something like this:

`ORDER BY ${columnName}` (Here MyBatis won't modify or escape the string.), different from `#{var}`.

String Substitution can be very useful when the metadata(i.e. table name or column name) in the sql statement is dynamic, for example, if you want to select from a table by any one of its columns, instead of writing code like:

```java
class T {
    @Select("select * from user where id = #{id}")
    User findById(@Param("id") long id);

    @Select("select * from user where name = #{name}")
    User findByName(@Param("name") String name);

    @Select("select * from user where email = #{email}")
    User findByEmail(@Param("email") String email);
    // and more "findByXxx" method

    // or, you can just write:
    @Select("select * from user where ${column} = #{value}")
    User findByColumn(@Param("column") String column, @Param("value") String value);
}
```

in which the ${column} will be substituted directly and the #{value} will be "prepared". Thus you can just do the same work by:

```java
class T {
    public static void main(String[] args){
        User userOfId1 = userMapper.findByColumn("id", 1L);
        User userOfNameKid = userMapper.findByColumn("name", "kid");
        User userOfEmail = userMapper.findByColumn("email", "noone@nowhere.com");
    }
}
```

**Result Maps**

```xml
<select id="selectUsers" resultType="map">
  select id, username, hashedPassword
  from some_table
  where id = #{id}
</select>
```

Such a statement simply results in all columns being automatically mapped to the keys of a `HashMap`, as specified by the `resultType` attribute.

While useful in many cases, a HashMap doesn't make a very good domain model. It's more likely that your application will use JavaBeans or POJOs (Plain Old Java Objects) for the domain model. MyBatis supports both. Consider the following JavaBean:

```java
package com.someapp.model;

public class User {
  private int id;
  private String username;
  private String hashedPassword;

  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getHashedPassword() {
    return hashedPassword;
  }
  public void setHashedPassword(String hashedPassword) {
    this.hashedPassword = hashedPassword;
  }
}
```

Such a JavaBean could be mapped to a ResultSet just as easily as the HashMap.

```xml
<select id="selectUsers" resultType="com.someapp.model.User">
  select id, username, hashedPassword
  from some_table
  where id = #{id}
</select>
```

use TypeAliases:

```xml
<example>
    <!-- In Config XML file -->
    <typeAlias type="com.someapp.model.User" alias="User"/>

    <!-- In SQL Mapping XML file -->
    <select id="selectUsers" resultType="User">
      select id, username, hashedPassword
      from some_table
      where id = #{id}
    </select>
</example>
```

In these cases MyBatis is automatically creating a `ResultMap` behind the scenes to auto-map the columns to the JavaBean properties **based on name**.

you can use employ select clause aliases on the column names to make the labels match. or define `ResultMap`, for example:

```xml
<resultMap id="userResultMap" type="User">
  <id property="id" column="user_id" />
  <result property="username" column="user_name"/>
  <result property="password" column="hashed_password"/>
</resultMap>
```

And the statement that references it uses the resultMap attribute to do so (notice we removed the resultType attribute). For example:

```xml
<select id="selectUsers" resultMap="userResultMap">
  select user_id, user_name, hashed_password
  from some_table
  where id = #{id}
</select>
```

**Advanced Result Maps**

```xml
<example>
    <resultMap id="blogResult" type="Blog">
      <association property="author" column="author_id" javaType="Author" select="selectAuthor"/>
    </resultMap>

    <select id="selectBlog" resultMap="blogResult">
      SELECT * FROM BLOG WHERE ID = #{id}
    </select>

    <select id="selectAuthor" resultType="Author">
      SELECT * FROM AUTHOR WHERE ID = #{id}
    </select>
</example>
```

That's it. We have two select statements: one to load the Blog, the other to load the Author, and the Blog's resultMap describes that the selectAuthor statement should be used to load its author property.

While this approach is simple, it will not perform well for large data sets or lists. This problem is known as the "N+1 Selects Problem". In a nutshell, the N+1 selects problem is caused like this:

- You execute a single SQL statement to retrieve a list of records (the "+1").
- For each record returned, you execute a select statement to load details for each (the "N").

more info pass.

**Auto-mapping**

When auto-mapping results MyBatis will get the column name and look for a property with the same name ignoring case.

That means that if a column named ID and property named id are found, MyBatis will set the id property with the ID column value.

Usually database columns are named using uppercase letters and underscores between words and java properties often follow the camelcase naming covention. To enable the auto-mapping between them set the setting mapUnderscoreToCamelCase to true.

**cache**

MyBatis includes a powerful *transactional query caching* feature which is very configurable and customizable. 

By default, just local session caching is enabled that is used solely to cache data for the duration of a session. To enable a global second level of caching you simply need to add one line to your SQL Mapping file: `<cache/>`

The effect of this one simple statement is as follows:

- All results from select statements in the mapped statement file will be cached.
- All insert, update and delete statements in the mapped statement file will flush the cache.
- The cache will use a Least Recently Used (LRU) algorithm for eviction.
- The cache will not flush on any sort of time based schedule (i.e. no Flush Interval).
- The cache will store 1024 references to lists or objects (whatever the query method returns).
- The cache will be treated as a read/write cache, meaning objects retrieved are not shared and can be safely modified by the caller, without interfering with other potential modifications by other callers or threads.

you can custom cache implementation, more info pass.
