package com.github.kakukosaku.basic.datatype;


/**
 * Description
 *
 * @author kaku
 * Date    2/28/21
 */
abstract class AbstractCls {
}

public class NestClass {

    String name;

    static class NestStaticCls {
    }

    class LocalCls {
    }

    interface InterfaceOfTest {
    }

    public static void main(String[] args) {
        // Usage of static nested class
        NestStaticCls o1 = new NestStaticCls();
        NestStaticCls o2 = new NestClass.NestStaticCls();

        // Usage of non-static nested class
        // 1. Local cls
        LocalCls o3 = new NestClass().new LocalCls();
        // 2. Anonymous cls
        AbstractCls o4 = new AbstractCls() {
        };
        InterfaceOfTest o5 = new InterfaceOfTest() {
        };
    }
}
