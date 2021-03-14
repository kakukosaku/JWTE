package com.github.kakukosaku.basic.interfaces;

import java.util.Iterator;

public interface CustomInterface {

    // All fields in interface are implicitly public static final, so can *omit* those modifiers.
    public static final String name = "const fields, actually";

    String whatYouName();

    /**
     * default implemented method.
     */
    default void echo() {
        System.out.println(whatYouName());
    }

    static void cannotOverride() {
    }

}

/**
 * This class is demo of how to implements a interface.
 * <p>
 * You can implements multiple interface, separate by comma(,)
 * <p>
 * By convention, the implements clause follows the extends clause,
 */
class IntroduceOneSelf implements CustomInterface, Iterable<Character> {

    String name;
    Iterator<Character> iterator;
    char[] args;

    IntroduceOneSelf(char... args) {
        this.args = args;
        this.iterator = new IntroduceIterator(args);
    }

    // Uncomment this line will cause compile error!
    // Because interface's static methods are not allowed override!
    // @Override
    public static void cannotOverride() {
    }

    public synchronized static int getInt() {
        return 0;
    }

    IntroduceOneSelf(String name) {
        this.name = name;
    }

    @Override
    public String whatYouName() {
        return name;
    }

    public static void main(String[] args) {
        CustomInterface i = new IntroduceOneSelf("anonymous");
        i.echo();
        IntroduceOneSelf kaku = new IntroduceOneSelf("kaku");
        kaku.echo();
        kaku.cannotOverride();

        IntroduceOneSelf kakuList = new IntroduceOneSelf('k', 'a', 'k', 'u');
        for (Character c : kakuList) {
            System.out.println(c);
        }

    }

    public Iterator<Character> iterator() {
        return iterator;
    }
}

class IntroduceIterator implements Iterator<Character> {

    char[] args;
    int position;

    IntroduceIterator(char... args) {
        this.args = args;
        this.position = 0;
    }

    @Override
    public boolean hasNext() {
        return position < args.length;
    }

    @Override
    public Character next() {
        if (!hasNext()) {
            return null;
        }
        return args[position++];
    }
}
