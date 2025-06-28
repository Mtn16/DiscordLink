package cz.bloodbear.discordLink.paper.utils;

public abstract class ConsoleColor {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    public static String text(String text, String color) {
        return color + text + RESET;
    }

    public static String red(String text) {
        return text(text, RED);
    }

    public static String green(String text) {
        return text(text, GREEN);
    }

    public static String yellow(String text) {
        return text(text, YELLOW);
    }

    public static String blue(String text) {
        return text(text, BLUE);
    }

    public static String magenta(String text) {
        return text(text, MAGENTA);
    }

    public static String cyan(String text) {
        return text(text, CYAN);
    }

    public static String white(String text) {
        return text(text, WHITE);
    }

}
