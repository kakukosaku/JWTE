# Using Spring Boot

## Overview

这部分是Spring Boot的更多细节, 涵盖了 build systems, auto-configuration, and how to run you applications. 也包含一些Spring Boot的最佳实践.

### Build Systems

推荐使用包含了 dependency management 的build systems, e.g. Maven or Gradle.

**Dependency Management**

Each release of Spring Boot provides a curated list of dependencies that it supports. In practice, you do not need to provide a version for any of these dependencies in your build configuration, as Spring Boot manages that for you. When you upgrade Spring Boot itself, these dependencies are upgraded as well in a consistent way.

Spring Boot提供了其版本支持的一系列的依赖的版本, 实际上你需要额外指明版本, 仅仅需要升级Spring Boot本身即可, 相关依赖会自动升级.

**Maven, Gradle, Ant**

pass

**Starters**

Starters are a set of convenient dependency descriptors that you can include in your application. 

Starters 是一些convenient依赖"描述符", 一站式体验for Spring and related technologies. some example:

| Name                        | Description                                                          |
|-----------------------------|----------------------------------------------------------------------|
| spring-boot-starter         | Core starter, including auto-configuration support, logging and YML. |
| spring-boot-starter-amqp    | Starter for using Spring AMQP and Rabbit MQ.                         |
| spring-boot-starter-jdbc    | Starter for using Spring Data JDBC.                                  |
| spring-boot-starter-actuator| Starter for using Spring Boot's Actuator which provides production ready features to help you monitor and manage you application. |

etc.

### Structuring Your Code

Spring Boot does not require any specific code layout to work. However, there are some best practices that help.

**Using the "default" Package**

When a class does not include a package declaration, it is considered to be in the “default package”. 应当避免使用"default" package.

> We recommend that you follow Java’s recommended package naming conventions and use a reversed domain name (for example, com.example.project).

```
com
 +- example
     +- myapplication
         +- Application.java
         |
         +- customer
         |   +- Customer.java
         |   +- CustomerController.java
         |   +- CustomerService.java
         |   +- CustomerRepository.java
         |
         +- order
             +- Order.java
             +- OrderController.java
             +- OrderService.java
             +- OrderRepository.java
```

The Application.java file would declare the main method, along with the basic @SpringBootApplication, as follows:

```java
package com.example.myapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

### Configuration Classes

Spring Boot favors Java-based configuration. Although it is possible to use SpringApplication with XML sources, we generally recommend that your primary source be a single @Configuration class. Usually the class that defines the main method is a good candidate as the primary @Configuration.

> 	Many Spring configuration examples have been published on the Internet that use XML configuration. If possible, always try to use the equivalent Java-based configuration. Searching for Enable* annotations can be a good starting point.

**Importing Additional Configuration Classes**

You need not put all your `@Configuration` into a single class. The `@Import` annotation can be used to import additional configuration classes. Alternatively, you can use `@ComponentScan` to automatically pick up all Spring components, including `@Configuration` classes.

**Importing XML Configuration**

If you absolutely must use XML based configuration, we recommend that you still start with a @Configuration class. You can then use an @ImportResource annotation to load XML configuration files.

### Auto-configuration

Spring Boot auto-configuration attempts to automatically configure your Spring application based on the jar dependencies that you have added. 

For example, if HSQLDB is on your classpath, and you have not manually configured any database connection beans, then Spring Boot auto-configures an in-memory database.

You need to opt-in to auto-configuration by adding the @EnableAutoConfiguration or @SpringBootApplication annotations to one of your @Configuration classes.

> `@SpringBootApplication` or `@EnableAutoConfiguration` should only use one.
>
> We generally recommend that you add one or the other to your primary @Configuration class only.

**Gradually Replacing Auto-configuration**

Auto-configuration is non-invasive.

At any point, you can start to define your own configuration to replace specific parts of the auto-configuration.

If you need to find out what auto-configuration is currently being applied, and why, start your application with the --debug switch. Doing so enables debug logs for a selection of core loggers and logs a conditions report to the console.

**Disabling Specific Auto-configuration Classes**

If you find that specific auto-configuration classes that you do not want are being applied, you can use the exclude attribute of @SpringBootApplication to disable them, as shown in the following example:

```java
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.jdbc.*;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class MyApplication {
}
```

### Spring Beans and Dependency Injection

You are free to use any of the standard Spring Framework techniques to define your beans and their injected dependencies. For simplicity, we often find that using @ComponentScan (to find your beans) and using @Autowired (to do constructor injection) works well.

If you structure your code as suggested above (locating your application class in a root package), you can add @ComponentScan without any arguments. All of your application components (@Component, @Service, @Repository, @Controller etc.) are automatically registered as Spring Beans.

```java
package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseAccountService implements AccountService {

    private final RiskAssessor riskAssessor;

    @Autowired
    public DatabaseAccountService(RiskAssessor riskAssessor) {
        this.riskAssessor = riskAssessor;
    }

    // ...

}
```

### Using the @SpringBootApplication Annotation

Many Spring Boot developers like their apps to use auto-configuration, component scan and be able to define extra configuration on their "application class". A single @SpringBootApplication annotation can be used to enable those three features, that is:

- @EnableAutoConfiguration: enable Spring Boot’s auto-configuration mechanism
- @ComponentScan: enable @Component scan on the package where the application is located (see the best practices)
- @Configuration: allow to register extra beans in the context or import additional configuration classes

```java
package com.example.myapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

### Running Your Application

`java -jar target/myapplication-0.0.1-SNAPSHOT.jar`

`mvn spring-boot:run`

`$ export MAVEN_OPTS=-Xmx1024m` to define operating system environment variable.

`gradle bootRun`

`$ export JAVA_OPTS=-Xmx1024m`

**Hot Swapping**

Since Spring Boot applications are just plain Java applications, JVM hot-swapping should work out of the box. JVM hot swapping is somewhat limited with the bytecode that it can replace. For a more complete solution, JRebel can be used.

The spring-boot-devtools module also includes support for quick application restarts. See the Developer Tools section later in this chapter and the Hot swapping “How-to” for details.

### Developer Tools

Spring Boot includes an additional set of tools that can make the application development experience a little more pleasant. The spring-boot-devtools module can be included in any project to provide additional development-time features. 

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

> 	Developer tools are automatically disabled when running a fully packaged application. If your application is launched from java -jar or if it is started from a special classloader, then it is considered a “production application”. If that does not apply to you (i.e. if you run your application from a container), consider excluding devtools or set the -Dspring.devtools.restart.enabled=false system property.

**Property Default**

pass

**Automatic Restart**

pass

**LiveReload**

pass

**Global Settings**

You can configure global devtools settings by adding any of the following files to the $HOME/.config/spring-boot directory:

`spring-boot-devtools.properties`, `spring-boot-devtools.yaml`, `spring-boot-devtools.yml`

**Remote Applications**

### Packaging You Application for Production

Executable jars can be used for production deployment. As they are self-contained, they are also ideally suited for cloud-based deployment.

For additional “production ready” features, such as health, auditing, and metric REST or JMX end-points, consider adding spring-boot-actuator. See [production-ready-features.html](https://docs.spring.io/spring-boot/docs/2.3.1.RELEASE/reference/html/production-ready-features.html#production-ready) for details.
