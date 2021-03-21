package com.github.kakukosaku.basic.genericCls;


/**
 * Description
 *
 * @author kaku
 * Date    3/21/21
 */
public class GenericCls<K, V> {

    K k;
    V v;

    GenericCls() {
    }

    GenericCls(K k, V v) {
        this.k = k;
        this.v = v;
    }

    GenericCls<K, V> setKey(K k) {
        this.k = k;
        return this;
    }

    GenericCls<K, V> setValue(V v) {
        this.v = v;
        return this;
    }

    public static void main(String[] args) {
        GenericCls<String, Integer> genericCls = new GenericCls<>();
        Utils.echo(genericCls.setKey("kaku").setValue(18));

        GenericCls<String, Integer> genericCls2 = new GenericCls<>("", 0);
        genericCls2.setKey("kaku.ko.saku").setValue(19);
        Utils.<String, Integer>echo(genericCls2.k, genericCls2.v);
    }

}

class Utils {

    static <K, V> void echo(GenericCls<K, V> genericCls) {
        if (genericCls == null) {
            System.out.println("No output for avoid NPE");
            return;
        }
        System.out.println(genericCls.k.toString() + "\t" + genericCls.v.toString());
    }

    static <K extends String, V extends Number> void echo(K k, V v) {
        System.out.println(k.toString() + "\t" + v.toString());
    }

}