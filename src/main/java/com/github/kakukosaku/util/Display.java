package com.github.kakukosaku.util;

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
