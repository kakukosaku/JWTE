# Web on Servlet Stack

## Contents

This part of the documentation covers support for Servlet-stack web applications built on the Servlet API and deployed to Servlet containers. 

Sprint MVC, View Technologies, CORS Support, WebSocket Support.

---

- [Spring Web MVC](#spring-web-mvc)
- [REST Clients](#rest-clients)
- [Testing](#testing)
- [WebSockets](#websockets)
- [Other Web Framework](#other-web-frameworks)

### Spring Web MVC

Spring Web MVC is the original web framework built on the Servlet API, but it is more commonly known as "Sprint MVC".

1. DispatcherServlet

> Spring MVC, as many other web frameworks, is designed around the front controller pattern where a central Servlet, the DispatcherServlet, provides a shared algorithm for request processing, while actual work is performed by configurable delegate components. 
>
> This model is flexible and supports diverse workflows.

与其他许多Web框架一样，Spring MVC围绕前端控制器模式进行设计，在该模式下，中央Servlet DispatcherServlet提供了用于请求处理的共享算法，而实际工作是由可配置的委托组件执行的。

> The DispatcherServlet, as any Servlet, needs to be declared and mapped according to the Servlet specification by using Java configuration or in web.xml. In turn, the DispatcherServlet uses Spring configuration to discover the delegate components it needs for request mapping, view resolution, exception handling, and more.
>
> The following example of the Java configuration registers and initializes the DispatcherServlet, which is auto-detected by the Servlet container (see Servlet Config):

```java
public class MyWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletCxt) {

        // Load Spring web application configuration
        AnnotationConfigWebApplicationContext ac = new AnnotationConfigWebApplicationContext();
        ac.register(AppConfig.class);
        ac.refresh();

        // Create and register the DispatcherServlet
        DispatcherServlet servlet = new DispatcherServlet(ac);
        ServletRegistration.Dynamic registration = servletCxt.addServlet("app", servlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/app/*");
    }
}
```

same effect xml config way example:

```xml
<web-app>

    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/app-context.xml</param-value>
    </context-param>

    <servlet>
        <servlet-name>app</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value></param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>app</servlet-name>
        <url-pattern>/app/*</url-pattern>
    </servlet-mapping>

</web-app>
```

**Context Hierarchy**

![infrastructure](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/images/mvc-context-hierarchy.png)

```java
public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { RootConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { App1Config.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/app1/*" };
    }
}
```

> If an application context hierarchy is not required, applications can return all configuration through getRootConfigClasses() and null from getServletConfigClasses().

the web.xml equivalent:

```xml
<web-app>

    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/root-context.xml</param-value>
    </context-param>

    <servlet>
        <servlet-name>app1</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/app1-context.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>app1</servlet-name>
        <url-pattern>/app1/*</url-pattern>
    </servlet-mapping>

</web-app>
```

**Special Bean Types**

The DispatcherServlet delegates to special beans to process requests and render the appropriate responses. By “special beans” we mean Spring-managed Object instances that implement framework contracts.

Those usually come with built-in contracts, but you can customize their properties and extend or replace them.

| Bean Type                 | Explanation   |
| ------------------------- | ------------- |
| HandlerMapping            |               |
| HandlerAdapter            |               |
| HandlerExceptionResolver  |               |
| ViewResolver              |               |
| LocaleResolver, LocaleContextResolver     |             |
| ThemeResolver             |               |
| MultipartResolver         |               |
| FlashMapManager           |               |

- HandlerMapping: Map a request to handler along with a list of interceptors for pre- and post- processing.

The mapping is based on some criteria, the details of which very by `HandlerMapping` implementation. The two main `HandlerMapping` implementations are `RequestionMappingHandlerMapping` (which supports @RequestMapping annotated methods) and `SimpleUrlHandlerMapping` (which maintains explicit registrations of URI path patterns to handlers).

- HandlerAdapter: Help the `DispatcherServlet` to invoke a handler mapped to a request, regardless of how the handler is actually invoked. For example, invoking an annotated controller requires resolving annotations.

The main purpose of a `HandlerAdapter` is to shiled the `DispatcherServlet` from such details.

- HandlerExceptionResolver: Strategy to resolve exceptions, possibly mapping them to handlers, to HTML error views, or other targets. See Exceptions.

- ViewResolver: Resolve logical `String`-based view names returned from a handler to an actual `View` with which to render to the response.

- LocaleResolver, LocaleContextResolver: Resolve the `locale` a client is using and possibly their time zone, in order to be able to offer internationalized views, See Locale.

- ThemeResolver: Resolve themes your web application can use - for example, to offer personlized layouts, See Themes.

- MultipartResolver: Abstraction for parsing a multi-part request (for example, browser from file upload) with the help of some multipart parsing library. See Multipart Resolver.

- FlashMapManager: Store and retrieve the "input" and the "output" `FlashMap` that can be used to pass attributes from one request to another, usually across a redirect. 

**Web MVC Config**

Applications can declare the infrastructure beans listed in Special Bean Types that are required to process requests. The DispatcherServlet checks the WebApplicationContext for each special bean. If there are no matching bean types, it falls back on the default types listed in DispatcherServlet.properties.

Spring Boot relies on the MVC Java configuration to configure Spring MVC and provides many extra convenient options.

**Servlet Config**

In a Servlet 3.0+ environment, you have the option of configuring the Servlet container programmatically as an alternative or in combination with a web.xml file. 

pass more info.

**Processing**

The `DispatcherServlet` processes requests as follows:

- The `WebApplicationContext` is searched for and bound in the request as an attribute that the controller and other elements in the process can use. It is bound by default under the DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE key.
- The locale resolver is bound to the request to let elements in the process resolve the locale to use when processing the request (rendering the view, preparing data, and so on). If you do not need locale resolving, you do not need the locale resolver.
- The theme resolver is bound to the request to let elements such as views determine which theme to use. If you do not use themes, you can ignore it. 
- If you specify a multipart file resolver, the request is inspected for multiparts.  If multiparts are found, the request is wrapped in a MultipartHttpServletRequest for further processing by other elements in the process.
- An appropriate handler is searched for. If a handler is found, the execution chain associated with the handler (preprocessors, postprocessors, and controllers) is executed in order to prepare a model or rendering. Alternatively, for annotated controllers, the response can be rendered (within the HandlerAdapter) instead of returning a view.
- If a model is returned, the view is rendered. If no model is returned (maybe due to a preprocessor or postprocessor intercepting the request, perhaps for security reasons), no view is rendered, because the request could already have been fulfilled.

The Spring DispatcherServlet also supports the return of the last-modification-date, as specified by the Servlet API.

**Interception**

All HandlerMapping implementations support handler interceptors that are useful when you want to apply specific functionality to certain requests — for example, checking for a principal. Interceptors must implement HandlerInterceptor from the org.springframework.web.servlet package with three methods that should provide enough flexibility to do all kinds of pre-processing and post-processing:

- preHandler(...): Before the actual handler is executed.
- postHandler(...): After the handler is executed.
- afterCompletion(...): After the complete request has finished

preHandler 返回bool值, 当返回false时, the handler execution chain continues break. DispatcherServlet assumes the interceptor itself has taken care of requests (and, for example, rendered an appropriate view) and does not continue executing the other interceptors and the actual handler in the execution chain.

See Interceptors in the section on MVC configuration for examples of how to configure interceptors. You can also register them directly by using setters on individual HandlerMapping implementations.

**Exceptions**

If an exception occurs during request mapping or is thrown from a request handler (such as a @Controller), the DispatcherServlet delegates to a chain of HandlerExceptionResolver beans to resolve the exception and provide alternative handling, which is typically an error response.

有一些默认实现: 

- HandlerExceptionResolver: A mapping between exception class names and error view names. Useful for rendering error pages in a browser application.
- SimpleMappingExceptionResolver: Resolves exceptions raised by Spring MVC and maps them to HTTP status codes. See also alternative ResponseEntityExceptionHandler and REST API exceptions.
- ResponseStatusExceptionResolver: Resolves exceptions with the @ResponseStatus annotation and maps them to HTTP status codes based on the value in the annotation.
- ExceptionHandlerExceptionResolver: Resolves exceptions by invoking an @ExceptionHandler method in a @Controller or a @ControllerAdvice class. See @ExceptionHandler methods.

**View Resolution**

Spring MVC defines the ViewResolver and View interfaces that let you render models in a browser without tying you to a specific view technology. ViewResolver provides a mapping between view names and actual views. View addresses the preparation of data before handing over to a specific view technology.

- AbstractCachingViewResolver: Sub-classes of AbstractCachingViewResolver cache view instances that they resolve. You can turn off the cache by setting the cache property to false. you can refresh a certain view at runtime.
- XmlViewResolver: Implementation of ViewResolver that accepts a configuration file written in XML with the same DTD as Spring’s XML bean factories. The default configuration file is /WEB-INF/views.xml.
- ResourceBundleViewResolver: 
- UrlBasedViewResolver:
- InternalResourceViewResolver:
- FreeMarkerViewResolver:
- ContentNegotiatingViewResolver:

Handling

Redirecting

Forwarding

Content Negotiation

**Locale**

pass

**Themes**

pass

**Multipart Resolver**

MultipartResolver from the org.springframework.web.multipart package is a strategy for parsing multipart requests including file uploads.

To enable multipart handling, you need to declare a MultipartResolver bean in your DispatcherServlet Spring configuration with a name of multipartResolver. The DispatcherServlet detects it and applies it to the incoming request. When a POST with content-type of multipart/form-data is received, the resolver parses the content and wraps the current HttpServletRequest as MultipartHttpServletRequest to provide access to resolved parts in addition to exposing them as request parameters.

**Logging**

pass

2. Filers

The spring-web module provides some useful filters:

- Form Data
- Forwarded Headers
- Shallow ETag
- CORS

**Form Data**

> Browsers can submit form data only through HTTP GET or HTTP POST but non-browser clients can also use HTTP PUT, PATCH, and DELETE. The Servlet API requires ServletRequest.getParameter*() methods to support form field access only for HTTP POST.
>
> The spring-web module provides FormContentFilter to intercept HTTP PUT, PATCH, and DELETE requests with a content type of application/x-www-form-urlencoded, read the form data from the body of the request, and wrap the ServletRequest to make the form data available through the ServletRequest.getParameter*() family of methods.

**Forwarded Headers**

pass

**Shallow ETag**

pass

**CORS**

Cross-Origin Resource Sharing, CORS.

3. Annotated Controllers

Spring MVC provides an annotation-based programming model where @Controller and @RestController components use annotations to express request mappings, request input, exception handling, and more

example:

```java
@Controller
public class HelloController {

    @GetMapping("/hello")
    public String handle(Model model) {
        model.addAttribute("message", "Hello World!");
        return "index";
    }
}
```

**Declaration**

You can define controller beans by using a standard Spring bean definition in the Servlet’s WebApplicationContext. The @Controller stereotype allows for auto-detection, aligned with Spring general support for detecting @Component classes in the classpath and auto-registering bean definitions for them. It also acts as a stereotype for the annotated class, indicating its role as a web component.

To enable auto-detection of such @Controller beans, you can add component scanning to your Java configuration, as the following example shows:

```java
@Configuration
@ComponentScan("org.example.web")
public class WebConfig {

    // ...
}
```

equivalent xml example:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="org.example.web"/>

    <!-- ... -->

</beans>
```

@RestController is a composed annotation that is itself meta-annotated with @Controller and @ResponseBody to indicate a controller whose every method inherits the type-level @ResponseBody annotation and, therefore, writes directly to the response body versus view resolution and rendering with an HTML template.

AOP Proxies

pass

**Request Mapping**

使用 `@RequestMapping` 注解将request映射到controllers methods. 该注解有various attribute to match by URL, HTTP method, request parameters, headers, and media types.

可用在class level or method level, There are also HTTP method specific shortcut variants of `@RequestMapping`:

`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`

`@RequestMapping`匹配所有method, example:

```java
@RestController
@RequestMapping("/persons")
class PersonController {

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable Long id) {
        // ...
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody Person person) {
        // ...
    }
}
```

URI patterns

You can map requests by using glob patterns and wildcards:

- ?: 匹配一个字符, "/pages/t?st.html" matches "/pages/test.html" and "/pages/t3st.html", not matches "/pages/teest.html"
- *: 匹配0或多个字符within a path segment, "/resources/*.png" matches "/resources/file.png", "/projects/*/versions" matches "/projects/spring/versions" but does not match "/projects/spring/boot/versions"
- **: 匹配0或多个segments until the end of the path, "/resources/**" matches "/resources/file.png" and "/resources/images/file.png"
- `{name}`: 匹配 a path segment and captures it as a variable named "name", "/projects/{project}/versions" matches "/projects/spring/versions" and captures project=spring
- `{name:[a-z]+}`: 带正则的匹配segment, "/projects/{project:[a-z]+}/versions" matches "/projects/spring/versions" but not "/projects/spring1/versions"

captured URI variables can be accessed with `@PathVariable`, example:

```java
class T {
    @GetMapping("/owners/{ownerId}/pets/{petId}")
    public Pet findPet(@PathVariable Long ownerId, @PathVariable Long petId) {
        // ...
    }
}
```

```java
@Controller
@RequestMapping("/owners/{ownerId}")
public class OwnerController {

    @GetMapping("/pets/{petId}")
    public Pet findPet(@PathVariable Long ownerId, @PathVariable Long petId) {
        // ...
    }
}
```

URI variables are automatically converted to the appropriate type, or TypeMismatchException is raised. Simple types (int, long, Date, and so on) are supported by default and you can register support for any other data type. See Type Conversion and DataBinder.

You can explicitly name URI variables (for example, @PathVariable("customId")), but you can leave that detail out if the names are the same and your code is compiled with debugging information or with the -parameters compiler flag on Java 8.

Pattern Comparision

当多个patterns match a URL, they must be compared to find the best match. 通过 `AntPathMatcher.getPatternComparator(String path)` look for patterns that are more specific.

越具体的URL会优先选择匹配, /public/** 之类总是最低

Suffix Match

Spring MVC 对默认执行 .*suffix pattern matching, e.g /person also match `/person.*`, 根据requested content type to use for the response(that is, instead of the Accept header) for example: /person.pdf, /person.xml

To completely disable the use of file extensions, you must set both of the following:

- useSuffixPatternMatching(false), see `PathMatchConfigurer`
- favorPathExtension(false), see `ContentNegotiationConfigurer`

Suffix Mathc and RFD

A reflected file download(RFD) attack is similar to XSS in that it relies on request input (for example, a query parameter and a URI variable) being reflected in the response. 

讲了一些避免的方法, pass.

Consumable Media Types

> You can narrow the request mapping based on the Content-Type of the request, as the following example shows:

可以限制request在某些Content-Type, 如:

```java
class T {
    @PostMapping(path = "/pets", consumes = "application/json") 
    public void addPet(@RequestBody Pet pet) {
        // ...
    }
}
```

The consumes 也可指定排除法, e.g: "!text/plain" 非"test/plain" Content-Type 的请求.

consumes 也可用于 class level, 不同于 `@RequestMapping` 这种函数再定义时拓展, 函数上的 consumes overrides rather than extends the class-level declaration.

Producible Media Types

也可限定request mapping based on the `Accept` request header and the list of content types that a controller method produces, as the follow example shows:

```java
class T{
    @GetMapping(path = "/pets/{petId}", produces = "application/json") 
    @ResponseBody
    public Pet getPet(@PathVariable String petId) {
        // ...
    }
}
```

method-level override class-level config

Parameters, headers

test whether a request parameters "myParam" equals "myValue" or its absence, example:

```java
class T {
    @GetMapping(path = "/pets/{petId}", params = "myParam=myValue") 
    public void findPet(@PathVariable String petId) {
        // ...
    }
}
```

or do same to headers, example:

```java
class T {
    @GetMapping(path = "/pets", headers = "myHeader=myValue") 
    public void findPet(@PathVariable String petId) {
        // ...
    }
}
```

HTTP HEAD, OPTIONS

pass

Customer Annotations

pass

Explicit Registrations

You can programmatically register handler methods. example:

```java
@Configuration
public class MyConfig {

    @Autowired
    public void setHandlerMapping(RequestMappingHandlerMapping mapping, UserHandler handler) 
            throws NoSuchMethodException {

        RequestMappingInfo info = RequestMappingInfo
                .paths("/user/{id}").methods(RequestMethod.GET).build(); 

        Method method = UserHandler.class.getMethod("getUser", Long.class); 

        mapping.registerMapping(info, handler, method); 
    }
}
```

**Handler Method**

`@RequestMapping` handler methods have a flexible signature and can choose from a range of supported controller method arguments and return values.

*Method Arguments*

pass

*Return Values*

pass

*Type Conversion*

Some annotated controller method arguments that represent String-based request input (such as @RequestParam, @RequestHeader, @PathVariable, @MatrixVariable, and @CookieValue) can require type conversion if the argument is declared as something other than String

*Matrix Variables*

name-value pairs in path segments, In Spring MVC, we refer to those as "Matrix Variables" based on an "old post" by Time Berners-Lee, they can be also be referred to as URI path parameters.

Matrix variables 被 semicolon(;) 分隔, 多个值由 comma(,), e.g `/cars;color=red,green;year=2012`, Multiple values can also be specified through repeated variable names, e.g `color=red;color=green;color=blue`

```java
class T {
    // GET /pets/42;q=11;r=22
    @GetMapping("/pets/{petId}")
    public void findPet(@PathVariable String petId, @MatrixVariable int q) {
    
        // petId == 42
        // q == 11
    }
}
```

多个path segments 可能含有 matrix variable, 消除歧义的 example:

or matrix variable default value.

```java
class T {
    // GET /owners/42;q=11/pets/21;q=22
    @GetMapping("/owners/{ownerId}/pets/{petId}")
    public void findPet(
            @MatrixVariable(name="q", pathVar="ownerId", required=false, defaultValue="1") int q1,
            @MatrixVariable(name="q", pathVar="petId") int q2) {
    
        // q1 == 11
        // q2 == 22
    }
}
```

需要配置相关启用: Note that you need to enable the use of matrix variables. In the MVC Java configuration, you need to set a UrlPathHelper with removeSemicolonContent=false through Path Matching. In the MVC XML namespace, you can set <mvc:annotation-driven enable-matrix-variables="true"/>.

*@RequestParam*

使用 `@RequestParam` 注解绑定请求参数(query parameters or form data) to a method argument in a controller, example:

```java
@Controller
@RequestMapping("/pets")
public class EditPetForm {
    // ...
    @GetMapping
    public String setupForm(@RequestParam("petId") int petId, Model model) { 
        Pet pet = this.clinic.loadPet(petId);
        model.addAttribute("pet", pet);
        return "petForm";
    }
}
```

默认的, 使用该注解的参数为必传的, 可以通过设置 attribute required=false or 声明参数为 `java.util.Optional` wrapper.

Type conversion 自动转换, 如果 target method parameter type is not string.

multiple parameter values for the same parameter name 的参数可以被定义为 array or list.

当 `@RequestParam` 注解被用于 Map<String, String> or MultiValueMap<String, String> 时, 不在注解中指定parameter name, 则自动解析参数name, value.

> Note that use of @RequestParam is optional (for example, to set its attributes). By default, any argument that is a simple value type (as determined by BeanUtils#isSimpleProperty) and is not resolved by any other argument resolver, is treated as if it were annotated with @RequestParam.

*@RequestHeader*

通过 `@RequestHeader` 注解, bind a request header to method argument in a controller, example:

```java
class T {
    @GetMapping("/demo")
    public void handle(
            @RequestHeader("Accept-Encoding") String encoding, 
            @RequestHeader("Keep-Alive") long keepAlive) { 
        //...
    }
}
```

同样, 当`@RequestHeader` 用于 `Map<String, String>`, `MultiValueMap<String, String>`, `HttpHeaders` 时, 所有header自动populated.

*@CookieValue*

`@CookieValue`注解, 用于取cookie的值(auto type conversion) example:

```java
// assume request with this cookie: JSESSIONID=415A4AC178C59DACE0B2C9CA727CDD84
class T {
    @GetMapping("/demo")
    public void handle(@CookieValue("JSESSIONID") String cookie) { 
        //...
    }
}
```

*@ModeAttribute*

`@ModeAttribute` 注解可用于 model (fields)自动的data binding, example:

```java
class T {
    @PostMapping("/owners/{ownerId}/pets/{petId}/edit")
    public String processSubmit(@ModelAttribute Pet pet) {
    }
}
```

The model `Pet` instance above is resolved as follows:

- From the model if already added by using Model.
- From the HTTP session by using @SessionAttributes.
- From a URI path variable passed through a Converter (see the next example).
- From the invocation of a default constructor.
- From the invocation of a “primary constructor” with arguments that match to Servlet request parameters. Argument names are determined through JavaBeans @ConstructorProperties or through runtime-retained parameter names in the bytecode.

more info pass

*@SessionAttributes*

pass

*@SessionAttribute*

pass

*Redirect Attributes*

example:

```java
class T {
    @PostMapping("/files/{path}")
    public String upload(Object ...args) {
        // ...
        return "redirect:files/{path}";
    }
}
```

*Flash Attributes*

Flash attributes provide a way for one request to store attributes that are intended for use in another. 

more info pass

*Multipart*

After a MultipartResolver has been enabled, the content of POST requests with multipart/form-data is parsed and accessible as regular request parameters. example:

```java
@Controller
public class FileUploadController {

    @PostMapping("/form")
    public String handleFormUpload(@RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            // store the bytes somewhere
            return "redirect:uploadSuccess";
        }
        return "redirect:uploadFailure";
    }
}
```

more info pass

*@RequestBody*

You can use the @RequestBody annotation to have the request body read and deserialized into an Object through an HttpMessageConverter. example:

```java
class T {
    @PostMapping("/accounts")
    public void handle(@RequestBody Account account) {
        // ...
    }
    // use combination with `javax.validation.Valid`
    @PostMapping("/accounts")
    public void handle2(@Valid @RequestBody Account account, BindingResult result) {
        // ...
    }
}
```

*HttpEntity*

pass

*@ResponseBody*

You can use the @ResponseBody annotation on a method to have the return serialized to the response body through an `HttpMessageConverter`. example:

```java
class T {
    @GetMapping("/accounts/{id}")
    @ResponseBody
    public Account handle() {
        // ...
    }
}
```

`@ResponseBody`注解也用于class-level, in which case it is inherited by all controller methods.

`@RestController`, 即为 `@Controller` 和 `@RespnoseBody` 两个meta-annotation 的combination.

You can use the Message Converters option of the MVC Config to configure or customize message conversion.

You can combine @ResponseBody methods with JSON serialization views. See Jackson JSON for details.

*ResponseEntity*

ResponseEntity is like @ResponseBody but with status and headers. For example:

```java
class T {
    @GetMapping("/something")
    public ResponseEntity<String> handle() {
        String body = "";
        String eTag = "";
        return ResponseEntity.ok().eTag(eTag).build(body);
    }
}
```

*Jackson JSON*

Sprint offers support for the jackson JSON library.

JSON Views example:

```java
@RestController
public class UserController {

    @GetMapping("/user")
    @JsonView(User.WithoutPasswordView.class)
    public User getUser() {
        return new User("eric", "7!jd#h23");
    }
}

public class User {

    public interface WithoutPasswordView {};
    public interface WithPasswordView extends WithoutPasswordView {};

    private String username;
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @JsonView(WithoutPasswordView.class)
    public String getUsername() {
        return this.username;
    }

    @JsonView(WithPasswordView.class)
    public String getPassword() {
        return this.password;
    }
}
```

**Model**

pass

**DataBinder**

pass

**Exception**

`@Controller` and `@ControllerAdvice` classes can have @ExceptionHandler methods to handle exceptions from controller methods, as the following example shows:

```java
@Controller
public class SimpleController {
    // ...
    @ExceptionHandler
    public ResponseEntity<String> handle(IOException ex) {
        // ...
    }

    @ExceptionHandler({FileSystemException.class, RemoteException.class})
    public ResponseEntity<String> handle(Exception ex) {
        // ...
    }
}
```

*Method Arguments*

pass

*Return Values*

pass

*REST API exceptions*

pass

**Controller Advice**

Typically @ExceptionHandler, @InitBinder, and @ModelAttribute methods apply within the @Controller class (or class hierarchy) in which they are declared. If you want such methods to apply more globally (across controllers), you can declare them in a class annotated with @ControllerAdvice or @RestControllerAdvice.

@ControllerAdvice is annotated with @Component, which means such classes can be registered as Spring beans through component scanning. @RestControllerAdvice is a composed annotation that is annotated with both @ControllerAdvice and @ResponseBody, which essentially means @ExceptionHandler methods are rendered to the response body through message conversion (versus view resolution or template rendering).

On startup, the infrastructure classes for @RequestMapping and @ExceptionHandler methods detect Spring beans annotated with @ControllerAdvice and then apply their methods at runtime. 

Global @ExceptionHandler methods (from a @ControllerAdvice) are applied after local ones (from the @Controller). By contrast, global @ModelAttribute and @InitBinder methods are applied before local ones.

By default, @ControllerAdvice methods apply to every request (that is, all controllers), but you can narrow that down to a subset of controllers by using attributes on the annotation, as the following example shows:

example:

```java
// Target all Controllers annotated with @RestController
@ControllerAdvice(annotations = RestController.class)
public class ExampleAdvice1 {}

// Target all Controllers within specific packages
@ControllerAdvice("org.example.controllers")
public class ExampleAdvice2 {}

// Target all Controllers assignable to specific classes
@ControllerAdvice(assignableTypes = {ControllerInterface.class, AbstractController.class})
public class ExampleAdvice3 {}
```

4. Functional Endpoints

a lightweight functional programming model in which functions are used to route and handle requests and contracts are designed for immutability. 

more info pass

5. URI Links

some example:

```java
class T {
    public static void main(String[] args){
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString("https://example.com/hotels/{hotel}")  
                .queryParam("q", "{q}")  
                .encode() 
                .build(); 
        
        URI uri = uriComponents.expand("Westin", "123").toUri(); 
        
        URI uri = UriComponentsBuilder
                .fromUriString("https://example.com/hotels/{hotel}")
                .queryParam("q", "{q}")
                .encode()
                .buildAndExpand("Westin", "123")
                .toUri();
        
        URI uri = UriComponentsBuilder
                .fromUriString("https://example.com/hotels/{hotel}")
                .queryParam("q", "{q}")
                .build("Westin", "123");

        URI uri = UriComponentsBuilder
                .fromUriString("https://example.com/hotels/{hotel}?q={q}")
                .build("Westin", "123");
    }
}
```

**UriBuilder**

pass

**URI Encoding**

pass

**Relative Servlet Requests**

pass

**Links to Controllers**

Spring MVC provides a mechanism to prepare links to controller methods. For example, the following MVC controller allows for link creation:

```java
@Controller
@RequestMapping("/hotels/{hotel}")
public class BookingController {

    @GetMapping("/bookings/{booking}")
    public ModelAndView getBooking(@PathVariable Long booking) {
        // ...
    }
}
```

You can prepare a link by referring to the method by name, as the following example shows:

```
UriComponents uriComponents = MvcUriComponentsBuilder
    .fromMethodName(BookingController.class, "getBooking", 21).buildAndExpand(42);

URI uri = uriComponents.encode().toUri();
```

more info pass

**Links in Views**

pass

6. Asynchronous Requests

Spring MVC has an extensive integration with Servlet 3.0 asynchronous request processing:

- DeferredResult and Callable return values in controller methods and provide basic support for a single asynchronous return value.
- Controllers can stream multiple values, including SSE(server send event) and raw data.
- Controllers can use reactive clients and return reactive types for response handling.

more info pass

7. CORS

Spring MVC lets you handle CORS (Cross-Origin Resource Sharing). This section describes how to do so.

The CORS specification distinguishes between preflight, simple, and actual requests. To learn how CORS works, you can read [this article](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS), among many others, or see the specification for more details.

more info pass

8. Web Security

link to another project [Spring Security](https://spring.io/projects/spring-security)

9. HTTP Caching

HTTP caching can significantly improve the performance of a web application.

HTTP caching 围绕 Cache-Control 相关的请求/响应头上作文章.

**CacheControl**

pass

**Controllers**

pass

**Static Resources**

pass

**ETage Filter**

pass

10. View Technologies

**Jackson**

The MappingJackson2JsonView uses the Jackson library’s ObjectMapper to render the response content as JSON.

MappingJackson2XmlView uses the Jackson XML extension’s XmlMapper to render the response content as XML. 

11. MVC Config

The MVC Java configuration and the MVC XML namespace provide default configuration suitable for most applications and a configuration API to customize it.

**Enable MVC Configuration**

In Java configuration, you can use the @EnableWebMvc annotation to enable MVC configuration, as the following example shows:

```java
@Configuration
@EnableWebMvc
public class WebConfig {
}
```

In XML configuration, you can use the <mvc:annotation-driven> element to enable MVC configuration, as the following example shows:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:mvc="http://www.springframework.org/schema/mvc"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/mvc
        https://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <mvc:annotation-driven/>
</beans>
```

**MVC Config API**

In Java configuration, you can implement the `WebMvcConfigurer` interface.

```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    // Implement configuration methods...
}
```

In XML, you can check attributes and sub-elements of `<mvc:annotation-driven/>`.

12. HTTP/2

pass

### REST Clients

**RestTemplate**

RestTemplate is a synchronous client to perform HTTP requests.

**WebClient**

WebClient is a non-blocking, reactive client to perform HTTP requests.

### Testing

This section summarizes the options available in spring-test for Spring MVC applications.

more info pass.

### WebSockets

This part of the reference documentation covers support for Servlet stack, WebSocket messaging that includes raw WebSocket interactions, WebSocket emulation through SockJS, and publish-subscribe messaging through STOMP as a sub-protocol over WebSocket.

**Introduction to WebSocket**

The WebSocket protocol, [RFC 6455](https://tools.ietf.org/html/rfc6455), provides a standardized way to establish a full-duplex, two-way communication channel between client and server over a single TCP connection. 

It is a different TCP protocol from HTTP but is designed to work over HTTP, using ports 80 and 443 and allowing re-use of existing firewall rules.

A WebSocket interaction begins with an HTTP request that uses the HTTP Upgrade header to upgrade or, in this case, to switch to the WebSocket protocol. The following example shows such an interaction:

```
GET /spring-websocket-portfolio/portfolio HTTP/1.1
Host: localhost:8080
Upgrade: websocket 
Connection: Upgrade 
Sec-WebSocket-Key: Uc9l9TMkWGbHFD2qnFHltg==
Sec-WebSocket-Protocol: v10.stomp, v11.stomp
Sec-WebSocket-Version: 13
Origin: http://localhost:8080
```

Instead of the usual 200 status code, a server with WebSocket support returns output similar to the following:

```
HTTP/1.1 101 Switching Protocols 
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: 1qVdfYHU9hPOl4JYYNXF623Gzn0=
Sec-WebSocket-Protocol: v10.stomp
```

After a successful handshake, the TCP socket underlying the HTTP upgrade request remains open for both the client and the server to continue to send and receive messages.

大体上: WebSocket 协议第一次也是HTTP协议的request, 然后协商升级后续使用WebSocket, 再详情的内容参见 RFC 6455.

**WebSocket API**

pass

**SockJS Fallback**

Over the public Internet, restrictive proxies outside your control may preclude WebSocket interactions, either because they are not configured to pass on the Upgrade header or because they close long-lived connections that appear to be idle.

The solution to this problem is WebSocket emulation — that is, attempting to use WebSocket first and then falling back on HTTP-based techniques that emulate a WebSocket interaction and expose the same application-level API.

On the Servlet stack, the Spring Framework provides both server (and also client) support for the SockJS protocol.

more info pass

**STOMP**

WebSocket 协议定义了2种类型的message(text and binary), 但content is undefined. 在websocket之上协商what kind of messages each can sed, what the format is, the content of each message, and so on.

The use of a sub-protocol is optional but, either way, the client and the server need to agree on some protocol that defines message content.

STOMP (Simple Text Oriented Messaging Protocol) was originally created for scripting languages (such as Ruby, Python, and Perl) to connect to enterprise message brokers. It is designed to address a minimal subset of commonly used messaging patterns. STOMP can be used over any reliable two-way streaming network protocol, such as TCP and WebSocket. Although STOMP is a text-oriented protocol, message payloads can be either text or binary.

more info pass

### Other Web Frameworks

This chapter details Spring’s integration with third-party web frameworks.

One of the core value propositions of the Spring Framework is that of enabling choice. In a general sense, Spring does not force you to use or buy into any particular architecture, technology, or methodology (although it certainly recommends some over others). 

more info pass
