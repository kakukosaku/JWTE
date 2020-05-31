package com.github.kakukosaku.mybatis.mapper;

import com.github.kakukosaku.mybatis.pojo.User;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/31
 */
public interface UserMapper {

    User selectUserById(int id);

}
