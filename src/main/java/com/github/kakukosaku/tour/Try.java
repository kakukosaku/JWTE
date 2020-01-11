package com.github.kakukosaku.tour;

public class Try {

    private int n;

    protected Try(int n) {
        this.n = n;
    }

    class InstanceInnerClass{}

    static class StaticInnerClass{}

    private static void func() {
    }

    public static void main(String[] args) {
        (new Thread(){}).start();
        (new Thread(){}).start();

        System.out.println(3 % -2);
        Try.func();

        class MethodClass1{};
        class MethodClass2{};
    }

}


class Son extends Try {

    protected Son(int n) {
        super(n);
    }

}
