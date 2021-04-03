package com.github.kakukosaku.fileio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Description
 *
 * @author kaku
 * Date    3/28/21
 */
public class FilesDemo {

    public static void main(String[] args) {
        Path p1 = Paths.get("/tmp/log/a.log");
        System.out.format("Files.exists(p): %s%n", Files.exists(p1));
        System.out.format("Files.notExists(p): %s%n", Files.notExists(p1));

        byte[] b;
        try {
            b = Files.readAllBytes(Paths.get("t.log").toAbsolutePath());
            System.out.format(new String(b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
