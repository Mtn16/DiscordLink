package cz.bloodbear.discordLink.core.utils;

import net.luckperms.api.LuckPerms;

import java.util.UUID;

public class LuckPermsProvider {
    public static boolean hasPermission(String uuid, String permission) {
        LuckPerms luckPerms = net.luckperms.api.LuckPermsProvider.get();
        net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(UUID.fromString(uuid));

        if (user == null) return false;

        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
}
