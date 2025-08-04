package cz.bloodbear.discordLink.core.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateChecker {
    private static final String API_URL = "https://api.modrinth.com/v2/project/discordlink/version";
    private final String CURRENT_VERSION;
    private final String TARGET_LOADER;
    private String LATEST = null;

    private final Gson gson = new Gson();

    private final OkHttpClient client = new OkHttpClient();

    public UpdateChecker(String CURRENT, String LOADER) {
        this.CURRENT_VERSION = CURRENT;
        this.TARGET_LOADER = LOADER;
    }

    public String getLatestVersion() {
        if(LATEST != null) return LATEST;
        Request request = new Request.Builder()
                .url(API_URL)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if(!response.isSuccessful() || response.body() == null) return null;

            String json = response.body().string();
            JsonArray versions = gson.fromJson(json, JsonArray.class);

            for (JsonElement element : versions) {
                JsonObject versionObj = element.getAsJsonObject();

                if(!versionObj.get("version_type").getAsString().equalsIgnoreCase("release")) continue;

                JsonArray loaders = versionObj.getAsJsonArray("loaders");
                for (JsonElement loader : loaders) {
                    if(loader.getAsString().equalsIgnoreCase(TARGET_LOADER)) {
                        LATEST =versionObj.get("name").getAsString();
                        return LATEST;
                    }
                }
            }

            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    public boolean isNewerVersionAvailable() {
        String latest = getLatestVersion();
        return latest != null && !latest.equals(CURRENT_VERSION);
    }
}
