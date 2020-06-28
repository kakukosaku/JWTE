package com.github.kakukosaku;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import javax.swing.*;

/**
 * Description
 *
 * @author kaku
 * Date    2020/6/28
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner runHook() throws Exception {
        return args -> {
            System.out.println("hi, spring-boot starting...");
        };
    }

}
