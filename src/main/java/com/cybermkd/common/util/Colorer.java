package com.cybermkd.common.util;

import org.fusesource.jansi.Ansi;

/**
 * 创建人:T-baby
 * 创建日期: 16/8/5
 * 文件描述:
 */
public class Colorer {

    public static String black(String value) {
        return diy("black", value);
    }

    public static String white(String value) {
        return diy("white", value);
    }

    public static String green(String value) {
        return diy("green", value);
    }

    public static String yellow(String value) {
        return diy("yellow", value);
    }

    public static String magenta(String value) {
        return diy("magenta", value);
    }

    public static String red(String value) {
        return diy("red", value);
    }

    public static String cyan(String value) {
        return diy("cyan", value);
    }

    public static String blue(String value) {
        return diy("blue", value);
    }

    private static String diy(String color, String value) {
        return String.valueOf(Ansi.ansi().eraseScreen().render("@|" + color + " " + value + "|@"));
    }

    public static void main(String[] args){
        System.out.println(white("测试颜色"));
        System.out.println(black("测试颜色"));
        System.out.println(green("测试颜色"));
        System.out.println(yellow("测试颜色"));
        System.out.println(magenta("测试颜色"));
        System.out.println(cyan("测试颜色"));
        System.out.println(blue("测试颜色"));
        System.out.println(red("测试颜色"));
    }


}
