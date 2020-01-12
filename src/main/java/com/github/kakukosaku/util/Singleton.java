package com.github.kakukosaku.util;

import java.io.Serializable;

/**
 * 实现了序列化接口的单化模式
 *
 * @author kaku
 * @version 1.0.0
 */
public class Singleton implements Serializable {

    private static volatile Singleton singleton;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (singleton == null) {
            singleton = new Singleton();
        }
        return singleton;
    }

    public static void main(String[] args) {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        System.out.println(s1);
        System.out.println(s2);
    }

    /**
     * 解决反序列化破坏单例模式
     *
     * @return singleton
     */
    private Object readResolve() {
        return singleton;
    }
}
