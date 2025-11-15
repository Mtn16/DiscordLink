package cz.bloodbear.discordLink.core.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public interface Bot {

    void updateCommands();
    Guild getGuild();
    JDA getJdaInstance();
    String getGuildId();
    String getClientId();
    void startAutoSync();
    void syncAllRoles();
    void syncRoles(String uuid);
    boolean hasPermission(String uuid, String permission);
    void removeSyncRoles(String memberId);
}