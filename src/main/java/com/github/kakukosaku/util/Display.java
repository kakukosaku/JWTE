package com.github.kakukosaku.util;

/**
 * Description
 *
 * @author kaku
 * @date 2020-01-12
 */
public class Display {

    private Display() {
    }

    public static <E> void displayArray(E[] array) {
        System.out.println("Display Array:");
        System.out.print("\t[ ");
        for (E i : array) {
            System.out.print(i + " ");
        }
        System.out.print("]\n");
    }

}
