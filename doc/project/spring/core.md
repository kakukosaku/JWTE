# Core

## Contents

IoC Container, Events, Resources, i18n, Validation, Data Binding, Type Conversion, SpEL, AOP

--- 

- [The IoC container](#the-ioc-container)
- [Resources](#resources)
- [Validation, Data Binding, and Type Conversion](#validation-data-binding-and-type-conversion)
- [Spring Expression Language(SpEl)](#spring-expression-languagespel)
- [Aspect Oriented Programming with Spring](#aspect-oriented-programming-with-spring)
- Spring AOP APIs
- [Null-safety](#null-safety)
- [Data Buffers and Codecs](#data-buffers-and-codecs)

### The IoC Container

1. Introduction to the Spring IoC Container and Beans

Introduction to the Spring IoC(Inversion of Control, IoC) Container and Beans

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

@Autowired default behavior is to treat annotated methods and fields as indicating required dependencies. You can change this behavior as demonstrated in the following example:

```java
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Autowired(required = false)
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}
```

**@Nullable**

example:

```java
public class SimpleMovieLister {

    @Autowired
    public void setMovieFinder(@Nullable MovieFinder movieFinder) {
        // ...
    }
}
```

额外的:

> The @Autowired, @Inject, @Value, and @Resource annotations are handled by Spring BeanPostProcessor implementations. This means that you cannot apply these annotations within your own BeanPostProcessor or BeanFactoryPostProcessor types (if any). These types must be 'wired up' explicitly by using XML or a Spring @Bean method.

**@Primary**

Fine-tuing Annotation-based Autowiring with @Primary.

自动装配 @Autowired 可能导致 candidate 众多, @Primary 指定其中优先级, example:

```java
@Configuration
public class MovieConfiguration {

    @Bean
    @Primary
    public MovieCatalog firstMovieCatalog() { ... }

    @Bean
    public MovieCatalog secondMovieCatalog() { ... }

    // ...
}

public class MovieRecommender {

    @Autowired
    private MovieCatalog movieCatalog;

    // ...
}
```

same effect XML example:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

    <bean class="example.SimpleMovieCatalog" primary="true">
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean id="movieRecommender" class="example.MovieRecommender"/>

</beans>
```

**@Qualifier**

you can use Spring’s @Qualifier annotation. You can associate qualifier values with specific arguments, narrowing the set of type matches so that a specific bean is chosen for each argument. 

In the simplest case, this can be a plain descriptive value, as shown in the following example:

```java
public class MovieRecommender {

    @Autowired
    @Qualifier("main")
    private MovieCatalog movieCatalog;

    // ...
}
```

@Qualifier 同时还可用于 constructor arguments or method parameters, example:

```java
public class MovieRecommender {

    private MovieCatalog movieCatalog;

    private CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    public void prepare(@Qualifier("main") MovieCatalog movieCatalog,
            CustomerPreferenceDao customerPreferenceDao) {
        this.movieCatalog = movieCatalog;
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...
}
```

**@Resource**

Spring also support injection by using JSR-250 `@Resource, javax.annotation.Resource` 注解 on fields or bean property setter methods.

@Resource 遵循 name-base injection, takes a name attribute, By default, Spring interprets that value as the bean name to be injected.

example:

```java
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Resource(name="myMovieFinder") 
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }
}
```

若不指定 name, default name is drived from field name(it takes the field name) or setter method(it takes the bean property name).

The following example is going to have the bean named movieFinder injected into its setter method:

```java
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Resource
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }
}
```

**@Value**

@Value is typically used to inject externalized properties, example:

In that case, the catalog parameter and field will be equal to the MovieCatalog value.

```java
@Component
public class MovieRecommender {

    private final String catalog;

    public MovieRecommender(@Value("${catalog.name}") String catalog) {
        this.catalog = catalog;
    }
}

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig { }
```

application.properties file:

```properties
catalog.name=MovieCatalog
```

> A default lenient embedded value resolver is provided by Spring. It will try to resolve the property value and if it cannot be resolved, the property name (for example ${catalog.name}) will be injected as the value. If you want to maintain strict control over nonexistent values, you should declare a PropertySourcesPlaceholderConfigurer bean

> 	Spring Boot configures by default a PropertySourcesPlaceholderConfigurer bean that will get properties from application.properties and application.yml files.

@Value 也可提供default value 以及一些 Built-in converter support provided by Spring, such to Integer, String Array.

default value example:

```java
@Component
public class MovieRecommender {

    private final String catalog;

    public MovieRecommender(@Value("${catalog.name:defaultCatalog}") String catalog) {
        this.catalog = catalog;
    }
}
```

**@PostConstructor and @PreDestroy**

example:

```java
public class CachingMovieLister {

    @PostConstruct
    public void populateMovieCache() {
        // populates the movie cache upon initialization...
    }

    @PreDestroy
    public void clearMovieCache() {
        // clears the movie cache upon destruction...
    }
}
```

10. Classpath Scanning and Managed Components

> Starting with Spring 3.0, many features provided by the Spring JavaConfig project are part of the core Spring Framework. This allows you to define beans using Java rather than using the traditional XML files. Take a look at the @Configuration, @Bean, @Import, and @DependsOn annotations for examples of how to use these new features.

**@Component and Further Stereotype Annotations**

> The @Repository annotation is a marker for any class that fulfills the role or stereotype of a repository (also known as Data Access Object or DAO). Among the uses of this marker is the automatic translation of exceptions, as described in Exception Translation.

Spring 提供了 `@Service`, `@Controller`, `@Component` 用于表示 `@Component` 特定的use cases(in the persistence, service, and presentation layers, respectively).

**Using Meta-annotations and Composed Annotations**

> Many of the annotations provided by Spring can be used as meta-annotations in your own code. A meta-annotation is an annotation that can be applied to another annotation. For example, the @Service annotation mentioned earlier is meta-annotated with @Component, as the following example shows:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
// The Component causes @Service to be treated in the same way as @Component.
@Component 
public @interface Service {

    // ...
}
```

You can also combine meta-annotations to create “composed annotations”. For example, the @RestController annotation from Spring MVC is composed of @Controller and @ResponseBody.

**Automatically Detecting Classes and Registering Bean Definitions**

Spring can automatically detect stereotyped classes and register corresponding BeanDefinition instances with the ApplicationContext.

To autodetect these classes and register the corresponding beans, you need to add @ComponentScan to your @Configuration class, where the basePackages attribute is a common parent package for the two classes. (Alternatively, you can specify a comma- or semicolon- or space-separated list that includes the parent package of each class.)

```java
@Configuration
@ComponentScan(basePackages = "org.example")
public class AppConfig  {
    // ...
}
```

same effect xml configuration example:

The use of `<context:component-scan>` implicitly enables the functionality of `<context:annotation-config>`. There is usually no need to include the `<context:annotation-config>` element when using `<context:component-scan>`.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="org.example"/>

</beans>
```

**Using Filters to Customize Scanning**

deep read, when need this :), just show some example:

```java
@Configuration
@ComponentScan(basePackages = "org.example",
        includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*Stub.*Repository"),
        excludeFilters = @Filter(Repository.class))
public class AppConfig {
    // ...
}
```

same effect xml config example:

```xml
<beans>
    <context:component-scan base-package="org.example">
        <context:include-filter type="regex"
                expression=".*Stub.*Repository"/>
        <context:exclude-filter type="annotation"
                expression="org.springframework.stereotype.Repository"/>
    </context:component-scan>
</beans>
```

**Defining Bean Metadata within Components**

example:

```java
@Component
public class FactoryMethodComponent {

    @Bean
    @Qualifier("public")
    public TestBean publicInstance() {
        return new TestBean("publicInstance");
    }

    public void doWork() {
        // Component method implementation omitted
    }
}
```

在 @Component 注解的类与 @Configuration 注解的类中使用 @Bean methods处理是不同的:

> The @Bean methods in a regular Spring component are processed differently than their counterparts inside a Spring @Configuration class. The difference is that @Component classes are not enhanced with CGLIB to intercept the invocation of methods and fields. CGLIB proxying is the means by which invoking methods or fields within @Bean methods in @Configuration classes creates bean metadata references to collaborating objects. Such methods are not invoked with normal Java semantics but rather go through the container in order to provide the usual lifecycle management and proxying of Spring beans, even when referring to other beans through programmatic calls to @Bean methods. In contrast, invoking a method or field in a @Bean method within a plain @Component class has standard Java semantics, with no special CGLIB processing or other constraints applying.

更多不同, 暂时不表, 待后续有更深入的了解需求时, 再深入. 

**Naming Autodetected Components**

By default, any Spring stereotype annotation (@Component, @Repository, @Service, and @Controller) that contains a name value thereby provides that name to the corresponding bean definition.

If such an annotation contains no name value or for any other detected component (such as those discovered by custom filters), the default bean name generator returns the uncapitalized non-qualified class name. 

example:

```java
// bean name: myMovieLister
@Service("myMovieLister")
public class SimpleMovieLister {
    // ...
}

// bean name: movieFinderImpl
@Repository
public class MovieFinderImpl implements MovieFinder {
    // ...
}
```

自动gen bean name 可也自定义实现, implement the `BeanNameGenerator` interface, example:

```java
@Configuration
@ComponentScan(basePackages = "org.example", nameGenerator = MyNameGenerator.class)
public class AppConfig {
    // ...
}
```

same effect xml config example:

```xml
<beans>
    <context:component-scan base-package="org.example"
        name-generator="org.example.MyNameGenerator" />
</beans>
```

**Providing a Scope for Autodetected Components**

by use `@Scope` annotation, example:

```java
@Scope("prototype")
@Repository
public class MovieFinderImpl implements MovieFinder {
    // ...
}
```

同样, 可自定义Scope level, pass.

**Providing Qualifier Metadata with Annotations**

pass

**Generating an Index of Candidate Components**

尽管扫描 classpath 非常快, 但提供 candidates at compilation time 仍然能帮忙改善大项目的起动性能 startup performance of large applications.

In this mode, all modules that are target of component scan must use this mechanism.

11. Using JSR 330 Standard Annotations

**Dependency Injection with @Inject and @Named**

@Inject ~ @Autowired, @Named ~ @Resource

pass

**@Named and @ManagedBean: Standard Equivalents to the @Component Annotation**

Instead of @Component, you can use @javax.inject.Named or javax.annotation.ManagedBean, as the following example shows:

```java
import javax.inject.Inject;
import javax.inject.Named;

@Named("movieListener")  // @ManagedBean("movieListener") could be used as well
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Inject
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}
```

12. Java-based Container Configuration

**Basic Concepts: @Bean and @Configuration**

> The @Bean annotation is used to indicate that a method instantiates, configures, and initializes a new object to be managed by the Spring IoC container.

@Bean 注解用来指明一个方法用来实例化, 配置一个用被Spring IoC容器管理的对象. 可会于 @Component, 但常与@Configuration beans.

> Annotating a class with @Configuration indicates that its primary purpose is as a source of bean definitions. Furthermore, @Configuration classes let inter-bean dependencies be defined by calling other @Bean methods in the same class.

@Configuration 注解指明一个类的首要目的是作为一个 bean 定义的source.

example:

```java
@Configuration
public class AppConfig {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }
}
```

same effect xml config example:

```xml
<beans>
    <bean id="myService" class="com.acme.services.MyServiceImpl"/>
</beans>
```

**Instantiating the Spring Container by Using AnnotationConfigApplicationContext**

> In much the same way that Spring XML files are used as input when instantiating a ClassPathXmlApplicationContext, you can use @Configuration classes as input when instantiating an AnnotationConfigApplicationContext. This allows for completely XML-free usage of the Spring container, as the following example shows:

```java
class T {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        MyService myService = ctx.getBean(MyService.class);
        myService.doStuff();
    }
}
```

**Using the @Bean Annotation**

alias, description, example:

```java
@Configuration
public class AppConfig {

    @Bean({"dataSource", "subsystemA-dataSource", "subsystemB-dataSource"})
    @Description("Provides a basic example of a bean")
    public DataSource dataSource() {
        // instantiate, configure and return DataSource bean...
    }
}
```

**Using the @Configuration annotation**

Injecting Inter-bean Dependencies: just work within a `@Configuration` class, `@Component` class cannot! 

example:

```java
@Configuration
public class AppConfig {

    @Bean
    public BeanOne beanOne() {
        return new BeanOne(beanTwo());
    }

    @Bean
    public BeanTwo beanTwo() {
        return new BeanTwo();
    }
}
```

Lookup Method Injection: useful when singleton scope has a dependency on a prototype-scope bean.

example:

```java
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

class T {
    @Bean
    @Scope("prototype")
    public AsyncCommand asyncCommand() {
        AsyncCommand command = new AsyncCommand();
        // inject dependencies here as required
        return command;
    }

    @Bean
    public CommandManager commandManager() {
        // return new anonymous implementation of CommandManager with createCommand()
        // overridden to return a new prototype Command object
        return new CommandManager() {
            protected Command createCommand() {
                return asyncCommand();
            }
        };
    }
}
```

**Further Information About How Java-based Configuration Works Internally**

Consider the following example, which shows a @Bean annotated method being called twice:

```java
@Configuration
public class AppConfig {

    @Bean
    public ClientService clientService1() {
        ClientServiceImpl clientService = new ClientServiceImpl();
        clientService.setClientDao(clientDao());
        return clientService;
    }

    @Bean
    public ClientService clientService2() {
        ClientServiceImpl clientService = new ClientServiceImpl();
        clientService.setClientDao(clientDao());
        return clientService;
    }

    @Bean
    public ClientDao clientDao() {
        return new ClientDaoImpl();
    }
}
```

> clientDao() has been called once in clientService1() and once in clientService2(). Since this method creates a new instance of ClientDaoImpl and returns it, you would normally expect to have two instances (one for each service). That definitely would be problematic: In Spring, instantiated beans have a singleton scope by default. This is where the magic comes in: All @Configuration classes are subclassed at startup-time with CGLIB. In the subclass, the child method checks the container first for any cached (scoped) beans before it calls the parent method and creates a new instance.

虽然 clientDao() 被call了2次, 从代码上看, call 一次创建一个ClienDaoImpl 实例, 但实际上, 由于CGLIB(code generation lib, 字节码生成技术)

This is where the magic comes in: All `@Configuration` classes are subclassed at startup-time with CGLIB. In the subclass, the child method checks the container first for any cached(scoped) beans before it calls the parent method and creates a new instance.

**Composing Java-based Configurations**

Much as the <import/> element is used within Spring XML files to aid in modularizing configurations, the @Import annotation allows for loading @Bean definitions from another configuration class

example:

```java
@Configuration
public class ConfigA {

    @Bean
    public A a() {
        return new A();
    }
}

@Configuration
@Import(ConfigA.class)
public class ConfigB {

    @Bean
    public B b() {
        return new B();
    }
}
```

**injecting Dependencies on Imported @Bean Definitions**

在xml配置的方式中, 一个bean依赖另一个bean可以通过 `ref="someBean"` 的方式完成定义, 在java-base 的配置中, 一个被`@Bean`注解的method是可以有参数的, 并且这些参数被当作该bean的依赖bean:

example:

```java
@Configuration
public class ServiceConfig {

    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }
}

@Configuration
public class RepositoryConfig {

    @Bean
    public AccountRepository accountRepository(DataSource dataSource) {
        return new JdbcAccountRepository(dataSource);
    }
}

@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return new DataSource
    }
}

class T {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
        // everything wires up across configuration classes...
        TransferService transferService = ctx.getBean(TransferService.class);
        transferService.transfer(100.00, "A123", "C456");
    }
}
```

同时, 也可以通过传统的方式 `@Autowired`, `@Value` 的方式完成 bean 依赖定义.

example:

```java
@Configuration
public class ServiceConfig {

    @Autowired
    private AccountRepository accountRepository;

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl(accountRepository);
    }
}

@Configuration
public class RepositoryConfig {

    private final DataSource dataSource;

    public RepositoryConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }
}

@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return new DataSource
    }
}

class T {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
        // everything wires up across configuration classes...
        TransferService transferService = ctx.getBean(TransferService.class);
        transferService.transfer(100.00, "A123", "C456");
    }
}
```

**Conditionally Include @Configuration Classes or @Bean Methods**

可通过 `@Profile` 根据一些 enviroment 决定激活哪些beans, 细节暂时pass

**Combining Java and XML Configuration**

通过xml 中 `<context:component-scan base-package="com.github.kakukosaku"/>` & `ClassPathXmlApplicationContext` 混合xml&java-base Annotation;

通过`@ImportResrouce` 来import XML as need.

**XML-centric Use of @Configuration Classes**

在xml-base 中临时使用 java-base in an ad-hoc fashion.

**Declaring @Configuration classes as plain Spring <bean/> elements**

`<context:annotation-config/>` switch on java-base annotation.

```java
@Configuration
public class AppConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }

    @Bean
    public TransferService transferService() {
        return new TransferService(accountRepository());
    }
}
```

meanwhile, xml config:

```xml
<beans>
    <!-- enable processing of annotations such as @Autowired and @Configuration -->
    <!-- or use this replace annotation-config -->
    <!-- <context:component-scan base-package="com.acme"/>-->
    <context:annotation-config/>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>

    <bean class="com.acme.AppConfig"/>

    <bean class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
</beans>
```

meanwhile, properties config:

```properties
jdbc.url=jdbc:hsqldb:hsql://localhost/xdb
jdbc.username=fakeUser
jdbc.password=fakePass
```

final usage:

```java
class T {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/com/acme/system-test-config.xml");
        TransferService transferService = ctx.getBean(TransferService.class);
        // ...
    }
}
```

13. Environment Abstraction

The Environment interface is an abstraction integrated in the container that models two key aspects of the application environment: profiles and properties.

A profile is a named, logical group of bean definitions to be registered with the only if the given profile is active.

The role of `Environment` object with relation to `profiles` is in determining which profiles (if any) are currently active, and which profiles (if any) should be active by default.

Properties play an important role in almost all applications and may originate from a variety of sources: properties files, JVM system properties, system environment variables, JNDI, servlet context parameters, ad-hoc Properties objects, Map objects, and so on. 

The role of the `Environment` object with relation to `properties` is to provide the user with a convenient service interface for configuring property sources and resolving properties from them.

总结, `Environment` 是整合 `profile` 和 `property` 的interface. 它指明哪个profile是activate&哪个profile默认需要active; 它同时还提供了方便的service interface用于配置解析 property(由property files, JVM system properties, system environment, etc.).

**Bean Definition Profiles**

bean difinition profile 提供了在不同环境注册不同的beans的机制. environment 语境在如下场景会非常有用:

working against an in-memory data source in development, versus looking up that same data source from JNDI when in QA or Production.

Registering monitoring infrastructure only when deploying an application into a performance environment.

Registering customized implementations of beans for customer A versus customer B deployments.

总结, 不同环境使用不同data soruce; 启动monitoring在performance environment; 为不同环境启动不同实现的bean(Test mock, etc.);

> Bean definition profiles is a core container feature that provides a solution to this problem.

**Using @Profile**

```java
@Configuration
@Profile("development")
public class StandaloneDataConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:com/bank/config/sql/schema.sql")
            .addScript("classpath:com/bank/config/sql/test-data.sql")
            .build();
    }
}

@Configuration
@Profile("production")
public class JndiDataConfig {

    @Bean(destroyMethod="")
    public DataSource dataSource() throws Exception {
        Context ctx = new InitialContext();
        return (DataSource) ctx.lookup("java:comp/env/jdbc/datasource");
    }
}
```

The profile string may contain logic to be expressed( & | !), can be use in class or method level. example:

```java
@Configuration
public class AppConfig {

    @Bean("dataSource")
    @Profile("development") 
    public DataSource standaloneDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:com/bank/config/sql/schema.sql")
            .addScript("classpath:com/bank/config/sql/test-data.sql")
            .build();
    }

    @Bean("dataSource")
    @Profile("production") 
    public DataSource jndiDataSource() throws Exception {
        Context ctx = new InitialContext();
        return (DataSource) ctx.lookup("java:comp/env/jdbc/datasource");
    }
}
```

**Activating a Profile**

Now that we have updated our configuration, we still need to instruct Spring which profile is active.

activated by programmatically against the `Environment` API which is available through an `ApplicationContext`

```
AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
ctx.getEnvironment().setActiveProfiles("development");
ctx.register(SomeConfig.class, StandaloneDataConfig.class, JndiDataConfig.class);
ctx.refresh();
```

In addition, you can also declaratively activate profiles through the spring.profiles.active property, which may be specified through system environment variables, JVM system properties, servlet context parameters in web.xml, or even as an entry in JNDI (see PropertySource Abstraction). In integration tests, active profiles can be declared by using the @ActiveProfiles annotation in the spring-test module (see context configuration with environment profiles).

```
-Dspring.profiles.active="profile1,profile2"
```

总结, 通过ctx - env - API setActiveProfiles; properties 中设置 `spring.profiles.active`

**Default Profile**

The default profile represents the profile that is enabled by default, example:

If any profile is enabled, the default profile does not apply.

```java
@Configuration
@Profile("default")
public class DefaultDataConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:com/bank/config/sql/schema.sql")
            .build();
    }
}
```

**PropertySource Abstraction**

```
ApplicationContext ctx = new GenericApplicationContext();
Environment env = ctx.getEnvironment();
boolean containsMyProperty = env.containsProperty("my-property");
System.out.println("Does my environment contain the 'my-property' property? " + containsMyProperty);
```

上述 env 中Spring's `StandardEnvironment` is configured with two PropertySource objects: JVM system properties(System.getProperties()); system environment variables (System.getenv())

env is configurable, as following example:

```
ConfigurableApplicationContext ctx = new GenericApplicationContext();
MutablePropertySources sources = ctx.getEnvironment().getPropertySources();
sources.addFirst(new MyPropertySource());
```

**Using @PropertySource**

The @PropertySource annotation provides a convenient and declarative mechanism for adding a PropertySource to Spring’s Environment.

`${...}` 也可出现在 `@PropertySource` resource location中, 会从已加载的 property sources 中解析

```java
@Configuration
@PropertySource("classpath:/com/myco/app.properties")
// @PropertySource("classpath:/com/${my.placeholder:default/path}/app.properties")
public class AppConfig {

    @Autowired
    Environment env;

    @Bean
    public TestBean testBean() {
        TestBean testBean = new TestBean();
        testBean.setName(env.getProperty("testbean.name"));
        return testBean;
    }
}
```

**Placeholder Resolution in Statements**

无实质性内容

14. Registering a LoadTimeWeaver

The LoadTimeWeaver is used by Spring to dynamically transform classes as they are loaded into the Java virtual machine (JVM).

example:

```java
@Configuration
@EnableLoadTimeWeaving
public class AppConfig {
}
```

xml example:

```xml
<beans>
    <context:load-time-weaver/>
</beans>
```

15. Additional Capabilities of the ApplicationContext

`org.springframework.beans.factory` package provides basic functionality for managing and manipulating beans, including in a programmatic way.

`org.springframework.context` package adds the `ApplicationContext` interface, which extends the `BeanFactory` interface, in addition to extending other interfaces to provide additional functionality in a more application framework-oriented style.

**Internationalization using MessageSource**

The `ApplicationContext` interface extends an interface called `MessageSource` and therefore, provides internationalization (i18n) functionality.

**Standard and Custom Events**

pass

**Annotation-based Event Listeners**

pass

**Asynchronous Listeners**

pass

**Ordering Listeners**

pass

**Generic Events**

pass

16. The BeanFactory

pass

### Resources

1. Introduction

说了些 `java.net.URL` 用途和缺点...

2. The Resource Interface

Spring `Resource` interface is meant to be a more capable interface for abstracting access to low-level resources.

```java
public interface Resource extends InputStreamSource {

    boolean exists();

    boolean isOpen();

    URL getURL() throws IOException;

    File getFile() throws IOException;

    Resource createRelative(String relativePath) throws IOException;

    String getFilename();

    String getDescription();
}

public interface InputStreamSource {

    InputStream getInputStream() throws IOException;
}
```

3. Built-in Resource Implementations

UrlResource, ClassPathResource, FileSystemResource, ServletContextResource, InputStreamResource, ByteArrayResource

4. The `ResourceLoader`

The ResourceLoader interface is meant to be implemented by objects that can return (that is, load) Resource instances.

```java
public interface ResourceLoader {

    Resource getResource(String location);
}
```

All application contexts implement the ResourceLoader interface. Therefore, all application contexts may be used to obtain Resource instances.

当call ctx.getResource() 且没有指定 specific prefix时, 根据ctx的类型返回 Resource的具体实现类. 如 `ClassPathXmlApplicationContext`, 返回 `ClassPathResource` etc.

example:

```
Resource template = ctx.getResource("classpath:some/resource/path/myTemplate.txt");
Resource template = ctx.getResource("file:///some/resource/path/myTemplate.txt");
Resource template = ctx.getResource("https://myhost.com/resource/path/myTemplate.txt");
```

5. The `ResrouceLoaderAware` interface

```java
public interface ResourceLoaderAware {

    void setResourceLoader(ResourceLoader resourceLoader);
}
```

6. Resources as Dependencies

7. Application Contexts and Resource Paths

**Constructing Application Contexts**

An application context constructor (for a specific application context type) generally takes a string or array of strings as the location paths of the resources, such as XML files that make up the definition of the context.

```
ApplicationContext ctx = new ClassPathXmlApplicationContext("conf/appContext.xml");
```

**Wildcards in Application Context Constructor Resource Paths**

example:

```
/WEB-INF/*-context.xml
com/mycompany/**/applicationContext.xml
file:C:/some/path/*-context.xml
classpath:com/mycompany/**/applicationContext.xml
```

The classpath*: Prefix

```
ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:conf/appContext.xml");
```

This special prefix specifies that all classpath resources that match the given name must be obtained (internally, this essentially happens through a call to ClassLoader.getResources(…​)) and then merged to form the final application context definition.

**FileSystemResource Caveats**

The FileSystemApplicationContext forces all attached FileSystemResource instances to treat all location paths as relative, whether they start with a leading slash or not. 

```
// the following examples are equivalent.
ApplicationContext ctx = new FileSystemXmlApplicationContext("conf/context.xml");
ApplicationContext ctx = new FileSystemXmlApplicationContext("/conf/context.xml");
```

### Validation, Data Binding, and Type Conversion

The `Validator` and the `DataBinder` make up the `validation` package

The `BeanWrapper` is a fundamental concept in the Spring Framework and is used in a lot of places.

Spring’s `DataBinder` and the lower-level `BeanWrapper` both use `PropertyEditorSupport` implementations to parse and format property values.

1. Validation by Using Spring’s Validator Interface

通过实现`org.springframework.validation.Validator`interface, 使得`Person`具有validation behavior.

- `supports(Class)`: Can this Validator validate instances of the supplied Class?
- `validate(Object, org.springframework.validation.Errors)`: Validates the given object and, in case of validation errors, registers those with the given Errors object

```java
public class Person {

    private String name;
    private int age;

    // the usual getters and setters...
}
```

借助 `ValidationUtils` 帮助, 可以很方便的实现 `Validation` interface

```java
public class PersonValidator implements Validator {

    /**
     * This Validator validates only Person instances
     */
    public boolean supports(Class clazz) {
        return Person.class.equals(clazz);
    }

    public void validate(Object obj, Errors e) {
        ValidationUtils.rejectIfEmpty(e, "name", "name.empty");
        Person p = (Person) obj;
        if (p.getAge() < 0) {
            e.rejectValue("age", "negativevalue");
        } else if (p.getAge() > 110) {
            e.rejectValue("age", "too.darn.old");
        }
    }
}
```

一般来说, 每个Class应该实现自己的 `Validation`; 出于复用校验逻辑, 可将有依赖关系的对象, 注入使用, example:

```java
public class CustomerValidator implements Validator {

    private final Validator addressValidator;

    public CustomerValidator(Validator addressValidator) {
        if (addressValidator == null) {
            throw new IllegalArgumentException("The supplied [Validator] is " +
                "required and must not be null.");
        }
        if (!addressValidator.supports(Address.class)) {
            throw new IllegalArgumentException("The supplied [Validator] must " +
                "support the validation of [Address] instances.");
        }
        this.addressValidator = addressValidator;
    }

    /**
     * This Validator validates Customer instances, and any subclasses of Customer too
     */
    public boolean supports(Class clazz) {
        return Customer.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "surname", "field.required");
        Customer customer = (Customer) target;
        try {
            errors.pushNestedPath("address");
            ValidationUtils.invokeValidator(this.addressValidator, customer.getAddress(), errors);
        } finally {
            errors.popNestedPath();
        }
    }
}
```

2. Resolving Codes to Error Messages

通过 `rejectValue` 之类的方法不只register the code you passed in but also registers a number of additional error codes.

By default, `DefaultMessageCodesResolver` 不只记录了a message with the code you gave but also registers messages that include the field name you passed to reject method.

more info see javadoc of `MessageCodesResolver` & `DefaultMessageCodesResolver`.

3. Bean Manipulation and the BeanWrapper

`org.springframework.beans` javaBeans is a class with a default no-argument constructor and that follows a naming convention where (for example) a property named bingoMadness would have a setter method setBingoMadness(..) and a getter method getBingoMadness().

One quite important class in the beans package is the BeanWrapper interface and its corresponding implementation (BeanWrapperImpl). As quoted from the javadoc, the BeanWrapper offers functionality to set and get property values (individually or in bulk), get property descriptors, and query properties to determine if they are readable or writable.

介绍了`BeanWrapper`的一些使用...recall when need :)

**Setting and Getting Basic and Nested Properties**

example:

```java
public class Company {

    private String name;
    private Employee managingDirector;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Employee getManagingDirector() {
        return this.managingDirector;
    }

    public void setManagingDirector(Employee managingDirector) {
        this.managingDirector = managingDirector;
    }
}

public class Employee {

    private String name;

    private float salary;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }
}
```

BeanWrapperImpl example:

```
BeanWrapper company = new BeanWrapperImpl(new Company());
// setting the company name..
company.setPropertyValue("name", "Some Company Inc.");
// ... can also be done like this:
PropertyValue value = new PropertyValue("name", "Some Company Inc.");
company.setPropertyValue(value);

// ok, let's create the director and tie it to the company:
BeanWrapper jim = new BeanWrapperImpl(new Employee());
jim.setPropertyValue("name", "Jim Stravinsky");
company.setPropertyValue("managingDirector", jim.getWrappedInstance());

// retrieving the salary of the managingDirector through the company
Float salary = (Float) company.getPropertyValue("managingDirector.salary");
```

**Built-in PropertyEditor Implementations**

`java.beans.PropertyEditor` Spring uses the concept of a PropertyEditor to effect the conversion between an Object and a String. e.g: 可以用于convert human readable <-> `Date` objects

more detail info when need.

4. Spring Type Conversion

Spring 3 introduced a core.convert package that provides a general type conversion system. 

The system defines an SPI(Service Provider Interface, SPI) to implement type conversion logic and an API to perform type conversions at runtime.

**Converter SPI**

The SPI to implement type conversion logic is simple and strongly typed, as the following interface definition shows:

```java
package org.springframework.core.convert.converter;

public interface Converter<S, T> {

    T convert(S source);
}
```

Several converter implementations are provided in the core.convert.support package as a convenience. These include converters from strings to numbers and other common types. The following listing shows the StringToInteger class, which is a typical Converter implementation:

```java
package org.springframework.core.convert.support;

final class StringToInteger implements Converter<String, Integer> {

    public Integer convert(String source) {
        return Integer.valueOf(source);
    }
}
```

**Using ConverterFactory**

When you need to centralize the conversion logic for an entire class hierarchy (for example, when converting from String to Enum objects), you can implement ConverterFactory, as the following example shows:

```java
package org.springframework.core.convert.converter;

public interface ConverterFactory<S, R> {

    <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}
```

Consider the StringToEnumConverterFactory as an example:

```java
package org.springframework.core.convert.support;

final class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumConverter(targetType);
    }

    private final class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

        private Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        public T convert(String source) {
            return (T) Enum.valueOf(this.enumType, source.trim());
        }
    }
}
```

**Using GenericConverter**

pass

**The ConversionService API**

pass

**Configuring a ConversionService**

pass

**Using a ConversionService Programmatically**

To work with a ConversionService instance programmatically, you can inject a reference to it like you would for any other bean. The following example shows how to do so:

```java
@Service
public class MyService {

    public MyService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public void doIt() {
        this.conversionService.convert()
    }
}
```

more info pass.

5. Spring Field Formatting

**The Formatter SPI**

The Formatter SPI to implement field formatting logic is simple and strongly typed. The following listing shows the Formatter interface definition:

```java
package org.springframework.format;

public interface Formatter<T> extends Printer<T>, Parser<T> {
}
```

```java
public interface Printer<T> {

    String print(T fieldValue, Locale locale);
}
```

```java
import java.text.ParseException;

public interface Parser<T> {

    T parse(String clientValue, Locale locale) throws ParseException;
}
```

The following DateFormatter is an example Formatter implementation:

```java
package org.springframework.format.datetime;

public final class DateFormatter implements Formatter<Date> {

    private String pattern;

    public DateFormatter(String pattern) {
        this.pattern = pattern;
    }

    public String print(Date date, Locale locale) {
        if (date == null) {
            return "";
        }
        return getDateFormat(locale).format(date);
    }

    public Date parse(String formatted, Locale locale) throws ParseException {
        if (formatted.length() == 0) {
            return null;
        }
        return getDateFormat(locale).parse(formatted);
    }

    protected DateFormat getDateFormat(Locale locale) {
        DateFormat dateFormat = new SimpleDateFormat(this.pattern, locale);
        dateFormat.setLenient(false);
        return dateFormat;
    }
}
```

**Annotation-driven Formatting**

more info pass, example:

```java
public class MyModel {

    @NumberFormat(style=Style.CURRENCY)
    private BigDecimal decimal;
}

public class MyModel {

    @DateTimeFormat(iso=ISO.DATE)
    private Date date;
}
```

**The FormatterRegistry SPI**

pass

**The FormatterRegistrar SPI**

pass

**Configuring Formatting in Spring MVC**

more info in Spring MVC.

6. Configuring a Global Date and Time Format

By default, date and time fields not annotated with @DateTimeFormat are converted from strings by using the DateFormat.SHORT style. If you prefer, you can change this by defining your own global format.

example:

```java
@Configuration
public class AppConfig {

    @Bean
    public FormattingConversionService conversionService() {

        // Use the DefaultFormattingConversionService but do not register defaults
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService(false);

        // Ensure @NumberFormat is still supported
        conversionService.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());

        // Register JSR-310 date conversion with a specific global format
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyyMMdd"));
        registrar.registerFormatters(conversionService);

        // Register date conversion with a specific global format
        DateFormatterRegistrar registrar = new DateFormatterRegistrar();
        registrar.setFormatter(new DateFormatter("yyyyMMdd"));
        registrar.registerFormatters(conversionService);

        return conversionService;
    }
}
```

xml example pass :)

7. Java Bean Validation

**Overview of Bean Validation**

Bean Validation provides a common way of validation through constraint declaration and metadata for Java applications. To use it, you annotate domain model properties with declarative validation constraints which are then enforced by the runtime. There are built-in constraints, and you can also define your own custom constraints.

Bean Validation lets you declare constraints as the following example shows:

See [Bean Validation](https://beanvalidation.org/) for general information about the API.

See the [Hibernate Validator](https://hibernate.org/validator/) documentation for specific constraints.

```java
public class PersonForm {
    private String name;
    private int age;
}

public class PersonForm {

    @NotNull
    @Size(max=64)
    private String name;

    @Min(0)
    private int age;
}
```

**Configuring a Bean Validation Provider**

The basic configuration in the following example triggers bean validation to initialize by using its default bootstrap mechanism.

A Bean Validation provider, such as the Hibernate Validator, is expected to be present in the classpath and is automatically detected.

```java
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class AppConfig {

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean;
    }
}
```

Injecting a Validator

LocalValidatorFactoryBean implements both javax.validation.ValidatorFactory and javax.validation.Validator, as well as Spring’s org.springframework.validation.Validator. You can inject a reference to either of these interfaces into beans that need to invoke validation logic.

```java
import javax.validation.Validator;

@Service
public class MyService {

    @Autowired
    private Validator validator;
}
```

```java
import org.springframework.validation.Validator;

@Service
public class MyService {

    @Autowired
    private Validator validator;
}
```

Configuring Custom Constraints

pass

Spring-driven Method Validation

pass

Additional Configuration Options

pass

**Configuring a DataBinder**

Since Spring 3, you can configure a DataBinder instance with a Validator. Once configured, you can invoke the Validator by calling binder.validate(). Any validation Errors are automatically added to the binder’s BindingResult.

more info see Sprint MVC

```java
class T {
    public static void main(String[] args) {
        Foo target = new Foo();
        DataBinder binder = new DataBinder(target);
        binder.setValidator(new FooValidator());

        // bind to the target object
        binder.bind(propertyValues);

        // validate the target object
        binder.validate();

        // get BindingResult that includes any validation errors
        BindingResult results = binder.getBindingResult();
    }
}
```

**Spring MVC 3 Validation**

see Sprint MVC...

### Spring Expression Language(SpEL)

1. Evaluation

This section introduces the simple use of SpEL interfaces and its expression language. The complete language reference can be found in [Language Reference](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#expressions-language-ref).

The SpEL classes and interfaces you are most likely to use are located in the org.springframework.expression package and its sub-packages, such as spel.support.

```java
class T {
    public static void main(String[] args){
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("'Hello World'"); 
        // The value of the message variable is 'Hello World'.
        String message = (String) exp.getValue();
            
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("'Hello World'.concat('!')"); 
        // 	The value of message is now 'Hello World!'.
        String message = (String) exp.getValue();

        ExpressionParser parser = new SpelExpressionParser();
        // invokes 'getBytes()'
        Expression exp = parser.parseExpression("'Hello World'.bytes"); 
        // This line converts the literal to a byte array.
        byte[] bytes = (byte[]) exp.getValue();
        
        // invokes 'getBytes().length'
        Expression exp = parser.parseExpression("'Hello World'.bytes.length"); 
        int length = (Integer) exp.getValue();
        
        // Construct a new String from the literal and make it be upper case.
        Expression exp = parser.parseExpression("new String('hello world').toUpperCase()"); 
        String message = exp.getValue(String.class);
    }
}
```

The more common usage of SpEL is to provide an expression string that is evaluated against a specific object instance (called the root object). 

```java
class T {
    public static void main(String[] args){
        // Create and set a calendar
        GregorianCalendar c = new GregorianCalendar();
        c.set(1856, 7, 9);
        
        // The constructor arguments are name, birthday, and nationality.
        Inventor tesla = new Inventor("Nikola Tesla", c.getTime(), "Serbian");
        
        ExpressionParser parser = new SpelExpressionParser();
        
        Expression exp = parser.parseExpression("name"); // Parse name as an expression
        String name = (String) exp.getValue(tesla);
        // name == "Nikola Tesla"
        
        exp = parser.parseExpression("name == 'Nikola Tesla'");
        boolean result = exp.getValue(tesla, Boolean.class);
        // result == true
    }
}
```

**Understanding EvaluationContext**

pass

**Parser Configuration**

pass

**SpEL Compilation**

2. Expressions in Bean Definitions

You can use SpEL expressions with XML-based or annotation-based configuration metadata for defining BeanDefinition instances. In both cases, the syntax to define the expression is of the form #{ <expression string> }.

**XML Configuration**

A property or constructor argument value can be set by using expressions

```xml
<bean id="numberGuess" class="org.spring.samples.NumberGuess">
    <property name="randomNumber" value="#{ T(java.lang.Math).random() * 100.0 }"/>

    <!-- other properties -->
</bean>
```

The systemProperties variable is predefined, so you can use it in your expressions

```xml
<bean id="taxCalculator" class="org.spring.samples.TaxCalculator">
    <property name="defaultLocale" value="#{ systemProperties['user.region'] }"/>

    <!-- other properties -->
</bean>
```

You can also refer to other bean properties by name.

```xml
<bean id="numberGuess" class="org.spring.samples.NumberGuess">
    <property name="randomNumber" value="#{ T(java.lang.Math).random() * 100.0 }"/>

    <!-- other properties -->
</bean>

<bean id="shapeGuess" class="org.spring.samples.ShapeGuess">
    <property name="initialShapeSeed" value="#{ numberGuess.randomNumber }"/>

    <!-- other properties -->
</bean>
```

**Annotation Configuration**

To specify a default value, you can place the @Value annotation on fields, methods, and method or constructor parameters.

Autowired methods and constructors can also use the @Value annotation, example pass.

```java
public class FieldValueTestBean {

    @Value("#{ systemProperties['user.region'] }")
    private String defaultLocale;

    // equivalent but on a property setter method:
    // @Value("#{ systemProperties['user.region'] }")
    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getDefaultLocale() {
        return this.defaultLocale;
    }
}
```

3. [Language Reference](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#expressions-language-ref)

pass

### Aspect Oriented Programming with Spring

Aspect-oriented Programming (AOP) complements Object-oriented Programming (OOP) by providing another way of thinking about program structure. The key unit of modularity in OOP is the class, whereas in AOP the unit of modularity is the aspect. Aspects enable the modularization of concerns (such as transaction management) that cut across multiple types and objects. (Such concerns are often termed “crosscutting” concerns in AOP literature.)

面向切面编程补充了面向对象编程, 提供了另一个思考程序结构的方式. OOP中, Class是核心, 而在AOP中, Aspect是核心

One of the key components of Spring is the AOP framework. While the Spring IoC container does not depend on AOP (meaning you do not need to use AOP if you don’t want to), AOP complements Spring IoC to provide a very capable middleware solution.

AOP framework 是Spring中的重要组件. Spring IoC 并不依赖于AOP(你可以选择用或者不用). AOP 为Spring IoC进行了补充, 提供了功能强大的中间件解决方案.

AOP is used in the Spring Framework to:

- Provide declarative enterprise services. The most important such service is declarative transaction management.
- Let users implement custom aspects, complementing their use of OOP with AOP.

Rest info skip, turn-back when need :)

### Spring AOP APIs

pass

### Null-safety

Although Java does not let you express null-safety with its type system, the Spring Framework now provides the following annotations in the org.springframework.lang package to let you declare nullability of APIs and fields:

- `@Nullable`: Annotation to **indicate** that a specific parameter, return value, or field can be null.
- `@NonNull`: Annotation to **indicate** that a specific parameter, return value, or field cannot be null (not needed on parameters / return values and fields where @NonNullApi and @NonNullFields apply, respectively).
- `@NonNullApi`: Annotation at the package level that declares non-null as the default semantics for parameters and return values.
- `@NonNullFields`: Annotation at the package level that declares non-null as the default semantics for fields.

1. Use cases

In addition to providing an explicit declaration for Spring Framework API nullability, these annotations can be used by an IDE (such as IDEA or Eclipse) to provide useful warnings related to null-safety in order to avoid NullPointerException at runtime.

2. JSR-305 meta-annotations

pass

### Data Buffers and Codecs

The spring-core module provides a set of abstractions to work with various byte buffer APIs as follows:

- `DataBufferFactory` abstracts the creation of a data buffer.
- `DataBuffer` represents a byte buffer, which may be pooled.
- `DataBufferUtils` offers utility methods for data buffers.
- `Codecs` decode or encode streams data buffer streams into higher level objects.
