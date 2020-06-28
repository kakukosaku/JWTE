package com.github.kakukosaku.basic.datatype;

import com.github.kakukosaku.util.Display;

import java.util.HashMap;

/**
 * Description
 *
 * @author kaku
 * @date 2020-01-12
 */
public class DataTypeEntryPoint<T> {

    T t;

    public static void main(String[] args) {
        // int & Integer & long & array of int
        int i1 = 0;
        long l1 = 1L;
        Integer i2 = -2;
        // int[] intArray = {1, 2, 3, 4, 5};
        // int[] intArray = new int[]{1, 2, 3, 4, 5};
        Integer[] intArray = new Integer[5];
        Display.displayArray(intArray);

        // init array
        intArray[0] = i1;
        intArray[1] = i2;
        intArray[2] = (int) l1;
        for (int i = 2; i < intArray.length; i++) {
            intArray[i] = i * 2;
        }
        Display.displayArray(intArray);

        // hash map
        HashMap<String, Integer> m = new HashMap <>();
        m.put("kaku", 18);
        m.put("gjs", 27);
        System.out.println(m);

        // character & array of char
        char c = 'a';
        Character[] charArray = {c, 'b'};
        Display.displayArray(charArray);

        // generic type
        DataTypeEntryPoint<String> d = new DataTypeEntryPoint<>();
        d.t = "kaku";
        System.out.println(d.t);

    }

}
