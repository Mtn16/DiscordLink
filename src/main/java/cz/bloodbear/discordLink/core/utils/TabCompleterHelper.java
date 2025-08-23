package cz.bloodbear.discordLink.core.utils;

import java.util.ArrayList;
import java.util.List;

public class TabCompleterHelper {
    public static List<String> getArguments(List<String> choices, String argument) {
        List<String> arguments = new ArrayList<>();
        choices.forEach(choice -> {
            if(choice.toLowerCase().startsWith(argument.toLowerCase())) {
                arguments.add(choice);
            }
        });

        return arguments;
    }
}
