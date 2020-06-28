package com.github.kakukosaku.basic.datatype;

import java.util.Arrays;
import java.util.List;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/25
 */
public class CompositeEntryPoint {

    public static void p(char[] v) {
        System.out.println(v);
    }

    /**
     * composite type: array
     */
    public static void main(String[] args) {
        char[] charArray = {'h', 'e', 'l', 'l', 'o'};
        p(charArray);
        char[] charArray1 = new char[1];
        charArray1[0] = ',';
        p(charArray1);
        char[] charArray2 = new char[]{'w', 'o', 'r', 'l', 'd', '!'};
        p(charArray2);
    }

}
