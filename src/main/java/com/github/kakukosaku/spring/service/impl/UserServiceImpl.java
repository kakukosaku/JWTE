package com.github.kakukosaku.spring.service.impl;

import com.github.kakukosaku.spring.dao.UserDAO;
import com.github.kakukosaku.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/29
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    UserDAO userDAO;

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public boolean login(String loginName, String loginPwd) {
        return userDAO.login(loginName, loginPwd);
    }
}
