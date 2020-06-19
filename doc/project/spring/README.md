# Spring Framework

Spring Framework Documentation Components (Version 5.2.6.RELEASE):

Ref: https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/index.html

## Overview

1. What we Mean be "Spring"

我们一般提及 Spring 时, 是带有上下文的, 它可以用来指 Spring Framework 本身, 更多是指 `Spring全家桶`(随着项目发展, 一些其它项目基于Spring开发而来)
 
这里的Spring 单指Spring Framework本身 :)

> The Spring Framework is divided into modules. Applications can choose which modules they need. At the heart are the modules of the core container, including a configuration model and a dependency injection mechanism.
>  Beyond that, the Spring Framework provides foundational support for different application architectures, including messaging, transactional data and persistence, and web.

Spring Framework提供了很多功能(诸如: messaging, transactional data and persistence, and web), 但它的核心是 `Core modules` 和它所提供的 ` configuration model and a dependency injection mechanism`.

3. Design Philosophy

> When you learn about a framework, it’s important to know not only what it does but what principles it follows. Here are the guiding principles of the Spring Framework:

学习一个框架, 光知道它做了什么是不够的, 它所遵循的"规则"同样重要! Spring Framework遵循:

- Provide choice at every level.

Spring lets you defer design decisions as late as possible. For example, you can switch persistence providers through configuration without changing your code. 

- Accommodate diverse perspectives.

Spring embraces flexibility and is not opinionated about how things should be done. It supports a wide range of application needs with different perspectives.

- Maintain strong backward compatibility.

Spring’s evolution has been carefully managed to force few breaking changes between versions. Spring supports a carefully chosen range of JDK versions and third-party libraries to facilitate maintenance of applications and libraries that depend on Spring.

- Care about API design.

The Spring team puts a lot of thought and time into making APIs that are intuitive and that hold up across many versions and many years.

- Set high standards for code quality.

The Spring Framework puts a strong emphasis on meaningful, current, and accurate javadoc. It is one of very few projects that can claim clean code structure with no circular dependencies between packages.

# Catalog

- [Core](core.md): IoC Container, Events, Resources, i18n, Validation, Data Binding, Type Conversion, SpEL, AOP.

- Testing: Mock Objects, TestContext Framework, Spring MVC Test, WebTestClient.

- Data Access: Transactions, DAO Support, JDBC, O/R Mapping, XML Marshalling.

- [Web Servlet](web.md): Spring MVC, WebSocket, SockJS, STOMP Messaging.

- Web Reactive: Spring WebFlux, WebClient, WebSocket.

- Integration: Remoting, JMS, JCA, JMX, Email, Tasks, Scheduling, Caching.

- Languages: Kotlin, Groovy, Dynamic Languages.

# Spring Boot

Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".

Ref: https://docs.spring.io/spring-boot/docs/2.3.1.RELEASE/reference/html/documentation-overview.html#boot-documentation

## Overview

Spring Boot makes it easy to create stand-alone, production-grade Spring-based Applications that you can run. We take an opinionated view of the Spring platform and third-party libraries, so that you can get started with minimum fuss. Most Spring Boot applications need very little Spring configuration.

You can use Spring Boot to create Java applications that can be started by using java -jar or more traditional war deployments. We also provide a command line tool that runs “spring scripts”.

- Provide a radically faster and widely accessible getting-started experience for all Spring development.
- Be opinionated out of the box but get out of the way quickly as requirements start to diverge from the defaults.
- Provide a range of non-functional features that are common to large classes of projects (such as embedded servers, security, metrics, health checks, and externalized configuration).
- Absolutely no code generation and no requirement for XML configuration.

## Catalog

- [First Steps](boot_first_steps.md), Introducing Spring Boot, System Requirements, Servlet Containers, Installing Spring Boot, Developing Your First Spring Boot Application
- [Working with Spring Boot](boot_working.md), build systems, best practices, running your code, packaging your app and Spring Boot CLI.
- [Learning about Spring Boot Features](boot_core.md), core features, web application, working with data, messaging, testing, extending.
- Moving to Production, management endpoints, connection options, monitoring.
- Advanced Topics, Spring Boot application deployment, Build tool plugins, appendix.

