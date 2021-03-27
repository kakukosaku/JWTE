package com.github.kakukosaku.ioCls;


/**
 * Description
 *
 * @author kaku
 * Date    3/24/21
 */
public class FmtCls {

    public static void main(String[] args) {
        // %n equal to \n, in linux
        System.out.format("Hi I'm %s, %d years old.%n", "kaku", 18);
        System.out.println("---");
        System.out.format("%f, %1$+020.10f %n", Math.PI);
        System.out.format("%f, %1$+20.10f %n", Math.PI);

    }

}
