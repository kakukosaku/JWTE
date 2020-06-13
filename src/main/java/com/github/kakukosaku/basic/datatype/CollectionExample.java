package com.github.kakukosaku.basic.datatype;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/24
 */
class CollectionExampleBase {
}

class CollectionExampleChild extends CollectionExample {
}

public class CollectionExample extends CollectionExampleBase {

    public static <E> void p(E[] arr) {
        System.out.print("[");
        for (E v : arr) {
            System.out.printf("%s, ", v);
        }
        System.out.print("\b\b]\n");
    }

    public static <E> void p(List<E> arr) {
        System.out.print("[");
        for (E v : arr) {
            System.out.printf("%s, ", v);
        }
        System.out.print("\b\b]\n");
    }

    public static void mapExample() {
        // HashMap, k,v nullable
        Map<String, Integer> m = new HashMap<>();
        m.put("kaku", 18);
        m.put("gjs", 27);
        Set<String> mKeys = m.keySet();
        System.out.println(m);

        mKeys.clear();
        System.out.println(m);

        m.put("kaku", 18);
        m.put("gjs", 27);
        // duplicate element, nullable k,v
        ((HashMap<String, Integer>) m).put(null, null);
        m.put("kaku", 19);
        System.out.println(m.get("no-exist-key"));
        System.out.println(m.containsKey("no-exist-key"));

        System.out.println(m);
        Collection<Integer> mValues = m.values();

        mValues.remove(null);
        System.out.println(m);

        // TreeMap, k not nullable, v nullable
        Map<String, Integer> m2 = new TreeMap<>();
        m2.putAll(m);
        m2.put("one", 1);
        m2.put("two", 2);
        m2.put("three", 3);
        System.out.println(m2);
        for (var i : m2.keySet()) {
            System.out.print(i + " ");
            System.out.println(m2.get(i));
        }

        // ConcurrentHasMap k,v not nullable
        Map<String, Integer> m3 = new ConcurrentHashMap<>(m2);
        System.out.println(m3);

        // HashTable deprecated, k,v not nullable
    }

    public static void listAndGenericType() {
        // 完全没有类型限制和赋值限定
        List a1 = new ArrayList();
        a1.add(new Object());
        a1.add(new Integer(1));
        a1.add(new String("hello, from a1!"));
        p(a1);

        // 把 a1 引用赋值给 a2(比 a1 增加了泛型限制 <Object>)
        List<Object> a2 = a1;
        a2.add(new Object());
        a2.add(new Integer(2));
        a2.add(new String("hello, from a2!"));
        p(a2);

        // 把 a1 引用赋值给 a3(比 a1 增加泛型 <Integer>)
        List<Integer> a3 = a1;
        a3.add(new Integer(3));
        // 下2行编译出错, 不允许增加非 Integer 的元素
        // a3.add(new Object());
        // a3.add(new String("hello, from a3!"))

        // 把 a1 引用赋值给 a4(比 a1 增加通配符 ?)
        List<?> a4 = a1;

        a1.remove(0);
        a4.remove(2);
        // 使用通配符集合 List<?> 不能添加元素, 但可以remove, clear, 并非immutable集合;
        // 一般作为参数接收外部的集合, 或返回一个不知类型的集合.
        // a4.add(new Integer(4));

        // Ref:
        // https://stackoverflow.com/questions/4343202/difference-between-super-t-and-extends-t-in-java
        //
        // Object -> Number -> Integer -> CustomInteger
        //              |----> Double
        // List<? extends Number>:
        // 1. 可以赋值给 `T 和 T 的子类` 的**集合**
        //    e.g. List<? extends Number> foo3 = new ArrayList<Number>();  means, Number extends Number
        //         List<? extends Number> foo3 = new ArrayList<Integer>(); means, Integer extends Number
        // 2. 可以从 List<? extends Number> 返回带类型的元素, 但只能是 **Number本身或Number的父类的对象, 如 Object**;
        //  2.1 extends 限制类型上界
        //  2.2 extends 能保证集合中的元素的类型 extends 自 Number(有可能为Integer, Double, 所以无法确定具体是什么, 但至少是Number), 也可以是Object
        //  2.3 所以说 extends 子类型的信息丢失了
        // 3. 不能添加元素, 但可添加 `null`.
        List<? extends CollectionExample> listExtendsCollectionExample = new ArrayList<>();
        // Compile error, can add element except null!
        // listExtendsCollectionExample.add(new CollectionExample());
        listExtendsCollectionExample.add(null);
        CollectionExample c = listExtendsCollectionExample.get(0);
        System.out.println(c);
        p(listExtendsCollectionExample);

        // List<? super Integer>:
        // 1. 可以赋值给 `T 及 T 的父类` 的**集合**
        //    e.g. List<? super Integer> foo3 = new ArrayList<Integer>();  means, Integer is a superclass of Integer
        //         List<? super Integer> foo3 = new ArrayList<Number>();   means, Number is a superclass of Integer
        // 2. 可以从 List<? super Integer> 返回元素, 但丢失了类型信息, 只能返回 Object;
        //  2.1 super 限制类型下界
        //  2.2 super 能保证集合中的元素的类型, 是 `Integer` 的 superclass, 但具体是哪一个不知道
        //  2.3 所以说, 不用来取元素一般
        // 3. 可以添加元素, 但只能为 T本身或T的子类的对象.
        List<? super CollectionExample> listSuperCollectionExample = new ArrayList<>();
        listSuperCollectionExample.add(new CollectionExample());
        Object c2 = listSuperCollectionExample.get(0);
        p(listSuperCollectionExample);
        System.out.println(c2);
        CollectionExample c3 = (CollectionExample) c2;
        System.out.println(c3);

    }

    public static void arrayListExample() {
        // raw array
        String[] array = new String[3];
        array[0] = "one";
        array[1] = "two";
        array[2] = "three";
        p(array);

        // convert raw array -> List
        List<String> stringList = Arrays.asList(array);

        // 修改转换后的集合
        stringList.set(0, "oneList");
        p(stringList);
        p(array);
        // 不能使用 .add, .remove, .clear 方式, 编译不报错, 运行时异常!
        // stringList.add("four");

        // ArrayList
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(array));
        arrayList.set(0, "one");
        arrayList.add("four");
        p(arrayList);

        // convert ArrayList -> raw array
        // if .toArray(array) array size is enough, ArrayList ele copy to `array`, else allocate new array!
        var array1 = arrayList.toArray(array);
        p(array);
        p(array1);
        System.out.println(array == array1);

    }

    public static void main(String[] args) {
        // Array, List
        arrayListExample();

        // List & Generic Type
        listAndGenericType();

        // Map
        mapExample();
    }

}
