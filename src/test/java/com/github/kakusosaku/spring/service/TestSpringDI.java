package com.github.kakusosaku.spring.service;

import com.github.kakukosaku.spring.HelloSpring;
import com.github.kakukosaku.spring.dao.UserDAO;
import com.github.kakukosaku.spring.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/29
 */
public class TestSpringDI {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationAnnotationContext.xml");
        UserService userService = (UserService) ctx.getBean("userService");
        System.out.println(userService);
        boolean flag = userService.login("kaku", "password");
        System.out.println(flag);

        ApplicationContext c2 = new ClassPathXmlApplicationContext("applicationAnnotationContext.xml");

        HelloSpring hi = (HelloSpring) ctx.getBean("helloSpring");
        hi.show();

        UserDAO u1 = (UserDAO) ctx.getBean("userDAO");
        System.out.println(u1);
        UserDAO u2 = (UserDAO) c2.getBean("userDAO");
        System.out.println(u2);
        UserDAO u3 = (UserDAO) c2.getBean("userDAO");
        System.out.println(u3);
    }

}
