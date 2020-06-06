package com.github.kakukosaku.spring.noxml;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description
 *
 * @author kaku
 * Date    2020/6/7
 */
@Configuration
public class AppConfig {

    @Bean
    Hello getGreetTool() {
        return new Hello("kaku");
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        // ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        ctx.register(AppConfig.class);
        ctx.refresh();
        Hello hi = (Hello) ctx.getBean("getGreetTool");
        hi.greet();
    }

}


class Hello {

    String name;

    Hello(String name) {
        this.name = name;
    }

    public void greet() {
        System.out.println("Hi, " + name + " from bean...");
    }
}
