package com.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class App {
    private final int port;
    private HttpServer server;
    private final ScoreRepository scoreRepository = new ScoreRepository();

    public App() {
        this(getPortFromEnv());
    }

    public App(int port) {
        this.port = port;
    }

    private static int getPortFromEnv() {
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.isBlank()) {
            try {
                return Integer.parseInt(portEnv);
            } catch (NumberFormatException ignored) {
            }
        }
        return 8080;
    }

    public String getGreeting() {
        return "Java Retro Games Hub is ready!";
    }

    public ScoreRepository getScoreRepository() {
        return scoreRepository;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/games", new GamesHandler());
        server.createContext("/api/scores", new ScoresHandler(scoreRepository));
        server.createContext("/api/status", new StatusHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Java Games Arcade Server running at http://localhost:" + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) {
        App app = new App();
        try {
            app.start();
            System.out.println("Press Ctrl+C to exit.");
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class ScoreEntry {
        private final String player;
        private final String game;
        private final int score;
        private final long timestamp;

        public ScoreEntry(String player, String game, int score, long timestamp) {
            this.player = player;
            this.game = game;
            this.score = score;
            this.timestamp = timestamp;
        }

        public String getPlayer() {
            return player;
        }

        public String getGame() {
            return game;
        }

        public int getScore() {
            return score;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String toJson() {
            return "{\"player\":\"" + escape(player) + "\",\"game\":\"" + escape(game) +
                    "\",\"score\":" + score + ",\"timestamp\":" + timestamp + "}";
        }

        private static String escape(String s) {
            return s == null ? "" : s.replace("\"", "\\\"");
        }
    }

    public static class ScoreRepository {
        private final List<ScoreEntry> scores = new CopyOnWriteArrayList<>();

        public ScoreRepository() {
            // Seed initial classic retro records
            addScore(new ScoreEntry("Duke", "space-impact", 4200, System.currentTimeMillis() - 86400000L * 2));
            addScore(new ScoreEntry("Neo", "snake-retro", 3100, System.currentTimeMillis() - 86400000L * 3));
            addScore(new ScoreEntry("PixelKing", "brick-breaker", 2850, System.currentTimeMillis() - 86400000L));
            addScore(new ScoreEntry("RetroGamer", "space-impact", 2600, System.currentTimeMillis() - 3600000L));
            addScore(new ScoreEntry("NokiaFan", "snake-retro", 1950, System.currentTimeMillis() - 7200000L));
        }

        public void addScore(ScoreEntry entry) {
            scores.add(entry);
        }

        public List<ScoreEntry> getTopScores(String gameFilter, int limit) {
            List<ScoreEntry> filtered = new ArrayList<>();
            for (ScoreEntry s : scores) {
                if (gameFilter == null || gameFilter.isBlank() || s.getGame().equalsIgnoreCase(gameFilter)) {
                    filtered.add(s);
                }
            }
            filtered.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());
            if (filtered.size() > limit) {
                return filtered.subList(0, limit);
            }
            return filtered;
        }
    }

    static class GamesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = "[" +
                    "{\"id\":\"space-impact\",\"title\":\"Space Impact J2ME\",\"genre\":\"Space Shooter\",\"icon\":\"🚀\"}," +
                    "{\"id\":\"snake-retro\",\"title\":\"Nokia Snake II\",\"genre\":\"Arcade Classic\",\"icon\":\"🐍\"}," +
                    "{\"id\":\"brick-breaker\",\"title\":\"Bounce Brick Breaker\",\"genre\":\"Action Puzzle\",\"icon\":\"🧱\"}" +
                    "]";
            sendJsonResponse(exchange, 200, json);
        }
    }

    static class ScoresHandler implements HttpHandler {
        private final ScoreRepository repo;

        public ScoresHandler(ScoreRepository repo) {
            this.repo = repo;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                String body = buffer.toString(StandardCharsets.UTF_8);

                // Simple JSON parser without external dependencies
                String player = extractJsonField(body, "player", "Player1");
                String game = extractJsonField(body, "game", "space-impact");
                int score = extractJsonInt(body, "score", 0);

                ScoreEntry entry = new ScoreEntry(player, game, score, System.currentTimeMillis());
                repo.addScore(entry);

                sendJsonResponse(exchange, 201, "{\"status\":\"success\",\"entry\":" + entry.toJson() + "}");
                return;
            }

            // GET
            String query = exchange.getRequestURI().getQuery();
            String gameFilter = null;
            if (query != null && query.contains("game=")) {
                for (String param : query.split("&")) {
                    if (param.startsWith("game=")) {
                        gameFilter = param.substring(5);
                    }
                }
            }

            List<ScoreEntry> top = repo.getTopScores(gameFilter, 10);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < top.size(); i++) {
                sb.append(top.get(i).toJson());
                if (i < top.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendJsonResponse(exchange, 200, sb.toString());
        }

        private String extractJsonField(String json, String field, String defVal) {
            String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
            if (m.find()) {
                return m.group(1);
            }
            return defVal;
        }

        private int extractJsonInt(String json, String field, int defVal) {
            String pattern = "\"" + field + "\"\\s*:\\s*(\\d+)";
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
            if (m.find()) {
                try {
                    return Integer.parseInt(m.group(1));
                } catch (NumberFormatException ignored) {}
            }
            return defVal;
        }
    }

    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Runtime runtime = Runtime.getRuntime();
            String json = "{\"status\":\"ONLINE\",\"app\":\"Java Retro Games Hub\",\"version\":\"1.0.0\"," +
                    "\"javaVersion\":\"" + System.getProperty("java.version") + "\"," +
                    "\"totalMemory\":" + runtime.totalMemory() + ",\"freeMemory\":" + runtime.freeMemory() + "}";
            sendJsonResponse(exchange, 200, json);
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }
            File file = new File("." + path);
            if (!file.exists() || file.isDirectory()) {
                file = new File("index.html");
            }

            if (file.exists() && file.isFile()) {
                String contentType = "text/html; charset=UTF-8";
                if (file.getName().endsWith(".css")) {
                    contentType = "text/css; charset=UTF-8";
                } else if (file.getName().endsWith(".js")) {
                    contentType = "application/javascript; charset=UTF-8";
                } else if (file.getName().endsWith(".json")) {
                    contentType = "application/json; charset=UTF-8";
                } else if (file.getName().endsWith(".svg")) {
                    contentType = "image/svg+xml";
                }
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, file.length());
                try (OutputStream os = exchange.getResponseBody(); FileInputStream fis = new FileInputStream(file)) {
                    fis.transferTo(os);
                }
            } else {
                String notFound = "404 Not Found";
                exchange.sendResponseHeaders(404, notFound.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
