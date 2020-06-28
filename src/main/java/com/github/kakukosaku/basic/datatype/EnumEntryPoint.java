package com.github.kakukosaku.basic.datatype;

/**
 * Description
 *
 * @author kaku
 * Date    2020/6/25
 */
public enum EnumEntryPoint {
    Phone("手机") {
        @Override
        String getSize() {
            return "small";
        }
    },
    Pad("平板") {
        @Override
        String getSize() {
            return "medium";
        }
    },
    Computer("电脑") {
        @Override
        String getSize() {
            return "big";
        }
    },
    Television("电视") {
        @Override
        String getSize() {
            return "big";
        }
    };

    private final String translation;

    EnumEntryPoint(String translation) {
        this.translation = translation;
    }

    abstract String getSize();

    public String getTranslation() {
        return this.translation;
    }

    public static void main(String[] args) {
        for (EnumEntryPoint e : EnumEntryPoint.values()) {
            System.out.println(e + " " + e.getTranslation());
        }
    }
}
