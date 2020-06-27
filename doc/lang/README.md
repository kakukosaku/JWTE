# Java Language Features

## Overview

Java lang related summary!

- [Access Modifiers](#access-modifiers)
- [Servlet & Servlet Container](#servlet--servlet-container)
- [Java Bean](#java-bean)
- [Glossary](#glossary)

### Access Modifiers

| Class Relationship | private | default(can be omitted) | protect | public |
|--------------------|---------|-------------------------|---------|--------|
| Same Class         | yes     | yes                     | yes     | yes    |
| Same Package Subclass| no    | yes                     | yes     | yes    |
| Same Package Non-subclass| no| yes                     | yes     | yes    |
| Diff Package Subclass| no    | no                      | yes     | yes    |
| Diff Package Non-subclass| no| no                      | no      | yes    |

总结:

private 只能类内访问;

default +允许包内访问;

protect +包外子类访问;

public 都能访问.

### Servlet & Servlet Container

Ref: https://docs.oracle.com/javaee/5/tutorial/doc/bnafe.html

> A servlet is a Java programming language class that is used to extend the capabilities of servers that host applications accessed by means of a request-response programming model.
>
> Although servlets can respond to any type of request, they are commonly used to extend the applications hosted by web servers. For such applications, Java Servlet technology defines HTTP-specific servlet classes.

一般来说, servlet 是指Java中用于代替 CGI(Common Gateway Interface) 程序, 提供高性能的web application 的一套规范(或Interface). (不仅仅用于HTTP-Base的web server)

就 Servlet Interface 而言, 它定义了如何 5 个方法:

init, service, destroy, getServletInfo, getServletConfig

工作流程一般为:

```
if (servlet is requested for the first time) {
    1. Servlet Container load servlet class
    2. instantiates the servlet class.
    3. calls the `init` method passing the ServletConfig object.
}

1. (或4) call `service` method passing with request and response object.
```

Servlet Container calls `destroy` method when it needs to remove the servlet such as at time of stopping server or un-deploy the project.

> Servlet container, also known as Servlet engine is an integrated set of objects that provide run time environment for Java Servlet components.
> In simple words, it is a system that manages Java Servlet components on top of the Web server to handle the Web client requests.

如果你有其它语言`server side`经验(如Python), Servlet, Servlet Container, Spring Web framework的关系, 与Python中 WSGI规范 与 gunicorn容器 flask web framework的关系一样:

1. Servlet(WSGI)规定handle server的规范
2. Servlet Container遵循servlet规范, 实现了具体的request & response处理(e.g. Tomcat, gunicorn)
3. web framework也遵循servlet规范, 处理更上层的逻辑. 如Spring MVC专门处理&提供web领域的封装. 降低与web server耦合.

单就HTTP-Base的Servlet Container的工作流程一般为:

```
1. maps the request with the servlet in the web.xml file.
2. creates request and response object for this request.
3. calls the service method on the thread.
4. The public service method internally calls the protected service method.
5. The protected service method calls the doGet method depending on the type of request.
6. The doGet method generates the response and it is passed to the client.
7. After sending the response, the web container deletes the request and response objects. The thread is contained in the thread pool or deleted depends on the server implementation.
```

### Java Bean

Ref: https://stackoverflow.com/questions/3295496/what-is-a-javabean-exactly

javaBean 只是一个标准, 它满足:

1. All properties private (use getters/setters)
2. A public no-argument constructor
3. Implements Serializable interface.

That's it. It's just a convention. Lots of libraries depend on it though.

### Annotation

ref: https://docs.oracle.com/javase/tutorial/java/annotations/index.html

Annotations, a form of metadata, provide data about a program that is not part of the program itself. Annotations have no direct effect on the operation of the code they annote.

Annotations have a number of uses, among them:

- Information for the compiler - Annotations can be used by the compiler to detect errors or suppress warnings.
- Compile-time and deployment-time processing - Software tools can precess annotation information to generate code, XML files, and so forth.
- Runtime processing - Some annotations are available to be examined at runtime.

使用形式:

```java
class T {
    @Override
    public static void testMethod() {
    }
}
```

可以使用在多种code elements; 可以有参数 `@Bean(name = "beanName")`; 没有可以省略 `@Bean`;

**Predefined annotation types:**

`@Deprecated`, `@Override`, `@SuppressWarnings`, `@SafeVarargs`, `@FunctionalInterface`

**Annotations that apply to other annotations:**

可用于其它注解的注解称为元注解(called meta-annotations), There are several meta-annotation types defined in `java.lang.annotation`.

- `@Retention`: specifies how the marked annotation is stored:
    - RetentionPolicy.SOURCE - the marked annotation is retained only in the source level and is ignored by the compiler.
    - RetentionPolicy.CLASS - the marked annotation is retained by the compiler at compile time, but is ignored by the Virtual Machine(JVM).
    - RetentionPolicy.RUNTIME - the marked annotation is retained by the JVM so it can be used by the runtime environment.
    
- `@Documented` 指明注解(被`@Documented`修饰的注解)是否应该被javadoc tool 解析(documented), 默认javadoc tool不解析注解.
- `@Target` 标记注解使用范围(what kind of java elements the annotation can be applied to.) A target annotation specifies one of the following element types as its value:
    - ElementType.ANNOTATION_TYPE can be applied to an annotation type.
    - ElementType.CONSTRUCTOR can be applied to a constructor.
    - ElementType.FIELD can be applied to field or property.
    - ElementType.LOCAL_VARIABLE
    - ElementType.METHOD
    - ElementType.PACKAGE
    - ElementType.PARAMETER
    - ElementType.Type
    - feature version Java introduce more, such as: TYPE_PARAMETER, TYPE_USE, MODULE

- `@Interited` 指明注解can be inherited from the super class.
- `@Repeatable` 指明注解can be applied more than once to the same declaration or type use.

**Type annotation and Pluggable type systems**

Before the Java SE 8 release, annotations could only be applied to declarations.
As of the Java SE 8 release, annotations can also be applied to any type use. This means that annotations can be used anywhere you use a type.
A few examples of where types are used are class instance creation expressions (new), casts, implements clauses, and throws clauses.
This form of annotation is called a **type annotation** and several examples are provided in [Annotations Basics](https://docs.oracle.com/javase/tutorial/java/annotations/basics.html).

For example:

`@NotNull String name;`

**Repeating Annotations**

more info pass

### Glossary

AOP: Aspect-Oriented Programming, AOP

DAO: Data Access Object, DAO

IOC: Inversion of Control, IOC

JSR: Java Specification Requests, JSR

XML: Extensible Markup Language, XML

DTD: Document Type Definition, DTD

xmlns: xml namespace, xmlns

xsd: xml schema definition, xsd

xsi: xml schema instance, xsi

