package com.github.kakukosaku.basic.interfaces;

import java.util.*;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/27
 * <p>
 * Compareable interface for the object that your wirte!
 */
class People implements Comparable<People> {

    int age;

    People(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("People{");
        sb.append("age=").append(age);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(People o) {
        if (o.age == this.age) {
            return 0;
        }

        if (o.age > this.age) {
            return -1;
        }

        return 1;
    }

    public static void main(String[] args) {
        People p1 = new People(18);
        People p2 = new People(20);

        People[] ps = new People[]{p2, p1, p2};
        Arrays.sort(ps);
        // another impl
        // Arrays.sort(ps, new PeopleComparator());
        System.out.println(Arrays.toString(ps));

        List<People> peopleList = new ArrayList<>();
        peopleList.add(p2);
        peopleList.add(p1);
        System.out.println(peopleList);

        peopleList.sort(new PeopleComparator());
        // peopleList.sort(People::compareTo);
        System.out.println(peopleList);

        Map<Integer, String> m = new HashMap<>();
        m.put(1, "kaku");
        System.out.println(m);
        m.merge(1, "default", (oldValue, newValue) -> oldValue + " " + newValue);
        m.merge(2, "default", (oldValue, newValue) -> oldValue + " " + newValue);
        System.out.println(m);
    }

}

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/27
 * <p>
 * Comparator interface for the object that out of you control!
 */
class PeopleComparator implements Comparator<People> {

    @Override
    public int compare(People o1, People o2) {
        return o1.compareTo(o2);
    }

}