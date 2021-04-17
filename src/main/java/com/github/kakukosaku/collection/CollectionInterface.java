package com.github.kakukosaku.collection;

import java.util.*;

/**
 * Description
 *
 * @author kaku
 * Date    4/3/21
 */
public class CollectionInterface {

    public static void main(String[] args) {
        // List operation
        List<Integer> l = new ArrayList<>();
        l.add(1);
        l.addAll(Arrays.asList(2, 3));
        Integer[] arr = new Integer[3];
        Integer[] arr2 = l.toArray(arr);
        if (arr == arr2) {
            System.out.println(Arrays.toString(arr));
        }
        for (int v : l) {
            System.out.print(v);
        }
        System.out.println();

        // sublist operation
        System.out.println(l.subList(1, 3));
        System.out.println(l.subList(1, 3).indexOf(3));

        // Collection & stream operation
        Collections.shuffle(l);
        l.stream().filter(e -> e < 10).forEach(System.out::print);
        System.out.println();

        // Set operation
        Set<Integer> s = new LinkedHashSet<>(Arrays.asList(1, 2, 2, 3));
        System.out.println(s);

        // Queue operation
        Queue<Integer> queue = new LinkedList<>();
        System.out.println("Queue: FIFO");
        for (int i = 0; i < 3; i++) {
            queue.add(i);
        }
        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }

        // Stack operation
        System.out.println("Stack: LIFO");
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < 3; i++) {
            stack.push(i);
        }
        while (!stack.isEmpty()) {
            System.out.println(stack.pop());
        }

        // Map operation
        Map<String, Integer> map = new HashMap<>();
        map.put("kaku", 18);
        map.put("kaku.ko.saku", 18);
        System.out.println(map);
        System.out.println("map.values() return: " + map.values());

        // ConcurrentModificationException 如果只是在for-each中删除倒数第2个元素, hasNext() curr != size 返回false, 直接结束.
        // 走不到 next() 中对modCount中检查.
        System.out.println("How to \"avoid\" ConcurrentModificationException check:");
        List<Integer> l2 = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        for (int i : l2) {
            if (i == 3) {
                l2.remove(Integer.valueOf(i));
                // l2.remove(i);
            }
        }
        System.out.println(l2);

        // Iterator 中提供的 remove() 删除最近next的值, 并重置了 expectedModCount 的值 = modCount
        List<Integer> l3 = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        for (Iterator<Integer> it = l3.iterator(); it.hasNext(); ) {
            if (it.next() == 2) {
                it.remove();
            }
        }

        // Collections operation
        Collections.sort(l, Collections.reverseOrder());
        Collections.reverse(l);
        System.out.println(l);

        Integer[] arr3 = new Integer[]{1, 2, 3, 4, 5};
        Arrays.sort(arr3, Collections.reverseOrder());
        System.out.println(Arrays.toString(arr3));

        // List.sublist
        System.out.println("list subList:");
        System.out.println(l);
        l.subList(0, l.size());
        System.out.println("list subList changed, original one:");
        System.out.println(l);

        // SortedSet & SortedMap
        SortedSet<Integer> sortedSet = new TreeSet<>();
        SortedMap<String, Integer> sortedMap = new TreeMap<>(map);
        System.out.println("sortedMap travel:");
        for (var iterator = sortedMap.keySet().iterator(); iterator.hasNext(); ) {
            var k = iterator.next();
            System.out.println("sortedMap key:" + k + "\t" + "value:" + sortedMap.get(k));
        }

        // 计算
        System.out.println(Integer.numberOfLeadingZeros(4 - 1));
        System.out.println(-1 >>> Integer.numberOfLeadingZeros(4 - 1));
    }

}
