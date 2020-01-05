package com.github.kakukosaku.util;

public class Display {

    private Display() {
    }

    public static void displayArray(int[] array) {
        System.out.println("Display Array:");
        System.out.print("\t[ ");
        for (int i : array) {
            System.out.print(i + " ");
        }
        System.out.print("]\n");
    }

    public static void displayArray(char[] array) {
        System.out.println("Display Array:");
        System.out.print("\t[ ");
        for (char i : array) {
            System.out.print(i + " ");
        }
        System.out.print("]\n");
    }

}
