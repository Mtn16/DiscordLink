package cz.bloodbear.discordLink.paper.utils;


import com.google.gson.*;
import cz.bloodbear.discordLink.core.records.RoleEntry;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class JsonConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path configPath;
    private JsonObject jsonData;

    public JsonConfig(Path dataDirectory, String filename) {
        this.configPath = dataDirectory.resolve(filename);
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

    public void load() {
        if (!Files.exists(configPath)) {
            createDefaultConfig(configPath.getFileName().toString());
        }

        try (Reader reader = Files.newBufferedReader(configPath)) {
            jsonData = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        return null;
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