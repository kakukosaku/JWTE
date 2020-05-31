package com.github.kakusosaku.spring.dao;

import com.github.kakukosaku.spring.dao.UserDAO;
import com.github.kakukosaku.spring.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/30
 */
public class TestJdbcTemplate {

    @Test
    public void addUserTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserDAO userDAO = (UserDAO) ctx.getBean("userDAO");

        User u1 = new User();
        u1.setUsername("kaku");
        u1.setPassword("password");

        int rest = userDAO.addUser(u1);
        assert rest == 1;
        System.out.println(rest);
    }

    @Test
    public void findUserByIdTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserDAO userDAO = (UserDAO) ctx.getBean("userDAO");

        User u1 = userDAO.findUserById(1);
        System.out.println(u1);
    }

    @Test
    public void findAllUserTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserDAO userDAO = (UserDAO) ctx.getBean("userDAO");

        List<User> users = userDAO.findAllUser();
        System.out.println(users);
    }
}
