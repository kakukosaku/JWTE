package com.github.kakukosaku.spring.dao.impl;

import com.github.kakukosaku.spring.dao.UserDAO;
import com.github.kakukosaku.spring.entity.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/29
 */
@Repository("userDAO")
public class UserDAOImpl implements UserDAO {

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean login(String loginName, String loginPwd) {
        if ("kaku".equals(loginName) && "password".equals(loginPwd)) {
            return true;
        }
        return false;
    }

    @Override
    public User findUserById(int id) {
        String sql = "SELECT * FROM user WHERE id=?";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        return this.jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    @Override
    public List<User> findAllUser() {
        String sql = "SELECT * FROM user";
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        return this.jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public int addUser(User user) {
        String sql = "INSERT INTO user(username, password) values(?, ?)";
        Object[] args = new Object[]{user.getUsername(), user.getPassword()};
        return this.jdbcTemplate.update(sql, args);
    }

    @Override
    public int updateUser(User user) {
        String sql = "UPDATE user SET username=?, password=? WHERE id=?";
        Object[] args = new Object[]{user.getUsername(), user.getPassword()};
        return this.jdbcTemplate.update(sql, args);
    }

    @Override
    public int deleteUser(int id) {
        String sql = "DELETE FROM user WHERE id=?";
        return this.jdbcTemplate.update(sql, id);
    }
}
