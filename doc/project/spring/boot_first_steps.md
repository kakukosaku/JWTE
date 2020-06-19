# Spring Boot First Steps

## Overview

It answers the basic “what?”, “how?” and “why?” questions.

### Introducing Spring Boot

Spring Boot makes it easy to create stand-alone, production-grade Spring-based Applications that you can run. 

我们对Spring platform and third-party libraries 有自己的见解(opinionated view), 以方便能以最小麻烦开始使用. Most Spring Boot applications need very little Spring configuration.

You can use Spring Boot to create java applications that can be started by using `java -jar` or more traditional war deployments.

Our primary goals are:

- Provide a radically faster and widely accessible getting-started experience for all Spring development.
- 提供开箱即用的默认选项, 并且在需求发生变化时能快速修改.
- 提供一系列非功能性(non-functional) feature, such as embedded servers, security, metrics, health checks, and externalized configuration.
- Absolutely no code generation and no requirement for XML configuration.

### System Requirements

不同版本的 Sprint Boot 有不同的需求, 我看的是2.3.1.RELEASE 最低需要java 8, 兼容上至java14. 同时Spring Framework需要5.2.7.RELEASE 或更高.

Servlet Containers也有一些要求: Tomcat9.0 servlet 4.0; Jetty 9.4 servlet 3.1; Undertow 2.0 servlet 4.0.

Absolutely no code generation and no requirement for XML configuration.

### Installing Spring Boot

maven installation:

```
// mac
brew install maven
// debin use apt-get or centos(which use yum as package management tool)
apt-get install maven
```

Spring Boot dependencies use the `org.springframework.boot groupId`. Typically, you Maven POM file inherits from the `spring-boot-starter-parent` project and declares dependencies to one or mare "Starters".

Spring Boot also provides an optional Maven plugin to create executable jars.

example:

```
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
```

Gradle Installation:

pass

Installing the Spring Boot CLI:

pass

### Developing You First Spring Boot Application

**Use with mvn**

通过mvn, pom.xml配置使用Spring Boot(The pom.xml is the recipe that is used to build your project.)

Spring Boot provides a number of “Starters” that let you add jars to your classpath. Our applications for smoke tests use the spring-boot-starter-parent in the parent section of the POM. The spring-boot-starter-parent is a special starter that provides useful Maven defaults. It also provides a dependency-management section so that you can omit version tags for “blessed” dependencies.

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.1.RELEASE</version>
    </parent>
```

The mvn dependency:tree command prints a tree representation of your project dependencies.

You can see that spring-boot-starter-parent provides no dependencies by itself.

To add the necessary dependencies, edit your pom.xml and add the spring-boot-starter-web dependency

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

**Writing the code**

example codes:

```java
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
public class Example {

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        SpringApplication.run(Example.class, args);
    }

}
```

`@RestController` & `RequestMapping` is Spring MVC annotations. 

`@EnableAutoConfiguration` This annotation tells Spring Boot to “guess” how you want to configure Spring, based on the jar dependencies that you have added. Since spring-boot-starter-web added Tomcat and Spring MVC, the auto-configuration assumes that you are developing a web application and sets up Spring accordingly.

The "main" method:

Our main method delegates to Spring Boot’s SpringApplication class by calling run. `SpringApplication` bootstraps our application, starting Spring, which, in turn, starts the auto-configured Tomcat web server.

`SpringApplication` 引导 our application, 启动Spring, 启动自动配置的Tomcat web server.

We need to pass Example.class as an argument to the run method to tell SpringApplication which is the primary Spring component. The args array is also passed through to expose any command-line arguments.

**Running the Example**

Since you used the spring-boot-starter-parent POM, you have a useful run goal that you can use to start the application. 
 
`mvn sprint-boot:run`

**Creating an Executable Jar**

We finish our example by creating a completely self-contained executable jar file that we could run in production. Executable jars (sometimes called “fat jars”) are archives containing your compiled classes along with all of the jar dependencies that your code needs to run.

To create an executable jar, we need to add the spring-boot-maven-plugin to our pom.xml. To do so, insert the following lines just below the dependencies section:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

> The spring-boot-starter-parent POM includes <executions> configuration to bind the repackage goal. If you do not use the parent POM, you need to declare this configuration yourself. See the plugin documentation for details.

`$ jar tvf target/myproject-0.0.1-SNAPSHOT.jar` to peek inside.

`java -jar target/myproject-0.0.1-SNAPSHOT.jar` to run jar.
