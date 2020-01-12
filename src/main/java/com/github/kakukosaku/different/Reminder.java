package com.github.kakukosaku.different;

/**
 * % actually means `reminder` 取余, java.lang.Math.floorMod 才是取模
 *
 * 计算步骤:
 * 1. 求整数商: c = a/b
 * 2. 计算模或余数: r = a - c*b
 *
 * 取余与取模的不同: 在于计算c值时的舍入方向不同
 * 求模: 负无穷方向舍入
 * 求余: 向0方向舍入
 *
 * @author kaku
 * @date 2020-01-12
 */
public class Reminder {

    public static void main(String[] args) {
        // % -> 取余
        int i = -3 % 2;
        System.out.println(i);

        // 取模
        int i2 = Math.floorMod(-3 , 2);
        System.out.println(i2);
    }

}
