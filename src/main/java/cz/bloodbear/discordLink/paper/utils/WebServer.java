package cz.bloodbear.discordLink.paper.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import cz.bloodbear.discordLink.core.utils.ConsoleColor;
import cz.bloodbear.discordLink.paper.DiscordLink;
import cz.bloodbear.discordLink.core.records.DiscordAccount;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class WebServer {
    private HttpServer server;
    private final int port;
    private final boolean useDomain;
    private final String domain;

    public WebServer(int port, boolean useDomain, String domain) {
        this.port = port;
        this.useDomain = useDomain;
        this.domain = domain;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/callback", new OAuthCallbackHandler());
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        DiscordLink.getInstance().getLogger().info(ConsoleColor.green("Webserver is running on port " + port));
        if(useDomain) {
            DiscordLink.getInstance().getLogger().info(ConsoleColor.green("using custom domain " + domain));
        } else {
            DiscordLink.getInstance().getLogger().warning(ConsoleColor.yellow("Webserver is not using domain!"));
        }
    }

    public void stop() {
        if(server != null) {
            server.stop(0);
            DiscordLink.getInstance().getLogger().info(ConsoleColor.green("Webserver disabled."));
        }
    }

    private class OAuthCallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            if(useDomain) {
                String origin = exchange.getRequestHeaders().getFirst("Origin");
                if(domain.equalsIgnoreCase(origin)) {
                    sendResponse(exchange, 403, "Forbidden");
                    return;
                }
            }

            String query = exchange.getRequestURI().getQuery();
            String state = getQueryParam(query, "state");
            String code = getQueryParam(query, "code");

            if(state == null) {
               // sendResponse(exchange, 400, "State parameter missing");
                sendHtmlResponse(exchange, 400, DiscordLink.getInstance().getHtmlPage("stateMissing").getContent());
                return;
            }

            if(code == null) {
                //sendResponse(exchange, 400, "Code parameter missing");
                sendHtmlResponse(exchange, 400, DiscordLink.getInstance().getHtmlPage("codeMissing").getContent());
                return;
            }

            String uuid = DiscordLink.getInstance().getDatabaseManager().getPlayerByCode(state);
            if(uuid == null) {
                //sendResponse(exchange, 400, "Invalid or expired code");
                sendHtmlResponse(exchange, 400, DiscordLink.getInstance().getHtmlPage("invalid").getContent());
                return;
            }

            DiscordAccount discordAccount = DiscordLink.getInstance().getOAuth2Handler().getDiscordAccount(code);
            if(discordAccount == null) {
                //sendResponse(exchange, 400, "Failed to verify account.");
                sendHtmlResponse(exchange, 400, DiscordLink.getInstance().getHtmlPage("failed").getContent());
                return;
            }

            if(DiscordLink.getInstance().getDatabaseManager().isDiscordAccountLinked(discordAccount.id())) {
                sendHtmlResponse(exchange, 400, cz.bloodbear.discordLink.velocity.DiscordLink.getInstance().getHtmlPage("alreadylinked").getContent());
                return;
            }

            if(DiscordLink.getInstance().getDatabaseManager().isLinked(uuid)) {
                sendHtmlResponse(exchange, 400, cz.bloodbear.discordLink.velocity.DiscordLink.getInstance().getHtmlPage("alreadylinked").getContent());
                return;
            }

            DiscordLink.getInstance().getDatabaseManager().linkAccount(uuid, discordAccount.id(), discordAccount.username());
            sendHtmlResponse(exchange, 200, DiscordLink.getInstance().getHtmlPage("linked").getContent());
        }
    }

    private String getQueryParam(String query, String param) {
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] keyValue = pair.split("=");
            if(keyValue.length == 2 && keyValue[0].equals(param)) {
                return keyValue[1];
            }
        }
        return null;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void sendHtmlResponse(HttpExchange exchange, int statusCode, String htmlContent) throws IOException {
        byte[] responseBytes = htmlContent.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}
