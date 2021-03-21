package com.github.kakukosaku.ioCls;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Description
 *
 * @author kaku
 * Date    2020/6/25
 */
public class FileDemo {
    public static void main(String[] args) {
        File file = new File("/");
        String[] fs = file.list((dir, name) -> !name.startsWith("."));

        // Arrays.sort(fs, String.CASE_INSENSITIVE_ORDER);
        List<String> arrayList = Arrays.asList(fs != null ? fs : new String[]{});
        Collections.sort(arrayList);

        for (String f : arrayList.subList(0, 6)) {
            System.out.println(f);
        }

        try (FileWriter fileWriter = new FileWriter("./t.log");) {
            fileWriter.write("name=kaku\nage = 18\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileReader fileReader = new FileReader("./t.log")) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            System.out.println(bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream fileInputStream = new FileInputStream("./t.log")) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            System.out.println(properties.getProperty("name"));
            String age = properties.getProperty("age");
            System.out.println(age.getClass().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(file.exists());
    }
}
