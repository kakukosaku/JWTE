package com.github.kakukosaku.basic.specialmethods;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.Arrays;
import java.util.Objects;

/**
 * Description
 *
 * @author kaku
 * Date    3/20/21
 */
public class SpecialMethods implements Cloneable {

    int flag;
    public String name;

    SpecialMethods() {
        flag = 10086;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SpecialMethods that = (SpecialMethods) o;
        return flag == that.flag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(flag);
    }

    /**
     * @throws Throwable
     * @deprecated This method is deprecated =,-
     */
    @Override
    @Deprecated
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public static void main(String[] args) {
        SpecialMethods specialMethods2 = null;
        SpecialMethods specialMethods1 = new SpecialMethods();

        // Invoke clone()
        try {
            specialMethods2 = (SpecialMethods) specialMethods1.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            System.out.println("clone failed");
        }
        System.out.println(specialMethods1.flag);
        assert specialMethods2 != null;
        System.out.println(specialMethods2.flag);

        // Invoke equal()
        System.out.println(specialMethods1 == specialMethods2);
        System.out.println(specialMethods1.equals(specialMethods2));

        // Invoke getClass()
        Class<? extends SpecialMethods> c = new SpecialMethods().getClass();
        System.out.println(c.getSimpleName());
        System.out.println(c.getSuperclass().getSimpleName());

        // Invoke Class's method
        System.out.println(c.isInterface());
        System.out.println(Arrays.toString(c.getFields()));

    }

}
