# Java Economy System, Engineering Problem solution!

## Overview

- [Spring Framework](#Spring-Framework)

### Spring Framework

Spring Framework Documentation Components (Version 5.2.6.RELEASE):

Ref: https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/index.html

**modules**

- [Core](#Core): IoC Container, Events, Resources, i18n, Validation, Data Binding, Type Conversion, SpEL, AOP.

- [Testing](#Testing): Mock Objects, TestContext Framework, Spring MVC Test, WebTestClient.

- [Data Access](#Data-Access): Transactions, DAO Support, JDBC, O/R Mapping, XML Marshalling.

- [Web Servlet](#Web-Servlet): Spring MVC, WebSocket, SockJS, STOMP Messaging.

Web Reactive: Spring WebFlux, WebClient, WebSocket.

Integration: Remoting, JMS, JCA, JMX, Email, Tasks, Scheduling, Caching.

Languages: Kotlin, Groovy, Dynamic Languages.

#### Overview

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

#### Core

IoC Container

--- 

1. The IoC(Inversion of Control, IoC) Container

Introduction to the Spring IoC Container and Beans

`org.springframework.beans` & `org.springframework.context` packages are the basis for Spring Framework’s IoC container.

`BeanFactory` provides the configuration framework and basic functionality, and the ApplicationContext adds more enterprise-specific functionality. 

The ApplicationContext is a complete superset of the BeanFactory and is used exclusively in this chapter in descriptions of Spring’s IoC container.

> In Spring, the objects that form the backbone of your application and that are managed by the Spring IoC container are called beans. A bean is an object that is instantiated, assembled, and otherwise managed by a Spring IoC container. Otherwise, a bean is simply one of many objects in your application. Beans, and the dependencies among them, are reflected in the configuration metadata used by a container.

在spring中, 将由spring 实例化, 组装并管理的object称为beans; Beans, and the dependencies among them, are reflected in the configuration metadata used by a container.

2. Container

> The `org.springframework.context.ApplicationContext` interface represents the Spring IoC container and is responsible for instantiating, configuring, and assembling the beans.
>
> The container gets its instructions on what objects to instantiate, configure, and assemble by reading configuration metadata.
>
> The configuration metadata is represented in XML, Java annotations, or Java code. It lets you express the objects that compose your application and the rich interdependencies between those objects.

这三句话, 讲的明明白白: 上面说了, 这里只针对 `ApplicationContext` 这一个说明; 太过直白精练, 不用再总结式翻译了...

spring 提供了一些`ApplicationContext` 这个 interface 的实现, 如 `ClassPathXmlApplicatoinContext` or `FileSystemXmlApplicationContext`.

一般经典是通过 XML 形式定义 configuration metadata, 但也可以通过 annotation & java code 去配置(但仍需少量xml 配置去启用这些功能).

```
configuration metadata + You business object(POJOs) --Spring Container--> Fully configured system Readay for use.
```

**Instantiating a Container**

通过 xml, annotation-base configuration, java-base configuration even groovy (Domain-specific Language, DSL)去定义 configuration metadata

一般使用:

```java
ApplicationContext ctx = new ClassPathXmlApplicationContext("services.xml");
PetStoreService service = ctx.getBean("petStore", PetStoreService.class);

List<String> rest = service.method();
```

The most flexible variant is GenericApplicationContext in combination with reader delegates

```java
GenericApplicationContext context = new GenericApplicationContext();
new XmlBeanDefinitionReader(context).loadBeanDefinitions("services.xml", "daos.xml");
context.refresh();

GenericApplicationContext context = new GenericApplicationContext();
new GroovyBeanDefinitionReader(context).loadBeanDefinitions("services.groovy", "daos.groovy");
context.refresh();
```

3. Bean

> A Spring IoC container manages one or more beans. These beans are created with the configuration metadata that you supply to the container (for example, in the form of XML <bean/> definitions).

在Container内部, 以`BeanDefinition`表示, 其包含如下metadata:

- A package-qualified class name: typically, the actual implementation class of the bean being defined.
- Bean behavioral configuration elements, which state how the bean should behave in the container (scope, lifecycle callbacks, and so forth).
- References to other beans that are needed for the bean to do its work. These references are also called collaborators or dependencies.
- Other configuration settings to set in the newly created object — for example, the size limit of the pool or the number of connections to use in a bean that manages a connection pool.

总结一下: class, bean behavioral config, dependencies, properties.

除了通过配置方式定义 bean, `ApplicationContext` 实现也允许注册已有的对象(在容器之外创建的)

```markdown
DefaultListableBeanFactory f = ctx.getBeanFactory()
f.registerBeanDefinition(...)
```

每个Bean必须有一个唯一的标识, 一般使用 id attribute, name attribute或2者同时使用, 一般为 alphanumeric (myBean, someService, etc.) 未显示指定的, container生成一个唯一的name for that bean.(此时无法通过 ref 引用)

name 中指定多个 separated by  comma(,), semicolon(;), or white space. 或者通过 `<alias/>`

```markdown
<alias name="fromName" alias="toName"/>
```

**Instantiating Beans**

A bean definition is essentially a recipe for creating one or more objects. The container looks at the recipe for a named bean when asked and uses the configuration metadata encapsulated by that bean definition to create (or acquire) an actual object.

在XML-based configuration metadate, 必须显示指定 class 属性在 `<bean/>` element 中, 一般通过以下2种方式完成 class 的配置:

- 典型的, 容器本身 creates the bean by calling `The bean class to be constructed` -> its constructor reflectively, somewhat equivalent to Java code with the new operator.
- To specify the actual class containing the static factory method that is invoked to create the object, in the less common case where the container invokes a static factory method on a class to create the bean. The object type returned from the invocation of the static factory method may be the same class or another class entirely.

Inner class names:

> For example, if you have a class called SomeThing in the com.example package, and this SomeThing class has a static nested class called OtherThing, the value of the class attribute on a bean definition would be com.example.SomeThing$OtherThing.

Instantiation with a Static Factory Method

并未指明 return type, 仅仅指明了类和它的工厂方法(该工厂方法must be a static method), for supplying(optional) arguments to factory method and setting object instance properties after the object is returned from factory method, see `Dependencies and Configuration detail`.

```xml
<bean id="clientService" class="examples.ClientService" factory-method="createInstance"/>
```

```java
public class ClientService {
    private static ClientService clientService = new ClientService();
    private ClientService() {}

    public static ClientService createInstance() {
        return clientService;
    }
}
```

类似的, 使用 instance factory method:

```xml
<!-- the factory bean, which contains a method called createInstance() -->
<bean id="serviceLocator" class="examples.DefaultServiceLocator">
    <!-- inject any dependencies required by this locator bean -->
</bean>

<!-- the bean to be created via the factory bean -->
<bean id="clientService"
    factory-bean="serviceLocator"
    factory-method="createClientServiceInstance"/>
```

```java
public class DefaultServiceLocator {

    private static ClientService clientService = new ClientServiceImpl();

    public ClientService createClientServiceInstance() {
        return clientService;
    }
}
```

**Determining a Bean’s Runtime Type**

一般建议通过 `BeanFactory.getType` 获取由于 factory method(AOP对 factory method的proxy等) 导致的 "runtime type of the bean" 

4. Dependencies

> Dependency Injection, DI  is a process whereby objects define their dependencies (that is, the other objects with which they work) only through constructor arguments, arguments to a factory method, or properties that are set on the object instance after it is constructed or returned from a factory method.

DI exists in two major variants: Constructor-based dependency injection and Setter-based dependency injection.

**Constructor-based Dependency Injection**

> Constructor-based DI is accomplished by the container invoking a constructor with a number of arguments, each representing a dependency. Calling a static factory method with specific arguments to construct the bean is nearly equivalent, and this discussion treats arguments to a constructor and to a static factory method similarly. The following example shows a class that can only be dependency-injected with constructor injection:

```java
public class SimpleMovieLister {

    // the SimpleMovieLister has a dependency on a MovieFinder
    private MovieFinder movieFinder;

    // a constructor so that the Spring container can inject a MovieFinder
    public SimpleMovieLister(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // business logic that actually uses the injected MovieFinder is omitted...
}
```

**Constructor Argument Resolution**

类构造器, 无歧义, constructor-arg 定义顺序与 构造器顺序相同, 如下

```java
package x.y;

public class ThingOne {

    public ThingOne(ThingTwo thingTwo, ThingThree thingThree) {
        // ...
    }
}
```

```xml
<beans>
    <bean id="beanOne" class="x.y.ThingOne">
        <constructor-arg ref="beanTwo"/>
        <constructor-arg ref="beanThree"/>
    </bean>

    <bean id="beanTwo" class="x.y.ThingTwo"/>

    <bean id="beanThree" class="x.y.ThingThree"/>
</beans>
```

```java
package examples;
public class ExampleBean {

    // Number of years to calculate the Ultimate Answer
    private int years;

    // The Answer to Life, the Universe, and Everything
    private String ultimateAnswer;

    public ExampleBean(int years, String ultimateAnswer) {
        this.years = years;
        this.ultimateAnswer = ultimateAnswer;
    }
}
```

```xml
<bean id="exampleBean" class="examples.ExampleBean">
    <constructor-arg type="int" value="7500000"/>
    <constructor-arg type="java.lang.String" value="42"/>
</bean>
```

或者显示的指定 `index` to specify explicitly the index of constructor arguments, as the following example shows:

```xml
<bean id="exampleBean" class="examples.ExampleBean">
    <constructor-arg index="0" value="7500000"/>
    <constructor-arg index="1" value="42"/>
</bean>
```

也可以在 <constructor-arg/> 中使用 name 指定参数名, 但必须在 `debug` 模式下编译或使用 @ConstructorProperties({"years", "ultimateAnswer"})

**Setter-based Dependency Injection**

可以与 constructor-base 混用;

一般来说, constructor-base 用来初始化必须的依赖, setter-base 可选的依赖;

constructor-base 的依赖初始化, 可以提供fully initialized state, 但过多的构造器参数 is a bad code smell.

**Dependency Resolution Process**

The container performs bean dependency resolution as follows:

- The `ApplicationContext` is created and initialized with `configuration metadata` that describes all the beans. Configuration metadata can be specified by XML, Java code, or annotations.
- For each bean, its dependencies are expressed in the form of properties, constructor arguments, or arguments to the static-factory method (if you use that instead of a normal constructor). These dependencies are provided to the bean, when the bean is actually created.
- Each property or constructor argument is an actual definition of the value to set, or a reference to another bean in the container.
- Each property or constructor argument that is a value is converted from its specified format to the actual type of that property or constructor argument. By default, Spring can convert a value supplied in string format to all built-in types, such as int, long, String, boolean, and so forth.

`ApplicationContext` ctx (container) 被创建时即校验 configuration metadata.

然而, the bean properties themselves are not set until the bean is actually created. 而beans何时被创建, 取决于lazy init, bean scopes等配置.

Spring 一般会检测发现配置的问题, 如non-existent beans和循环依赖 在container load-time. Spring set properties and resolves dependencies as late as possible, when the bean is actually created.

这会导致在使用某个bean时, 才会发现配置的问题, 如它的properties invalid/missing, 这也是为什么`ApplicationContext` 被默认实现为 pre-instantiate singleton beans的原因!

> At the cost of some upfront time and memory to create these beans before they are actually needed, you discover configuration issues when the ApplicationContext is created, not later.

**Dependencies and Configuration in Detail**

p-namespace 定义等效于 <property name="attr" value="attrValue"/>, 但typos are discovered at runtime rather than design time, unless you use an IDE (such as IntelliJ IDEA or the Spring Tools for Eclipse) that supports automatic property completion when you create bean definitions. Such IDE assistance is highly recommended.

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource"
        destroy-method="close"
        p:driverClassName="com.mysql.jdbc.Driver"
        p:url="jdbc:mysql://localhost:3306/mydb"
        p:username="root"
        p:password="masterkaoli"/>

</beans>
```

> The first form is preferable to the second, because using the idref tag lets the container validate at deployment time that the referenced, named bean actually exists. In the second variation, no validation is performed on the value that is passed to the targetName property of the client bean. Typos are only discovered (with most likely fatal results) when the client bean is actually instantiated. If the client bean is a prototype bean, this typo and the resulting exception may only be discovered long after the container is deployed.

The idref element is preferable to <property name="attr" value="attrValue"/>

**Inner Beans**

A <bean/> element inside the <property/> or <constructor-arg/> elements defines an inner bean, as the following example shows:

```xml
<bean id="outer" class="...">
    <!-- instead of using a reference to a target bean, simply define the target bean inline -->
    <property name="target">
        <bean class="com.example.Person"> <!-- this is the inner bean -->
            <property name="name" value="Fiona Apple"/>
            <property name="age" value="25"/>
        </bean>
    </property>
</bean>
```

> An inner bean definition does not require a defined ID or name. If specified, the container does not use such a value as an identifier. The container also ignores the scope flag on creation, because inner beans are always anonymous and are always created with the outer bean. It is not possible to access inner beans independently or to inject them into collaborating beans other than into the enclosing bean.
> Inner beans typically simply share their containing bean’s scope.

**Collections**

example:

```xml
<bean id="moreComplexObject" class="example.ComplexObject">
    <!-- results in a setAdminEmails(java.util.Properties) call -->
    <property name="adminEmails">
        <props>
            <prop key="administrator">administrator@example.org</prop>
            <prop key="support">support@example.org</prop>
            <prop key="development">development@example.org</prop>
        </props>
    </property>
    <!-- results in a setSomeList(java.util.List) call -->
    <property name="someList">
        <list>
            <value>a list element followed by a reference</value>
            <ref bean="myDataSource" />
        </list>
    </property>
    <!-- results in a setSomeMap(java.util.Map) call -->
    <property name="someMap">
        <map>
            <entry key="an entry" value="just some string"/>
            <entry key ="a ref" value-ref="myDataSource"/>
        </map>
    </property>
    <!-- results in a setSomeSet(java.util.Set) call -->
    <property name="someSet">
        <set>
            <value>just some string</value>
            <ref bean="myDataSource" />
        </set>
    </property>
</bean>
```

collection merging

example:

```xml
<beans>
    <bean id="parent" abstract="true" class="example.ComplexObject">
        <property name="adminEmails">
            <props>
                <prop key="administrator">administrator@example.com</prop>
                <prop key="support">support@example.com</prop>
            </props>
        </property>
    </bean>
    <bean id="child" parent="parent">
        <property name="adminEmails">
            <!-- the merge is specified on the child collection definition -->
            <props merge="true">
                <prop key="sales">sales@example.com</prop>
                <prop key="support">support@example.co.uk</prop>
            </props>
        </property>
    </bean>
<beans>
```

> the child collection’s values are the result of merging the elements of the parent and child collections, with the child’s collection elements overriding values specified in the parent collection.

Limitations of Collection Merging

> You cannot merge different collection types (such as a Map and a List)

Strongly-typed collection

Null and Empty String Values

**XML Shortcut with the p-namespace**

> The p-namespace lets you use the bean element’s attributes (instead of nested <property/> elements) to describe your property values collaborating beans, or both.

example:

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean name="classic" class="com.example.ExampleBean">
        <property name="email" value="someone@somewhere.com"/>
    </bean>

    <bean name="p-namespace" class="com.example.ExampleBean"
        p:email="someone@somewhere.com"/>
</beans>
```

p-namespace with reference to another bean:

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean name="john-classic" class="com.example.Person">
        <property name="name" value="John Doe"/>
        <property name="spouse" ref="jane"/>
    </bean>

    <bean name="john-modern"
        class="com.example.Person"
        p:name="John Doe"
        p:spouse-ref="jane"/>

    <bean name="jane" class="com.example.Person">
        <property name="name" value="Jane Doe"/>
    </bean>
</beans>
```

**XML Shortcut with the c-namespace**

> the c-namespace, introduced in Spring 3.1, allows inlined attributes for configuring the constructor arguments rather then nested constructor-arg elements.

example:

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:c="http://www.springframework.org/schema/c"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="beanTwo" class="x.y.ThingTwo"/>
    <bean id="beanThree" class="x.y.ThingThree"/>

    <!-- traditional declaration with optional argument names -->
    <bean id="beanOne" class="x.y.ThingOne">
        <constructor-arg name="thingTwo" ref="beanTwo"/>
        <constructor-arg name="thingThree" ref="beanThree"/>
        <constructor-arg name="email" value="something@somewhere.com"/>
    </bean>

    <!-- c-namespace declaration with argument names -->
    <bean id="beanOne" class="x.y.ThingOne" c:thingTwo-ref="beanTwo"
        c:thingThree-ref="beanThree" c:email="something@somewhere.com"/>

</beans>
```

**Compound Property Names**

```xml
<bean id="something" class="things.ThingOne">
    <property name="fred.bob.sammy" value="123" />
</bean>
```

**Using depends-on**

> The depends-on attribute can explicitly force one or more beans to be initialized before the bean using this element is initialized. 

example:

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:c="http://www.springframework.org/schema/c"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="beanOne" class="ExampleBean" depends-on="manager,accountDao">
        <property name="manager" ref="manager" />
    </bean>

    <bean id="manager" class="ManagerBean" />
    <bean id="accountDao" class="x.y.jdbc.JdbcAccountDao" />
</beans>
```

**Lazy-initialized Beans**

> By default, ApplicationContext implementations eagerly create and configure all singleton beans as part of the initialization process.
> Generally, this pre-instantiation is desirable, because errors in the configuration or surrounding environment are discovered immediately, as opposed to hours or even days later.

默认的 `ApplicationContext` 实现, 尽早的创建并配置所有的bean为单例模式

```xml
<bean id="lazy" class="com.something.ExpensiveToCreateBean" lazy-init="true"/>
<bean name="not.lazy" class="com.something.AnotherBean"/>
```

有例外是说, 如果标记为 lazy-init="true" 的bean是另一个 `singleton pre-instantiation` 的依赖项, 则lazy-init不生效...

container level lazy-init

```xml
<beans default-lazy-init="true">
    <!-- no beans will be pre-instantiated... -->
</beans>
```

**Autowiring Collaborators**

自动装配, 随着代码开发变化, 无需显示的修改依赖配置; 当代码稳定时, 也可切换到显示装配;

> When using XML-based configuration metadata (see Dependency Injection), you can specify the autowire mode for a bean definition with the autowire attribute of the <bean/> element. The autowiring functionality has four modes. You specify autowiring per bean and can thus choose which ones to autowire. The following table describes the four autowiring modes:

|     Mode     |Explanation                                                                         |
|--------------|------------------------------------------------------------------------------------|
| no           | (Default) No autowiring. Bean references must be defined by ref elements.          |
| byName       | Autowiring by property name. e.g. bean contain a master property (that is, it has a setMaster(..) method), Spring looks for a bean definition named master and uses it to set the property.        |
| byType       | Lets a property be autowired if exactly one bean of the property type exists in the container. If more than one exists, a fatal exception is thrown. If there are no matching beans, nothing happens (the property is not set).          |
| constructor  | Analogous to byType but applies to constructor arguments. If there is not exactly one bean of the constructor argument type in the container, a fatal error is raised.          |

**Limitations and Disadvantages of Autowiring**

> Autowiring works best when it is used consistently across a project. If autowiring is not used in general, it might be confusing to developers to use it to wire only one or two bean definitions.

Consider the limitations and disadvantages of autowiring:

- Explicit dependencies in property and constructor-arg settings always override autowiring. You cannot autowire simple properties such as primitives, Strings, and Classes (and arrays of such simple properties). This limitation is by-design.
- Autowiring is less exact than explicit wiring. Although, as noted in the earlier table, Spring is careful to avoid guessing in case of ambiguity that might have unexpected results. The relationships between your Spring-managed objects are no longer documented explicitly.
- Wiring information may not be available to tools that may generate documentation from a Spring container.
- Multiple bean definitions within the container may match the type specified by the setter method or constructor argument to be autowired. For arrays, collections, or Map instances, this is not necessarily a problem. However, for dependencies that expect a single value, this ambiguity is not arbitrarily resolved. If no unique bean definition is available, an exception is thrown.

In the latter scenario, you have several options:

- Abandon autowiring in favor of explicit wiring.
- Avoid autowiring for a bean definition by setting its `autowire-candidate` attributes to `false`, as described in the next section.
- Designate a single bean definition as the primary candidate by setting the `primary` attribute of its <bean/> element to true.
- Implement the more fine-grained control available with annotation-based configuration, as described in Annotation-based Container Configuration.

**Excluding a Bean from Autowiring**

On a per-bean basis, you can exclude a bean from autowiring. In Spring’s XML format, set the autowire-candidate attribute of the <bean/> element to false. The container makes that specific bean definition unavailable to the autowiring infrastructure (including annotation style configurations such as @Autowired).

Notes:

> The autowire-candidate attribute is designed to only affect type-based autowiring. It does not affect explicit references by name, which get resolved even if the specified bean is not marked as an autowire candidate. As a consequence, autowiring by name nevertheless injects a bean if the name matches.

You can also limit autowire candidates based on pattern-matching against bean names. The top-level <beans/> element accepts one or more patterns within its default-autowire-candidates attribute. For example, to limit autowire candidate status to any bean whose name ends with Repository, provide a value of *Repository. To provide multiple patterns, define them in a comma-separated list. An explicit value of true or false for a bean definition’s autowire-candidate attribute always takes precedence. For such beans, the pattern matching rules do not apply.

**Method Injection**

In most application scenarios, most beans in the container are singletons.

尝试这样一个场景:

singleton bean A needs to use non-singleton (prototype) bean B, perhaps on each method invocation on A. 

而 Container 只创建 A 一次, 因此只会set properties一次, The container cannot provide bean A with a new instance of bean B every time one is needed.

一个解决方法是通过 `forego some inversion of control`, You can make bean `A aware of the container` by implementing the `ApplicationContextAware` interface, and by `making a getBean("B") call to the container` ask for (a typically new) bean B instance every time bean A need it.

example:

```java
// a class that uses a stateful Command-style class to perform some processing
package fiona.apple;

// Spring-API imports
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CommandManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Object process(Map commandState) {
        // grab a new instance of the appropriate Command
        Command command = createCommand();
        // set the state on the (hopefully brand new) Command instance
        command.setState(commandState);
        return command.execute();
    }

    protected Command createCommand() {
        // notice the Spring API dependency!
        return this.applicationContext.getBean("command", Command.class);
    }

    public void setApplicationContext(
            ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
```

缺点也很明显, business code is aware of and coupled to the Spring Framework. 也即侵入性太强!

**Lookup Method Injection**

Lookup method injection 是容器提供的, override 容器管理的beans的方法并且返回 `the lookup result for another named bean in the container`

example:

```java
package fiona.apple;

// no more Spring imports!

public abstract class CommandManager {

    public Object process(Object commandState) {
        // grab a new instance of the appropriate Command interface
        Command command = createCommand();
        // set the state on the (hopefully brand new) Command instance
        command.setState(commandState);
        return command.execute();
    }

    // okay... but where is the implementation of this method?
    protected abstract Command createCommand();
}
```

需要被injected的method, 需要有如下method signature:

```
<public|protected> [abstract] <return-type> theMethodName(no-arguments);
```

> If the method is abstract, the dynamically-generated subclass implements the method. Otherwise, the dynamically-generated subclass overrides the concrete method defined in the original class. Consider the following example:

xml example:

```xml
<!-- a stateful bean deployed as a prototype (non-singleton) -->
<bean id="myCommand" class="fiona.apple.AsyncCommand" scope="prototype">
    <!-- inject dependencies here as required -->
</bean>

<!-- commandProcessor uses statefulCommandHelper -->
<bean id="commandManager" class="fiona.apple.CommandManager">
    <lookup-method name="createCommand" bean="myCommand"/>
</bean>
```

annotation example:

```java
public abstract class CommandManager {

    public Object process(Object commandState) {
        Command command = createCommand();
        command.setState(commandState);
        return command.execute();
    }

    @Lookup("myCommand")
    protected abstract Command createCommand();
}
```

**Arbitrary Method Replacement**

With XML-based configuration metadata, you can use the replaced-method element to replace an existing method implementation with another, for a deployed bean. Consider the following class, which has a method called computeValue that we want to override:

```java
public class MyValueCalculator {

    public String computeValue(String input) {
        // some real code...
    }

    // some other methods...
}
```

A class that implements the `org.springframework.beans.factory.support.MethodReplacer` interface provides the new method definition, as the following example shows:

```java
/**
 * meant to be used to override the existing computeValue(String)
 * implementation in MyValueCalculator
 */
public class ReplacementComputeValue implements MethodReplacer {

    public Object reimplement(Object o, Method m, Object[] args) throws Throwable {
        // get the input value, work with it, and return a computed result
        String input = (String) args[0];
        // ...
        return "someObject";
    }
}
```

xml example:

```xml
<bean id="myValueCalculator" class="x.y.z.MyValueCalculator">
    <!-- arbitrary method replacement -->
    <replaced-method name="computeValue" replacer="replacementComputeValue">
        <arg-type>String</arg-type>
    </replaced-method>
</bean>

<bean id="replacementComputeValue" class="a.b.c.ReplacementComputeValue"/>
```

5. Bean Scopes

You can control not only the various dependencies and configuration values that are to be plugged into an object that is created from a particular bean definition but also control the scope of the objects created from a particular bean definition.

Spring 支持6种 scopes, 其中4种仅在web-aware `ApplicationContext` 时可用, 详情如下:

| Scope       | Description                                                                                          |
|-------------|------------------------------------------------------------------------------------------------------|
| singleton   | (Default) Scopes a single bean definition to a single object instance for each Spring IoC container. |
| prototype   | Scopes a single bean definition to any number of object instances. |
| request     | Scopes a single bean definition to the lifecycle of a single HTTP request. That is, each HTTP request has its own instance of a bean created off the back of a single bean definition. Only valid in the context of a web-aware Spring `ApplicationContext`. |
| session     | Scopes a single bean definition to the lifecycle of an HTTP Session. Only valid in the context of a web-aware Spring ApplicationContext. |
| application | Scopes a single bean definition to the lifecycle of a ServletContext. Only valid in the context of a web-aware Spring ApplicationContext. |
| websocket   | Scopes a single bean definition to the lifecycle of a WebSocket. Only valid in the context of a web-aware Spring ApplicationContext. |

**The Singleton Scope**

Only one shared instance of a singleton bean is managed, and all requests for beans with an ID or IDs that match that bean definition result in that one specific bean instance being returned by the Spring container.

区另于单例, singleton(default) scope 是指 Spring container creates one and only one instance of the class defined by that bean definition per-container.

xml example:

```xml
<bean id="accountService" class="com.something.DefaultAccountService"/>

<!-- the following is equivalent, though redundant (singleton scope is the default) -->
<bean id="accountService" class="com.something.DefaultAccountService" scope="singleton"/>
```

**The Prototype Scope**

当 prototype scope 的 bean 被DI或显式调用`getBean()`时, 它总是 `results in the creation of a new bean instance`.

As a rule, you should use the prototype scope for all stateful beans and the singleton scope for stateless beans.

有状态的beans使用 prototype scope, stateless 使用 singleton scope

> (A data access object (DAO) is not typically configured as a prototype, because a typical DAO does not hold any conversational state. It was easier for us to reuse the core of the singleton diagram.)

xml example:

```xml
<bean id="accountService" class="com.something.DefaultAccountService" scope="prototype"/>
```

Notes:

- The client code must clean up prototype-scoped objects and release expensive resources that the prototype beans hold.
- To get the Spring container to release resources held by prototype-scoped beans, try using a custom bean post-processor, which holds a reference to beans that need to be cleaned up.

> In some respects, the Spring container’s role in regard to a prototype-scoped bean is a replacement for the Java new operator. All lifecycle management past that point must be handled by the client. (For details on the lifecycle of a bean in the Spring container, see Lifecycle Callbacks.)

**Request, Session, Application, and WebSocket Scopes**

request, session, application, websocket scopes 仅能用于 web-aware 的Spring `ApplicationContext` 诸如: `XmlWebApplicationContext`. 在其它常规 Spring IoC 容器诸如 `ClassPathXmlApplicationContext`使用时, `IllegalStateException` is thrown.

对于使用Spring Web MVC(request is processed by the Spring `DispatcherServlet`), 无需额外的配置, 否则需要配置一下在`web.xml`中.

xml example:

```xml
<bean id="loginAction" class="com.something.LoginAction" scope="request"/>
<bean id="userPreferences" class="com.something.UserPreferences" scope="session"/>
<bean id="appPreferences" class="com.something.AppPreferences" scope="application"/>
```

**Scoped Beans as Dependencies**

when injecting a shorter-lived bean into a longer-live scoped bean(for example, injecting an HTTP Session-scoped collaborating bean as a dependency into singleton bean). you need `<aop:scoped-proxy>`!

```xml
<bean id="userPreferences" class="com.something.UserPreferences" scope="session">
    <aop:scoped-proxy/>
</bean>

<bean id="userManager" class="com.something.UserManager">
    <property name="userPreferences" ref="userPreferences"/>
</bean>
```

**Using a Custome Scope**

pass

6. Customizing the Nature of a Bean

**Lifecycle Callbacks**

- 你可以通过实现 `InitializingBean` 和 `DisposableBean` interfaces, 介入 bean 的生命周期中.
- xml的配置 `<bean id="exampleInitBean" class="examples.ExampleBean" init-method="init"/>`
- 或使用 Default Initialization and Destroy Methods, 如下示例:

example:

```java
public class DefaultBlogService implements BlogService {

    private BlogDao blogDao;

    public void setBlogDao(BlogDao blogDao) {
        this.blogDao = blogDao;
    }

    // this is (unsurprisingly) the initialization callback method
    public void init() {
        if (this.blogDao == null) {
            throw new IllegalStateException("The [blogDao] property must be set.");
        }
    }
}
```

```xml
<beans default-init-method="init">
    <bean id="blogService" class="com.something.DefaultBlogService">
        <property name="blogDao" ref="blogDao" />
    </bean>
</beans>
```

> The Spring container guarantees that a configured initialization callback is called immediately after a bean is supplied with all dependencies. Thus, the initialization callback is called on the raw bean reference, which means that AOP interceptors and so forth are not yet applied to the bean. 

**Startup and Shutdown Callbacks**

The Lifecycle interface defines the essential methods for any object that has its own lifecycle requirements (such as starting and stopping some background process):

```java
public interface Lifecycle {

    void start();

    void stop();

    boolean isRunning();
}
```

在容器`ApplicationContext` 收到 start/stop signals(for example, for a stop/restart scenario at runtime), it cascades those calls to all `Lifecycle` implementations defined within that context.

It does this by delegating to a `LifecycleProcessor`, shown in the following listing.

```java
public interface LifecycleProcessor extends Lifecycle {

    void onRefresh();

    void onClose();
}
```

**Shutting Down the Spring IoC Container Gracefully in Non-Web Applications**

example:

```java
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class Boot {

    public static void main(final String[] args) throws Exception {
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");

        // add a shutdown hook for the above context...
        ctx.registerShutdownHook();

        // app runs here...

        // main method exits, hook is called prior to the app shutting down...
    }
}
```

7. Bean Definition Inheritance

Bean 定义包含很多配置信息, 诸如: constructor arguments, property values, and container-specific information, such as the initialization method, a static factory method name, and so on.

子Bean定义继承这些配置信息从父Bean定义中, 可复写或添加其它信息

>A child bean definition inherits scope, constructor argument values, property values, and method overrides from the parent, with the option to add new values. Any scope, initialization method, destroy method, or static factory method settings that you specify override the corresponding parent settings.
>
> The remaining settings are always taken from the child definition: depends on, autowire mode, dependency check, singleton, and lazy init.

xml example:

注意其中 parent bean 被标记为 abstract bean, 该bean 不会被实例化, 无法使用也.

```xml
<bean id="inheritedTestBeanWithoutClass" abstract="true">
    <property name="name" value="parent"/>
    <property name="age" value="1"/>
</bean>

<bean id="inheritsWithClass" class="org.springframework.beans.DerivedTestBean"
        parent="inheritedTestBeanWithoutClass" init-method="initialize">
    <property name="name" value="override"/>
    <!-- age will inherit the value of 1 from the parent bean definition-->
</bean>
```

8. Container Extension Points

pass

9. Annotation-based Container Configuration

注解的配置是否比XML配置更好?

答案是: 视情况而定, 每种方法有每种方法的优缺点, 通常取决于开发人员认为哪种更适合. 比如annotation的方式通常更简短, 但无法注解非自己的代码! 而xml的方式则更灵活, 更集中.

而Spring2种方式都可以, 甚至混合使用

> Annotation injection is performed before XML injection. Thus, the XML configuration overrides the annotations for properties wired through both approaches.

先加载注解, 再加载XML配置, 因此XML配置会覆盖注解配置

**@Required**

The @Required annotation applies to bean property setter methods. @Required要求bean property 必须在configuration time, through an explicit property value in a bean definition or through autowiring.

The container throws an exception if the affected bean property has not been populated. 可避免 `NullPointerException` 或类似的异常.

example:

```java
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Required
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}
```

额外的: We still recommend that you put assertions into the bean class itself (for example, into an init method). Doing so enforces those required references and values even when you use the class outside of a container.

**@Autowired**

> We still recommend that you put assertions into the bean class itself (for example, into an init method). Doing so enforces those required references and values even when you use the class outside of a container.

You can apply the @Autowired annotation to constructors.

Spring Framework 4.3 之后, 如果只有一个显示的constructor, @Autowired 不是必须的, 如果有多个constructor, 必须显示的指明container应该使用哪一个(@Autowired is necessary)

example:

```java
public class MovieRecommender {

    private final CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...
}
```

@Autowired 还可用于 `traditional setter methods`, example:

```java
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Autowired
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}
```

@Autowired 还可用于注解(任意名称, 参数数量的)方法, example:

```java
public class MovieRecommender {

    private MovieCatalog movieCatalog;

    private CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    public void prepare(MovieCatalog movieCatalog,
            CustomerPreferenceDao customerPreferenceDao) {
        this.movieCatalog = movieCatalog;
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...
}
```

You can apply @Autowired to fields as well and even mix it with constructors, as the following example shows:

```java
public class MovieRecommender {

    private final CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    private MovieCatalog movieCatalog;

    @Autowired
    public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...
}
```

需要注意的是, 确保参数中的参数(如上面示例中, `MovieCatalog`, `CusotmerPreferenceDao`)are consistently declared by the type you use, 否则jnjection may failed due to a `no type match found` error at runtime!

> For XML-defined beans or component classes found via classpath scanning, the container usually knows the concrete type up front. However, for @Bean factory methods, you need to make sure that the declared return type is sufficiently expressive. For components that implement several interfaces or for components potentially referred to by their implementation type, consider declaring the most specific return type on your factory method (at least as specific as required by the injection points referring to your bean).

同时, @Autowired 可以被用作 field, 如上, 也可用于 `collection`

特殊的但当用于 `Map` 时, Even typed Map instances can be autowired as long as the expected key type is String. The map values contain all beans of the expected type, and the keys contain the corresponding bean names, as the following example shows:




