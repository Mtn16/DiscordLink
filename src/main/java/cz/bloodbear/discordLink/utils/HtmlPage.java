package cz.bloodbear.discordLink.utils;

import cz.bloodbear.discordLink.DiscordLink;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class HtmlPage {
    private final Path pagePath;
    private String content;

    public HtmlPage(Path dataDirectory, String filename) {
        this.pagePath = dataDirectory.resolve(filename);
        createDefaultPage(filename);
        load();
    }

    private void createDefaultPage(String filename) {
        if (!Files.exists(pagePath)) {
            try (InputStream inputStream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(filename))) {
                Files.createDirectories(pagePath.getParent());
                Files.copy(inputStream, pagePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        if (!Files.exists(pagePath)) {
            createDefaultPage(pagePath.getFileName().toString());
        }

        try {
            content = Files.readString(pagePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            DiscordLink.getInstance().getLogger().error(e.getMessage());
        }
    }

    public String getContent() { return content; }
}
