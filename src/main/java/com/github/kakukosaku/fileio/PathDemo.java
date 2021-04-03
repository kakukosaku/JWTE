package com.github.kakukosaku.fileio;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Description
 *
 * @author kaku
 * Date    3/28/21
 */
public class PathDemo {

    public static void main(String[] args) {
        Path p1 = Paths.get("/tmp/log/a.log");
        System.out.format("toUri: %s%n", p1.toUri());
        System.out.format("getFileName: %s%n", p1.getFileName());
        System.out.format("getName(0): %s%n", p1.getName(0));
        System.out.format("getParent: %s%n", p1.getParent());
        System.out.format("getRoot: %s%n", p1.getRoot());
        System.out.format("resolve: %s%n", p1.resolve("append.file"));

        try {
            p1.toRealPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
