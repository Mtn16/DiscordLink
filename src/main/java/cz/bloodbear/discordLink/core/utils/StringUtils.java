package cz.bloodbear.discordLink.core.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static String[] splitByString(String input, String delimiter) {
        if (delimiter.isEmpty()) {
            throw new IllegalArgumentException("Delimiter must not be empty");
        }

        List<String> result = new ArrayList<>();
        int index;
        int start = 0;

        while ((index = input.indexOf(delimiter, start)) != -1) {
            result.add(input.substring(start, index));
            start = index + delimiter.length();
        }

        result.add(input.substring(start));

        return result.toArray(new String[0]);
    }


}
