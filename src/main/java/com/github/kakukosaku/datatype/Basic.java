package com.github.kakukosaku.datatype;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/24
 */
public class Basic {

    public static <T> void p(T v) {
        System.out.printf("typeOf(%s):\t%s\n", v, v.getClass().getSimpleName());
    }

    /**
     * Primitive data type examples.
     */
    public static void main(String[] args) {

        // bool, 1 bit
        boolean t;
        t = true;
        p(t);

        // byte, 1 byte
        byte b = 97;
        p(b);

        // char, 2 byte
        char c = 'a';
        p(c);

        // int, 4 byte
        int i = 1;
        p(i);

        // short
        short sInt = 1;
        p(sInt);

        // int
        int iInt = 2;
        p(iInt);

        // long
        long lInt = 3L;
        p(lInt);

        // float
        float f = 1.1f;
        p(f);

        // double
        double fDouble = 1.1;
        p(fDouble);
    }

}
