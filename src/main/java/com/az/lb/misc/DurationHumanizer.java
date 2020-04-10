package com.az.lb.misc;

public class DurationHumanizer {
    public static String humanizeTotal (String total) {
        return total.replace("P", "")
                .replace("T", "")
                .replace("D", "d ")
                .replace("H", "h ")
                .replace("M", "m ")
                .replace("S", "s ")
                .replace("  ", " ")
                .replace("  ", " ")
                ;
    }
}
