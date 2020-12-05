package com.github.kakukosaku.basic.datatype;

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
        boolean t = false;
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

        // long
        long lInt = 3L;
        p(lInt);

        // float
        float f = 1.1f;
        p(f);

        // double
        double fDouble = 1.1;
        p(fDouble);

        // Integer
        // Integer auto-boxing
        Integer iInteger = 1;
        p(iInteger);

        // Long
        Long iLong = 1L;
        p(iLong);

        // Float
        Float fObject = 1.5F;
        p(fObject);

        // Double
        Double dObject = 1.5D;
        p(dObject);

        // Boolean
        Boolean tObject = Boolean.valueOf("true");
        p(tObject);
    }

}
