package com.github.kakukosaku.spring.dao;

import com.github.kakukosaku.spring.entity.User;

import java.util.List;

public interface UserDAO {

    boolean login(String loginName, String loginPwd);

    User findUserById(int id);

    List<User> findAllUser();

    int addUser(User user);

    int updateUser(User user);

    int deleteUser(int id);
}
