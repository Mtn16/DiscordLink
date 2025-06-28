package cz.bloodbear.discordLink.velocity.utils;

import com.velocitypowered.api.proxy.Player;
import cz.bloodbear.discordLink.velocity.DiscordLink;
import cz.bloodbear.discordLink.velocity.records.DiscordAccount;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager(String host, int port, String database, String username, String password, boolean useSSL) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL + "&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try {
            connection = DriverManager.getConnection(url, username, password);
            createTable();
        } catch (SQLException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS linked_accounts (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "discord_id VARCHAR(25) NOT NULL, " +
                "discord_username VARCHAR(255) NOT NULL" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }

        String sql2 = "CREATE TABLE IF NOT EXISTS link_requests ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "uuid VARCHAR(36) NOT NULL, "
                + "code VARCHAR(64) NOT NULL UNIQUE, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql2);
        } catch (SQLException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }
    }

    public void linkAccount(String uuid, String discordId, String discordUsername) {
        String sql = "INSERT INTO linked_accounts (discord_id, discord_username, uuid) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE discord_id = ?, discord_username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, discordId);
            stmt.setString(2, discordUsername);
            stmt.setString(3, uuid);
            stmt.setString(4, discordId);
            stmt.setString(5, discordUsername);
            stmt.executeUpdate();
        } catch (SQLException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }

        String sql2 = "DELETE FROM link_requests WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql2)) {
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }

        Optional<Player> player = DiscordLink.getInstance().getServer().getPlayer(UUID.fromString(uuid));
        player.ifPresent(value -> value.sendMessage(MiniMessage.miniMessage().deserialize(DiscordLink.getInstance().getMessage("command.discord.linked", value))));
        DiscordLink.getInstance().getDiscordBot().syncRoles(uuid);
    }

    public DiscordAccount getDiscordAccount(String uuid) {
        String sql = "SELECT discord_id, discord_username FROM linked_accounts WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new DiscordAccount(rs.getString("discord_id"), rs.getString("discord_username"));
            }
        } catch (SQLException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }
        return null;
    }

    public void unlinkAccount(String uuid) {
        DiscordLink.getInstance().getDiscordBot().removeSyncRoles(getDiscordAccount(uuid).id());

        String sql = "DELETE FROM linked_accounts WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }
    }

    public boolean isLinked(String uuid) {
        String query = "SELECT COUNT(*) FROM linked_accounts WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }
        return false;
    }

    public void saveLinkRequest(String uuid, String code) {
        String query = "INSERT INTO link_requests (uuid, code) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE code = VALUES(code)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid);
            stmt.setString(2, code);
            stmt.executeUpdate();
        } catch (SQLException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }
    }

    public String getPlayerByCode(String code) {
        String query = "SELECT uuid FROM link_requests WHERE code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("uuid");
            }
        } catch (SQLException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }
        return null;
    }

    public Map<String, String> getAllLinkedAccounts() {
        Map<String, String> linkedAccounts = new HashMap<>();

        String query = "SELECT uuid, discord_id FROM linked_accounts";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                linkedAccounts.put(rs.getString("uuid"), rs.getString("discord_id"));
            }
        } catch (Exception e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }

        return linkedAccounts;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }
    }
}
