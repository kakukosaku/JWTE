package com.github.kakukosaku.basic.interfaces;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/27
 */
public class EqualInterface extends People {

    String name;

    EqualInterface(int age, String name) {
        super(age);
        this.name = name;
    }

    @Override
    public int hashCode() {
        return age + this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        People p = (People) obj;
        if (this.age == p.age) {
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        EqualInterface e1 = new EqualInterface(18, "kaku");
        EqualInterface e2 = new EqualInterface(18, "kaku");
        Set<EqualInterface> s = new HashSet<>();
        s.add(e1);
        s.add(e2);
        // expect: 1
        System.out.println(s.size());
        // expect: true
        System.out.println(Objects.equals(e1, e2));
    }
}
