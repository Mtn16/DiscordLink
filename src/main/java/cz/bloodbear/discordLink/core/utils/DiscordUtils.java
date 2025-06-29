package cz.bloodbear.discordLink.core.utils;

import net.dv8tion.jda.api.entities.Member;

public abstract class DiscordUtils {
    public static String getOAuthLink(String clientId, String redirectUri, String linkCode, String scope) {
        return "https://discord.com/oauth2/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=" + scope
                + "&state=" + linkCode;
    }

    public static String getOAuthLink(String clientId, String redirectUri, String linkCode) {
        String scope = "identify";

        return "https://discord.com/oauth2/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=" + scope
                + "&state=" + linkCode;
    }

    public static boolean hasRole(Member member, String roleId) {
        return member.getRoles().stream().anyMatch(role -> role.getId().equals(roleId));
    }

}
