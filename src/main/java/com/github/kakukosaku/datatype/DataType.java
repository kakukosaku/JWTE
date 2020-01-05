package com.github.kakukosaku.datatype;

import com.github.kakukosaku.util.Display;

public class DataType {

    public static void main(String[] args) {
        // int & Integer & long & array of int
        int i1 = 0;
        long l1 = 1L;
        Integer i2 = -2;
        // int[] intArray = {i1, 2, 3, 4, 5};
        // int[] intArray = {i1, 2, 3, 4, 5};
        // int[] intArray = new int[]{1, 2, 3, 4, 5};
        int[] intArray = new int[5];
        Display.displayArray(intArray);

        // init array
        intArray[0] = i1;
        intArray[1] = i2;
        intArray[2] = (int) l1;
        for (int i = 2; i < intArray.length; i++) {
            intArray[i] = i * 2;
        }
        Display.displayArray(intArray);


        // character & array of char
        char c = 'a';
        char[] charArray = {c, 'b'};
        Display.displayArray(charArray);

    }

}
