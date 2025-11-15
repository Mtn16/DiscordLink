package cz.bloodbear.discordLink.core.utils;

import com.google.gson.JsonObject;
import cz.bloodbear.discordLink.core.records.RoleEntry;

import java.util.List;

public interface Config {

    void reload();
    void save();
    String getString(String path, String defaultValue);
    void setString(String path, String value);
    Integer getInt(String path, Integer defaultValue);
    void setInt(String path, Integer value);
    Boolean getBoolean(String path, Boolean defaultValue);
    void setBoolean(String path, Boolean value);
    List<String> getStringList(String path);
    void setStringList(String path, List<String> values);
    List<RoleEntry> getRoles(String path);
    JsonObject getSectionObject(String path);
}
