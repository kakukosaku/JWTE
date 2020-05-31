package com.github.kakukosaku.basic.mistake;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/28
 */
public class FailFast1 {

    public static void main(String[] args) {
        List<String> masterList = new ArrayList<>();
        masterList.add("one");
        masterList.add("two");
        masterList.add("three");
        masterList.add("four");
        masterList.add("five");

        List<String> branchList = masterList.subList(0, 3);
        // origin list structural modifications may yield incorrect results or RuntimeException ConcurrentModificationException.
         masterList.remove(0);
        // masterList.add("ten");
        // masterList.clear();

        branchList.clear();
        branchList.add("six");
        branchList.add("seven");
        branchList.remove(0);

        for (Object t: branchList) {
            System.out.println(t);
        }

        System.out.println(masterList);
    }
}
