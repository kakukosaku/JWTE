package com.github.kakukosaku.collection;

import java.util.*;

/**
 * Description
 *
 * @author kaku
 * Date    4/3/21
 */
public class collectionInterface {

    public static void main(String[] args) {
        List<Integer> l = new ArrayList<>();
        l.add(1);
        l.addAll(Arrays.asList(2, 3));

        Integer[] arr = new Integer[3];
        Integer[] arr2 = l.toArray(arr);
        if (arr == arr2) {
            System.out.println(Arrays.toString(arr));
        }

        for (int v : l) {
            System.out.print(v);
        }
        System.out.println();

        l.stream().filter(e -> e < 10).forEach(System.out::print);
        System.out.println();
    }

}
