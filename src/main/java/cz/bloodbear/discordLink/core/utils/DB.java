package cz.bloodbear.discordLink.core.utils;

import cz.bloodbear.discordLink.core.records.DiscordAccount;

import java.util.Map;
import java.util.UUID;

public interface DB {

    void linkAccount(String uuid, String discordId, String discordUsername);

    DiscordAccount getDiscordAccount(String uuid);

    boolean isDiscordAccountLinked(String discordId);

    void unlinkAccount(String uuid);

    boolean isLinked(String uuid);

    void saveLinkRequest(String uuid, String code);

    String getPlayerByCode(String code);

    UUID getPlayerByDiscord(String discordId);

    void deleteLinkCodes(String uuid);

    void deleteLinkCodes();

    Map<String, String> getAllLinkedAccounts();

    void close();
}