package com.github.kakukosaku.basic.controlflow;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/25
 */
public class ControlFlowEntryPoint {

    public static void main(String[] args) {
        // if
        var age = 18;
        if (age == 18) {
            System.out.println("you age (18) years old still young!");
        }

        // switch
        switch (age) {
            case 18:
                System.out.println("18 years old still young!");
            case 19:
                System.out.println("execute the next case if no break!");
                break;
            default:
                System.out.println("default case when no case match!");
        }

        // loop, for
        System.out.println("for loop");
        int[] arr = {0, 1, 2};
        for (var i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }

        // loop, foreach
        System.out.println("foreach loop");
        for (var i : arr) {
            System.out.println(arr[i]);
        }

        // loop, infinite for loop
        System.out.println("for infinite loop");
        forLoopLable:
        for (int i = 0; ; ) {
            if (i < arr.length) {
                System.out.println(arr[i]);
                i++;
            } else {
                break forLoopLable;
            }
        }

        // loop, while
        System.out.println("while loop");
        int i = 0;
        while (i < arr.length) {
            System.out.println(arr[i]);
            i++;
        }

        // loop, do-while
        System.out.println("do-while loop");
        i = 0;
        do {
            System.out.println(arr[i]);
            i++;
        } while (i < arr.length);

        // exception
        System.out.println("exception flow");
        String sNull = null;
        try {
            sNull.equals("exception flow");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            System.out.println("exception flow changed!");
        }
    }
}
