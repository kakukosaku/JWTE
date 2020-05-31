package com.github.kakukosaku.mybatis.mapper;

import com.github.kakukosaku.mybatis.pojo.Person;

public interface PersonMapper {

    Person selectPersonById(int id);

}
