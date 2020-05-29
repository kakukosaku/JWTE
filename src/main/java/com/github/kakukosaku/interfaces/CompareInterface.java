package com.github.kakukosaku.interfaces;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/27
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

        List<People> peopleList = new ArrayList<>();
        peopleList.add(p2);
        peopleList.add(p1);
        System.out.println(peopleList);

        peopleList.sort(new PeopleComparator());
        // peopleList.sort(People::compareTo);
        System.out.println(peopleList);
    }

}

class PeopleComparator implements Comparator<People> {

    @Override
    public int compare(People o1, People o2) {
        return o1.compareTo(o2);
    }
}