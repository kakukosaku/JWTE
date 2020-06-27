package com.github.kakukosaku.serialization;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashMap;
import java.util.Map;

/**
 * Description
 *
 * @author kaku
 * Date    2020/6/26
 */

@JsonPropertyOrder({"username", "ID"})
@JsonIgnoreProperties(ignoreUnknown = true)
class User {

    private long id;
    private String username;
    private Map<String, String> otherAttr;

    @JsonGetter("ID")
    public long getId() {
        return id;
    }

    @JsonSetter("ID")
    public void setId(long id) {
        this.id = id;
    }

    @JsonAlias({"user_name", "userName"})
    public String getUsername() {
        return username;
    }

    // @JsonDeserialize(using = CustomerDateDeserializer.class)
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonAnyGetter
    public Map<String, String> getOtherAttr() {
        return otherAttr;
    }

    public void setOtherAttr(Map<String, String> otherAttr) {
        this.otherAttr = otherAttr;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

public class JacksonDemo {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "{\"ID\": 1, \"userName\": \"kaku\", \"uuid\": \"123\"}";

        try {
            System.out.println("original: " + jsonString);
            User user = objectMapper.readValue(jsonString, User.class);
            System.out.println(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println();

        User user = new User();
        user.setId(2);
        user.setUsername("kaku ko saku");
        user.setOtherAttr(new HashMap<>());
        Map<String, String> otherAttr = user.getOtherAttr();
        otherAttr.put("uuid", "uuid-2");
        otherAttr.put("login_password", "16-char-password");

        try {
            System.out.println(user);
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
