package cz.bloodbear.discordLink.paper.utils;


import com.google.gson.*;
import cz.bloodbear.discordLink.core.records.RoleEntry;
import cz.bloodbear.discordLink.paper.DiscordLink;
import io.leangen.geantyref.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class JsonConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Path configPath;
    private JsonObject jsonData;

    private final String filename;

    public JsonConfig(Path dataDirectory, String filename) {
        this.configPath = dataDirectory.resolve(filename);
        this.filename = filename;
        createDefaultConfig(filename);
        load();
    }

    private void createDefaultConfig(String filename) {
        if (!Files.exists(configPath)) {
            try (InputStream inputStream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(filename))) {
                Files.createDirectories(configPath.getParent());
                Files.copy(inputStream, configPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JsonObject loadDefaultMessages(String filename) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (inputStream != null) {
                Reader reader = new java.io.InputStreamReader(inputStream);
                return JsonParser.parseReader(reader).getAsJsonObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonObject();
    }

    private void mergeMessagesFromDefaults(String filename) {
        JsonObject defaultMessages = loadDefaultMessages(filename);
        JsonObject actualMessages = jsonData;

        boolean changed = mergeJsonObjectsRecursive(actualMessages, defaultMessages);
        if (changed) {
            createBackup();
            save();
        }
    }

    private void createBackup() {
        try {
            Path backupDir = configPath.getParent().resolve("_backup");
            Files.createDirectories(backupDir);

            String timestamp = java.time.LocalDateTime.now()
                    .toString()
                    .replace(":", "-")
                    .replace(".", "-");

            String backupFileName = configPath.getFileName().toString().replace(".json", "") + "_" + timestamp + ".json";
            Path backupFile = backupDir.resolve(backupFileName);

            Files.copy(configPath, backupFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Config backup created: " + backupFile.toAbsolutePath());

        } catch (IOException e) {
            DiscordLink.getInstance().getServer().getLogger().severe("An error occurred while creating config backup: " + e.getMessage());
        }
    }

    private boolean mergeJsonObjectsRecursive(JsonObject target, JsonObject source) {
        boolean changed = false;

        for (String key : source.keySet()) {
            JsonElement sourceElement = source.get(key);

            if (sourceElement.isJsonObject()) {
                JsonObject targetChild;
                if (target.has(key) && target.get(key).isJsonObject()) {
                    targetChild = target.getAsJsonObject(key);
                } else {
                    targetChild = new JsonObject();
                    target.add(key, targetChild);
                    changed = true;
                }

                if (mergeJsonObjectsRecursive(targetChild, sourceElement.getAsJsonObject())) {
                    changed = true;
                }

            } else {
                if (!target.has(key)) {
                    target.add(key, sourceElement);
                    changed = true;
                }
            }
        }

        return changed;
    }


    public void load() {
        if (!Files.exists(configPath)) {
            createDefaultConfig(configPath.getFileName().toString());
        }

        try (Reader reader = Files.newBufferedReader(configPath)) {
            jsonData = JsonParser.parseReader(reader).getAsJsonObject();
            mergeMessagesFromDefaults(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() { load(); }

    public void save() {
        try (Writer writer = Files.newBufferedWriter(configPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GSON.toJson(jsonData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonObject getSection(String sectionPath) {
        String[] keys = sectionPath.split("\\.");
        JsonObject current = jsonData;

        for (String key : keys) {
            if (current.has(key) && current.get(key).isJsonObject()) {
                current = current.getAsJsonObject(key);
            } else {
                JsonObject newSection = new JsonObject();
                current.add(key, newSection);
                current = newSection;
            }
        }
        return current;
    }

    public String getString(String path, String defaultValue) {
        String[] keys = path.split("\\.");
        JsonObject section = getSection(String.join(".", Arrays.copyOfRange(keys, 0, keys.length - 1)));
        String key = keys[keys.length - 1];

        return section.has(key) ? section.get(key).getAsString() : defaultValue;
    }

    public void setString(String path, String value) {
        String[] keys = path.split("\\.");
        JsonObject section = getSection(String.join(".", Arrays.copyOfRange(keys, 0, keys.length - 1)));
        section.addProperty(keys[keys.length - 1], value);
        save();
    }

    public Integer getInt(String path, Integer defaultValue) {
        String[] keys = path.split("\\.");
        JsonObject section = getSection(String.join(".", Arrays.copyOfRange(keys, 0, keys.length - 1)));
        String key = keys[keys.length - 1];

        return section.has(key) ? section.get(key).getAsInt() : defaultValue;
    }

    public void setInt(String path, Integer value) {
        String[] keys = path.split("\\.");
        JsonObject section = getSection(String.join(".", Arrays.copyOfRange(keys, 0, keys.length - 1)));
        section.addProperty(keys[keys.length - 1], value);
        save();
    }

    public Boolean getBoolean(String path, Boolean defaultValue) {
        String[] keys = path.split("\\.");
        JsonObject section = getSection(String.join(".", Arrays.copyOfRange(keys, 0, keys.length - 1)));
        String key = keys[keys.length - 1];

        return section.has(key) ? section.get(key).getAsBoolean() : defaultValue;
    }

    public void setBoolean(String path, Boolean value) {
        String[] keys = path.split("\\.");
        JsonObject section = getSection(String.join(".", Arrays.copyOfRange(keys, 0, keys.length - 1)));
        section.addProperty(keys[keys.length - 1], value);
        save();
    }

    public List<String> getStringList(String path) {
        String[] keys = path.split("\\.");
        JsonObject section = getSection(String.join(".", Arrays.copyOfRange(keys, 0, keys.length - 1)));
        String key = keys[keys.length - 1];

        if (section.has(key)) {
            Type listType = new TypeToken<List<String>>() {}.getType();
            return GSON.fromJson(section.get(key), listType);
        }
        return Collections.emptyList();
    }

    public List<RoleEntry> getRoles(String path) {

        List<RoleEntry> roleEntries = new ArrayList<>();

        jsonData.get(path).getAsJsonArray().forEach(element -> {
            roleEntries.add(new RoleEntry(element.getAsJsonObject().get("role_id").getAsString(), element.getAsJsonObject().get("permission").getAsString()));
        });

        return roleEntries;
    }

    public void setStringList(String path, List<String> values) {
        JsonArray jsonArray = new JsonArray();
        values.forEach(jsonArray::add);

        String[] keys = path.split("\\.");
        JsonObject section = getSection(String.join(".", Arrays.copyOfRange(keys, 0, keys.length - 1)));
        section.add(keys[keys.length - 1], jsonArray);
        save();
    }

    public JsonObject getSectionObject(String path) {
        return getSection(path);
    }
}