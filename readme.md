## platform

## 启动参数必须指定

-Dspring.profiles.active=dev 或者 -Dspring.profiles.active=h2

mvn clean -DskipTests=true install -P mysql
mvn clean install
-Xshare:off

# Maven

+ 继承统一的父级
+ 设置统一的JDK版本
+ 设置统一的字符编码

# 格式化

+ 安装插件 Eclipse Code Formatter
+ 配置项目根目录下格式化配置文件 CodeFormatterProfile.xml
+ 配置包导入次序文件 eclipse-new-project.importorder

# API文档

+ Spring Document
+ http://localhost:9150/information-portal/swagger-ui.html

# trace日志下载

+ Spring Document
+ http://localhost:9150/information-portal//system/downloadLogFile?logId={}

# RESTful规范

+ 请求对象要求包含：header/body/pageInfo属性

  ```json
  {
      "header":{},
      "body": {},
      "pageInfo": {
          "pageSize ": "每页显示的条数",
          "totalPage": "总页码数",
          "currentPageNum": "当前页码"
      }
  }
  ```

+ 响应对象要求包含：trackId/code/message/body

  ```json
  {
      "trackId": "日志链追踪标识",
      "code": "响应码",
      "message": "响应描述",
      "body": {}
  }
  ```

# 缓存

+ 通用场景选用redis， 局部场景可使用caffeine

- 双层缓存支持(暂不启用)
    * 只使用本地缓存, 配置在ehcache3.xml
    * 只使用远程缓存（redis）， 配置在spring.redis.cache.ttl
    * 本地缓存和远程缓存都使用

# 单元测试

+ 本工程单元测试必须都继承 XxxApplicationTests，确保使用同一Spring上下文
+ mysql, h2 SQL兼容性, 使用自定义函数实现, 查阅一下相关文件
    - H2FunctionsLoader
    - V0__functions.sql
    - mysql_h2_function.xlsx

# Kafka规范

+ 消息对象要求包含：transDo/kafkaKey/kafkaMessage/kafkaMessageType/isNotEmptyContent/state/hisTopic

# JDK参数

+ -Xms2048m

+ -Xmx3072m

+ -XX：+ HeapDumpOnOutOfMemoryError

+ -XX：HeapDumpPath = *path*

+ -XX：MaxMetaspaceSize = *size*

+ -XX：MaxDirectMemorySize = *size*

+ -XX:+OmitStackTraceInFastThrow ？

+ -Duser.language = *zh*

+ -Dfile.encoding= *utf-8*

+ -Duser.timezone= *GMT+8*

# 程序启动

+ 第一次需要运行数据库DDL初始化脚本

> 脚本路径: **/db/schema/\*.sql*

+ 第一次需要运行数据库DML初始化脚本

> 脚本路径: **/db/data/\*.sql*

+ 解压**/information-*.zip

```shell
unzip information-*.zip
```

+ 进入**/information-*/目录

```shell
cd information-*
```

+ 运行启动脚本

```shell
./information-*.sh
```

# 校验框架使用

+ 请求入参对象增加**@Valid**注解

```java
class Bar {
    public QueryResAssignRsp queryResAssign(@Valid @RequestBody QueryResAssignReq req);
}
```

+ 请求入参属性增加校验注解, **message**则是属性名

```java
/**
 * 交互流水，页面请求URL参数exchangeId
 */
class Foo {
    @NotBlank(message = "交互流水")
    @JsonProperty("exchangeId")
    private String exchangeId;
}
```

+ 对象级联校验, 则需要在对象属性上增加**@Valid**注解

```java
class Foo {
    @Valid
    @NotNull(message = "请求体")
    @JsonProperty("body")
    private QueryResAssignReqBody body;
}
```

# **Assert**使用

+ 在**ExceptionEnum**增加业务异常枚举定义, 支持参数模板, 规则参见**MessageFormat**

```java
class BarEnum {
    /*
     * 通用错误
     */
    ERROR_COMMON("10101001","错误： {0}"),

    /*
     * 重定向错误
     */
    ERROR_REDIRECT("301","{0}");
}
```

+ 业务直接使用断言**Assert**, 并使用定义的业务异常枚举

```java
class Foo {
Assert.notNull(body,NULL_RES_ASSIGN);

//参数模板
Assert.notNull(userNoticeInfoReq,ERROR_COMMON,"请求为空！");
}
```

# Spring迁移

+ pom中jdk11-->jdk17+

```xml

<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
</dependency>
<dependency>
<groupId>jakarta.servlet</groupId>
<artifactId>jakarta.servlet-api</artifactId>
<scope>provided</scope>
</dependency>
```

+ javax.annotation--->jakarta.annotation
+ javax.validation--->jakarta.validation
+ javax.Resource--->jakarta.Resource
+ javax.validation--->jakarta.validation
+ javax.servlet--->jakarta.servlet

# custom java runtime

```shell
jdeps -cp "target/lib/*" --multi-release 17 --ignore-missing-deps --print-module-deps target/information-portal.jar
jlink --module-path "$JAVA_HOME/jmods" \
  --add-modules java.base,java.compiler,java.desktop,java.instrument,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.security.sasl,java.sql.rowset,java.xml.crypto,jdk.attach,jdk.jdi,jdk.jfr,jdk.management,jdk.net,jdk.unsupported,jdk.xml.dom \
  --output custom-jre \
  --compress=2 \
  --no-header-files \
  --no-man-pages
```

# check maven plugin version

```shell
mvn versions:display-plugin-updates
mvn rewrite:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-spring:RELEASE \
  -Drewrite.activeRecipes=org.openrewrite.java.spring.boot4.UpgradeSpringBoot_4_0

```
