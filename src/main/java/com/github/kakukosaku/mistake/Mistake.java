package com.github.kakukosaku.mistake;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Description
 *
 * @author kaku
 * @date 2020-01-19
 */
public class Mistake {

    public static void iteratorAndModifyError() {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        list.add("four");

        for (String s : list) {
            if ("two".equals(s)) {
                list.remove(s);
            }
        }

        System.out.println(list);
    }

    public static void iteratorAndModifyOk() {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        list.add("four");

        Iterator<String> i = list.iterator();
        while (i.hasNext()) {
            String item = i.next();
            if ("two".equals(item)) {
                i.remove();
            }
        }

        System.out.println(list);

    }

    public static void main(String[] args) {
        iteratorAndModifyOk();
        iteratorAndModifyError();
    }

}
