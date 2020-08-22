package com.github.kakukosaku.algo.sort;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Description
 *
 * @author kaku
 * Date    2020/8/6
 */
public class QuickSort {
    List<Integer> stack_use = new ArrayList<>();
    List<Integer> stack_bak = new ArrayList<>();

    public static void sort() {

    }

    static void quickSort(int[] arr) {

    }

    static void partition() {

    }

    public static void main(String[] args) {
        InputStream inputStream = new ByteArrayInputStream("12 23\n".getBytes());
        Scanner scanner = new Scanner(inputStream);
        String s = scanner.nextLine();
        String[] sArray = s.split(" ");
        System.out.println(Arrays.toString(sArray));
    }

}
