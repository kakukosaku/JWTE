package com.github.kakusosaku.spring.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Description
 *
 * @author kaku
 * Date    2020/6/20
 */
class Person {
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Person{");
        sb.append("name='").append(name).append('\'');
        sb.append(", age=").append(age);
        sb.append('}');
        return sb.toString();
    }

}

public class TestJson {

    @Test
    public void testJackson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Person p = new Person();
        p.setName("kaku");
        p.setAge(18);
        System.out.println(objectMapper.writeValueAsString(p));

        String jsonStr = "{\"name\":\"kaku\",\"age\":18}";
        Person p2 = objectMapper.readValue(jsonStr, Person.class);
        System.out.println(p2);
    }

}
