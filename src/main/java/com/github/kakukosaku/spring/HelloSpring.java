package com.github.kakukosaku.spring;

import org.springframework.stereotype.Component;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/29
 */
@Component("helloSpring")
public class HelloSpring {
    private String userName;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void show() {
        System.out.println("Hello from, " + userName);
    }
}
