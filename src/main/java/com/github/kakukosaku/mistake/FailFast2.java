package com.github.kakukosaku.mistake;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/28
 */
public class FailFast2 {

    static void badWay(List<? extends String> l) {
        // 仅仅是因为 .remove("two") 后, 剩余元素数量 size == cursor, 退出循环. 而没有运行 next() 做 checkForCoModification()
        for (String s: l) {
            if (s.equals("two")) {
                l.remove(s);
            }
        }
    }

    static void correctWay(List<? extends String> l) {
        Iterator<String> iterator = (Iterator<String>) l.iterator();

        while (iterator.hasNext()) {
            var item = iterator.next();
            if (item.equals("two")) {
                iterator.remove();
            }
        }
    }

    public static void main(String[] args) {

        List<String> l = new ArrayList<>();
        l.add("one");
        l.add("two");
        l.add("three");
        // cancel comment result in badWay CurrentModificationException
        // l.add("four");
        System.out.println(l);

        badWay(l);
        System.out.println(l);

        l.add("two");
        l.add("four");
        correctWay(l);
        System.out.println(l);
    }
}
