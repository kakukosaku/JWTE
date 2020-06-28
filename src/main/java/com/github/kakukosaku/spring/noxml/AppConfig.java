package com.github.kakukosaku.spring.noxml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Description
 *
 * @author kaku
 * Date    2020/6/7
 */
@Configuration
public class AppConfig {

    @Bean(initMethod = "init")
    @Scope("singleton")
    Hello getGreetTool() {
        return new Hello();
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        // ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        ctx.register(AppConfig.class);
        ctx.refresh();
        Hello hi = (Hello) ctx.getBean("getGreetTool");
        hi.greet("name-passed-by-ref");
    }

}


class Hello {

    private String name;

    public void setName(@Value("kaku-default-name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void init() {
        System.out.println("Hello init method is invoked!");
    }

    public void greet(String name) {
        System.out.println("Hi, " + name + " from bean...");
    }
}
