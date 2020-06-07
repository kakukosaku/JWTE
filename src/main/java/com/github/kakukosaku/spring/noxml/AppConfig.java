package com.github.kakukosaku.spring.noxml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
        return new Hello();
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

    private String name;

    @Autowired
    public void setName(@Value("kaku-default-name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void greet() {
        System.out.println("Hi, " + name + " from bean...");
    }
}
