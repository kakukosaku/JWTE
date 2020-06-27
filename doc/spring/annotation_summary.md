# Spring Annotations

Ref: https://www.baeldung.com/tag/spring-annotations/

## Overview

- [Spring Core Annotations](#spring-core-annotations)
- [Spring Web Annotations](#spring-web-annotations)
- [Spring Boot Annotations](#spring-boot-annotations)

### Spring Core Annotations

**DI-Related Annotations:**

- `@Autowired`

mark a dependency which Spring is going to resolve and inject. can use this annotation with a constructor, setter, or field injection.

`@Autowired` has a boolean argument called `required` with default value of true.

用于依赖的自动装配, 当`required`为true却没有相应依赖bean to wire, an exception is thrown. otherwise, nothing is wired.

- `@Bean`

mark a factory method which instantiates a Spring bean. Spring calls these methods when a new instance of the return type is required.

if `@Bean` not specify arg `value`, the bean has the same name as the factory method.

Note, that all methods annotated with `@Bean` must be in `@Configuration` classes.

用于指明bean的工厂方法, 需要与`@Configuration`配套使用.

```java
class T {
    @Bean("engine")
    Engine getEngine() {
        return new Engine();
    }
}
```

- `@Qualifier`

We use @Qualifier along with @Autowired to provide the bean id or bean name we want to use in ambiguous situations.

for example:

```java
class Bike implements Vehicle {}
 
class Car implements Vehicle {}

class T {
    
    @Qualifier("car") 
    private Car car;

    @Autowired
    Biker(@Qualifier("bike") Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Autowired
    @Qualifier("bike") 
    void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
```

- `@Required`

mark a particular property is required!

checks if a particular property has been set or not. If a field has been annotated with @Required annotation and that field is not set, you will get `org.springframework.beans.factory.BeanInitializationException`

- `@Value`

se `@Value` for injecting property values into beans. It's compatible with constructor, setter, and field injection.

for example:

```java
import org.springframework.beans.factory.annotation.Value;
class T {
    // dynamic value from config.properties
    // engine.fuelType=8
    @Value("${engine.fuelType}")
    private int cylinderCount;

    Engine(@Value("8") int cylinderCount) {
        this.cylinderCount = cylinderCount;
    }

    @Autowired
    void setCylinderCount(@Value("8") int cylinderCount) {
        this.cylinderCount = cylinderCount;
    }
}
```

- `@DependsOn`

use this annotation to make Spring initialize **other beans before the annotated one**. Usually, this behavior is automatic, based on the explict dependencies between beans.

- `@Lazy`

use this annotation when we want to initialize our bean lazily. 

根据注解使用的地方不同, 有细致的区别. more info pass :)

This annotation has an argument named value with the default value of true. It is useful to override the default behavior.

for example:

```java
@Configuration
@Lazy
class VehicleFactoryConfig {
 
    @Bean
    @Lazy(false)  // specific this methods to eager loading.
    Engine engine() {
        return new Engine();
    }
}
``` 

- `@Lookup`

A method annotated with @Lookup tells Spring to return an instance of the method’s return type when we invoke it.

more info pass.

- `@Primary`

mark the most frequently used bean with @Primary it will be chosen on unqualified injection points, for example:

```java
@Component
@Primary
class Car implements Vehicle {}
 
@Component
class Bike implements Vehicle {}
 
@Component
class Driver {
    @Autowired
    Vehicle vehicle;
}
 
@Component
class Biker {
    @Autowired
    @Qualifier("bike")
    Vehicle vehicle;
}
```

- `@Scope`

use this annotation to define the scope of a `@Component` class or a `@Bean` definition. 

can be: prototype, singleton, request, session, application...etc.

- `@Configuration`

- `@Component`

- `@ComponetScan`

- `@Service`

- `@Repository`

**Context Configuration Annotations**

- `@Profile`

If we want Spring to use a @Component class or a @Bean method only when a specific profile is active, we can mark it with @Profile.

We can configure the name of the profile with the value argument of the annotation:

```java
@Component
@Profile("sportDay")
class Bike implements Vehicle {}
```

more info pass

- `@Import`

We can use specific `@Configuration` classes without component scanning with this annotation.

We can provide those classes with @Import‘s value argument:

```java
@Import(VehiclePartSupplier.class)
class VehicleFactoryConfig {}
```

- `@ImportResource`

We can import XML configurations with this annotation.

We can specify the XML file locations with the locations argument, or with its alias, the value argument:

```java
@Configuration
@ImportResource("classpath:/annotations.xml")
class VehicleFactoryConfig {}
```

- `@PropertyResource` & `@PropertySources`

With this annotation, we can define property files for application settings:

```java
@Configuration
@PropertySource("classpath:/annotations.properties")
class VehicleFactoryConfig {}
```

### Spring Web Annotations

In this tutorial, we'll explore Spring Web annotations from the `org.springframework.web.bind.annotation` package.

**Request Handling**

- `@RequestMapping`

marks request handler methods inside @Controller classes; it can be configured using:

1. path: or its aliases: name & value which URL the method is mapped to.
2. method: HTTP methods.
3. params: filters requests based on presence, absence, or value of HTTP parameters.
4. headers: filters requests based on presence, absence, or value of HTTP headers.
5. consumes: which media types the method can consume in the HTTP request body.
6. produces: which media types the method can produce in the HTTP response body.

for example:

```java
@Controller
class VehicleController {
 
    @RequestMapping(value = "/vehicles/home", method = RequestMethod.GET)
    String home() {
        return "home";
    }
}
```

We can provide default settings for all handler methods in a @Controller class if we apply this annotation on the class level.

The only exception is the URL which Spring won't override with method level settings but appends the two path parts.

for example:

```java
@Controller
@RequestMapping(value = "/vehicles", method = RequestMethod.GET)
class VehicleController {
 
    @RequestMapping("/home")  // real URL match: /vehicles/home
    String home() {
        return "home";
    }
}
```

`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, and `@PatchMapping` are different variants of `@RequestMapping` with the HTTP method already set to GET, POST, PUT, DELETE, and PATCH respectively.

- `@RequestBody`

use this annotation to map the body of HTTP request to an object, for example:

```java
class T {
    @PostMapping("/save")
    void saveVehicle(@RequestBody Vehicle vehicle) {
        // ...
    }
}
```

- `@PathVariable`

indicates that a method argument is bound to a URI template variable. 

for example:

```java
@Controller
@RequestMapping(value = "/vehicles", method = RequestMethod.GET)
class T {
    @RequestMapping("/{id}")
    Vehicle getVehicle(@PathVariable("id") long id) {
        // ...
    }
}
```

If the name of the part in the template matches the name of the method argument, we don't have to specify it in the annotation.

Moreover, we can mark a path variable optional by setting the argument required to false.

- `@RequestParam`

use this annotation for accessing HTTP request parameters.

for example:

```java
@Controller
@RequestMapping(value = "/vehicles", method = RequestMethod.GET)
class T {
    Vehicle getVehicleByParam(@RequestParam("id") long id) {
        // ...
    }
}
```

**Response Handling**

- `@ResponseBody`

If we mark a request handler method with @ResponseBody, Spring treats the result of the method as the response itself:

```java
class T {
    @ResponseBody
    @RequestMapping("/hello")
    String hello() {
        return "Hello World!";
    }
}
```

- `@ExceptionHandler`

use this annotation, we can declare a custom error handler method.

Spring calls this method when a request handler method throws any of the specified exceptions.

```java
class T {
    @ExceptionHandler(IllegalArgumentException.class)
    void onIllegalArgumentException(IllegalArgumentException exception) {
        // ...
    }
}
```

- `@ResponseStatus`

We can specify the desired HTTP status of the response if we annotate a request handler method with this annotation. 

```java
class T {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    void onIllegalArgumentException(IllegalArgumentException exception) {
        // ...
    }
}
```

**Other Web Annotations**

- `@Controller`

- `@RestController`

use this annotation we can access elements that are already in the model of an MVC @Controller.

```java
class T {
    @PostMapping("/assemble")
    void assembleVehicle(@ModelAttribute("vehicle") Vehicle vehicleInModel) {
        // ...
    }
}
```

Before Spring calls a request handler method, it invokes all @ModelAttribute annotated methods in the class.

- `@ModelAttribute`

With this annotation we can access elements that are already in the model of an MVC @Controller, by providing the model key:

```java
class T {
    @PostMapping("/assemble")
    void assembleVehicle(@ModelAttribute Vehicle vehicle) {
        // ...
    }
}
```

- `@CrossOrigin`

enables cross-domain communication for the annotated request handler methods.

### Spring Boot Annotations

Spring Boot made configuring Spring easier with its auto-configuration feature.

we'll explore the annotations from the `org.springframework.boot.autoconfigure` and `org.springframework.boot.autoconfigure.condition` packages.

- `@SpringBootApplication`

We use this annotation to mark the main class of a Spring Boot application, encapsulates `@Configuration`, `@EnableAutoConfiguration` and `@ComponentScan` annotations with their default attributes.

- `@EnableAutoConfiguration`

as its name says, enables auto-configuration. It means that Spring Boot looks for auto-configuration beans on its classpath and automatically applies them.

Note, that we have to use this annotation with @Configuration, for example:

```java
@Configuration
@EnableAutoConfiguration
class VehicleFactoryConfig {}
```

**Auto-Configuration Conditions**

- `@ConditionalOnClass` and `@ConditionalOnMissingClass`

Spring will only use the marked auto-configuration bean if the class in the annotation's argument is present/absent.

```java
@Configuration
@ConditionalOnClass(DataSource.class)
class MySQLAutoconfiguration {
    //...
}
```

- `@ConditionalOnBean` and `@ConditionalOnMissingBean`

We can use these annotations when we want to define conditions based on the presence or absence of a specific bean:

```java
@Configuration
class T {
    @Bean
    @ConditionalOnBean(name = "dataSource")
    LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // ...
    }
}
```

- `@ConditionalOnProperty`

With this annotation, we can make conditions on the values of properties:

```java
@Configuration
class T {
    @Bean
    @ConditionalOnProperty(
        name = "usemysql", 
        havingValue = "local"
    )
    DataSource dataSource() {
        // ...
    }
}
```

- `@ConditionalOnResource`

```java
@Configuration
class T {
    @ConditionalOnResource(resources = "classpath:mysql.properties")
    Properties additionalProperties() {
        // ...
    }
}
```

- `@ConditionalOnWebApplication` and `@ConditionalOnNotWebApplication`

example pass :)

- `@ConditionalExpression`

We can use this annotation in more complex situations. Spring will use the marked definition when the SpEL expression is evaluated to true:

```java
@Configuration
class T {
    @Bean
    @ConditionalOnExpression("${usemysql} && ${mysqlserver == 'local'}")
    DataSource dataSource() {
        // ...
    }
}
```

- `@Conditional`

For even more complex conditions, we can create a class evaluating the custom condition. We tell Spring to use this custom condition with @Conditional.



