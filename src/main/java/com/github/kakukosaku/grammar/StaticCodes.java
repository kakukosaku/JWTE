package com.github.kakukosaku.grammar;

/**
 * Description
 *
 * @author kaku
 * @version 1.0.0
 */
public class StaticCodes {

    static String prior = "done";
    static String last = choicePrior(true) ? getLast() : prior;

    static {
        System.out.println("From static codes, last:" + last);
        getLast();
    }

    public static boolean choicePrior(boolean b) {
        return b;
    }

    public static String getLast() {
        return "Hello world";
    }

    public static void main(String[] args) {
        System.out.println(StaticCodes.last);
    }
}
