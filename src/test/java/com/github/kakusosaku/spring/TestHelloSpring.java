package com.github.kakusosaku.spring;

import com.github.kakukosaku.spring.HelloSpring;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/29
 */
public class TestHelloSpring {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        HelloSpring helloSpring = (HelloSpring) ctx.getBean("helloSpring");
        helloSpring.show();
    }

}
