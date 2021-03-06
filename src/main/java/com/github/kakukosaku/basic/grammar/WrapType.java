package com.github.kakukosaku.basic.grammar;

/**
 * Description
 *
 * @author kaku
 * @date 2020-01-12
 */
public class WrapType {

    public static void main(String[] args) {
        // IntegerCache.cache 复用对象
        Integer i1 = 127;
        Integer i2 = 127;
        System.out.println(i1 == i2);

        Integer i3 = 128;
        Integer i4 = 128;
        System.out.println(i3 == i4);
        System.out.println(i3.equals(i4));

        boolean b = Integer.valueOf(1).equals(1);
        System.out.println(b);

        String s1 = "Hello";
        for (int i = 0; i < 5; i++) {
            s1 += " world";
        }
        System.out.println(s1);
        String s2 = new StringBuilder(s1).append(" end").toString();
        System.out.println(s2);
    }
}
