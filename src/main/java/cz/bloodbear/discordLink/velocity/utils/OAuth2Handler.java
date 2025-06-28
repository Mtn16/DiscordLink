package cz.bloodbear.discordLink.velocity.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cz.bloodbear.discordLink.velocity.DiscordLink;
import cz.bloodbear.discordLink.velocity.records.DiscordAccount;
import okhttp3.*;

import java.io.IOException;

public class OAuth2Handler {
    private final String CLIENT_ID;
    private final String CLIENT_SECRET;
    private final String REDIRECT_URI;

    private final OkHttpClient httpClient;

    public OAuth2Handler(String CLIENT_ID, String CLIENT_SECRET, String REDIRECT_URI) {
        this.CLIENT_ID = CLIENT_ID;
        this.CLIENT_SECRET = CLIENT_SECRET;
        this.REDIRECT_URI = REDIRECT_URI;

        this.httpClient = new OkHttpClient();
    }

    private String getAccessToken(String code) throws IOException {
        RequestBody formBody = (new FormBody.Builder())
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET)
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", REDIRECT_URI)
                .build();
        Request request = (new Request.Builder()).url("https://discord.com/api/oauth2/token").post(formBody).build();

        try (Response response = this.httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                return jsonObject.get("access_token").getAsString();
            }
        }

        return null;
    }

    public DiscordAccount getDiscordAccount(String code) {
        try {
            String accessToken = getAccessToken(code);

            DiscordAccount accountDetails = getAccountDetails(accessToken);
            return new DiscordAccount(accountDetails.id(), accountDetails.username());
        } catch (IOException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }
        return null;
    }

    private String extractValue(String json, String key) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        return jsonObject.has(key) ? jsonObject.get(key).getAsString() : null;
    }

    private DiscordAccount getAccountDetails(String accessToken) {
        try {
            Request request = (new Request.Builder())
                    .url("https://discord.com/api/users/@me")
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            try (Response response = this.httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    String userId = responseBody.substring(responseBody.indexOf("\"id\":\"") + 6);
                    userId = userId.substring(0, userId.indexOf("\""));
                    String username = responseBody.substring(responseBody.indexOf("\"username\":\"") + 12);
                    username = username.substring(0, username.indexOf("\""));
                    return new DiscordAccount(userId, username);
                }
            }
        } catch (IOException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }
        return null;
    }
}
