package com.github.kakusosaku.mybatis;

import com.github.kakukosaku.mybatis.mapper.IdCardMapper;
import com.github.kakukosaku.mybatis.mapper.PersonMapper;
import com.github.kakukosaku.mybatis.pojo.IdCard;
import com.github.kakukosaku.mybatis.pojo.Person;
import com.github.kakukosaku.mybatis.pojo.User;
import com.github.kakukosaku.mybatis.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;
import java.util.List;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/31
 */
public class MybatisTest {

    private SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;

    @Test
    public void init() {
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession();

        UserMapper uInterface = sqlSession.getMapper(UserMapper.class);
        User u1 = uInterface.selectUserById(1);
        System.out.println(u1);

        List<User> users = sqlSession.selectList("selectUserByUsername", "kaku");
        System.out.println(users);
    }

    @Test
    public void relationshipTest() {
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession();

        PersonMapper pm = sqlSession.getMapper(PersonMapper.class);
        Person person = pm.selectPersonById(2);
        System.out.println(person);

        IdCardMapper idm = sqlSession.getMapper(IdCardMapper.class);
        IdCard idCard = idm.selectIdCardById(1);
        System.out.println(idCard);
    }

}
