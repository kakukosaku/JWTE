package com.github.kakukosaku.basic.datatype;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/26
 */
public class ConversionEntryPoint {

    public static void main(String[] args) {
        // String to int
        int i = Integer.parseInt("1");
        System.out.println(i);

        // String to Integer
        Integer iInteger = Integer.valueOf("2");
        System.out.println(iInteger);

        // String + int
        // expect: 200100
        System.out.println("100" + 101);

        // int to String
        System.out.println(String.valueOf(102));

        // Integer ot String
        System.out.println(Integer.valueOf(103).toString());

        // format String
        String s = String.format("%d", 104);
        System.out.println(s);

        // String to Date
        Date now = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            now = dateFormat.parse("2020/05/26");
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            System.out.println(now);
            System.out.println(dateFormat.format(now));
        }
    }
}
