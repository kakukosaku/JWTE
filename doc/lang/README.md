# Java Language Features

## Overview

Java lang related summary!

- [Access Modifiers](doc/lang#access-modifiers)
- [Servlet & Servlet Container](doc/lang#servlet--servlet-container)
- [Java Bean](doc/lang#java-bean)
- [Glossary](doc/lang#glossary)

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

