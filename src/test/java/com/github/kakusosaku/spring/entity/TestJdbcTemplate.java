package com.github.kakusosaku.spring.entity;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/30
 */
public class TestJdbcTemplate {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        JdbcTemplate jdbcTemplate = (JdbcTemplate) ctx.getBean("jdbcTemplate");
        String sql1 = "DROP TABLE IF EXISTS user";
        String sql2 = "CREATE TABLE `user`(id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(20) NOT NULL, password VARCHAR(32) NOT NULL)";
        jdbcTemplate.execute(sql1);
        jdbcTemplate.execute(sql2);
    }

}
