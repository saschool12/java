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
    private final GameCatalog gameCatalog = new GameCatalog();

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
        return "Java 100 Retro Games Vault is ready!";
    }

    public ScoreRepository getScoreRepository() {
        return scoreRepository;
    }

    public GameCatalog getGameCatalog() {
        return gameCatalog;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/games", new GamesHandler(gameCatalog));
        server.createContext("/api/scores", new ScoresHandler(scoreRepository));
        server.createContext("/api/status", new StatusHandler(gameCatalog));
        server.setExecutor(null);
        server.start();
        System.out.println("100 Java Games Arcade Server running at http://localhost:" + port);
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

    public static class GameInfo {
        private final int id;
        private final String slug;
        private final String title;
        private final String category;
        private final String developer;
        private final int year;
        private final String icon;
        private final boolean playable;
        private final String description;

        public GameInfo(int id, String slug, String title, String category, String developer, int year, String icon, boolean playable, String description) {
            this.id = id;
            this.slug = slug;
            this.title = title;
            this.category = category;
            this.developer = developer;
            this.year = year;
            this.icon = icon;
            this.playable = playable;
            this.description = description;
        }

        public int getId() { return id; }
        public String getSlug() { return slug; }
        public String getTitle() { return title; }
        public String getCategory() { return category; }
        public String getDeveloper() { return developer; }
        public int getYear() { return year; }
        public String getIcon() { return icon; }
        public boolean isPlayable() { return playable; }
        public String getDescription() { return description; }

        public String toJson() {
            return "{\"id\":" + id +
                    ",\"slug\":\"" + escape(slug) + "\"" +
                    ",\"title\":\"" + escape(title) + "\"" +
                    ",\"category\":\"" + escape(category) + "\"" +
                    ",\"developer\":\"" + escape(developer) + "\"" +
                    ",\"year\":" + year +
                    ",\"icon\":\"" + escape(icon) + "\"" +
                    ",\"playable\":" + playable +
                    ",\"description\":\"" + escape(description) + "\"}";
        }

        private static String escape(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }

    public static class GameCatalog {
        private final List<GameInfo> games = new ArrayList<>();

        public GameCatalog() {
            populateGames();
        }

        public List<GameInfo> getAllGames() {
            return Collections.unmodifiableList(games);
        }

        public int getTotalGamesCount() {
            return games.size();
        }

        public List<GameInfo> filter(String category, String search) {
            List<GameInfo> result = new ArrayList<>();
            String catFilter = category == null ? "" : category.trim().toLowerCase();
            String sFilter = search == null ? "" : search.trim().toLowerCase();

            for (GameInfo g : games) {
                boolean matchCat = catFilter.isEmpty() || catFilter.equals("all") || g.getCategory().toLowerCase().contains(catFilter);
                boolean matchSearch = sFilter.isEmpty() ||
                        g.getTitle().toLowerCase().contains(sFilter) ||
                        g.getDeveloper().toLowerCase().contains(sFilter) ||
                        g.getCategory().toLowerCase().contains(sFilter);
                if (matchCat && matchSearch) {
                    result.add(g);
                }
            }
            return result;
        }

        private void add(int id, String slug, String title, String category, String developer, int year, String icon, boolean playable, String description) {
            games.add(new GameInfo(id, slug, title, category, developer, year, icon, playable, description));
        }

        private void populateGames() {
            // Action & Adventure (1-20)
            add(1, "space-impact", "Space Impact", "Action", "Nokia", 2000, "🚀", true, "The legendary side-scrolling space shooter pre-installed on Nokia 3310.");
            add(2, "doom-rpg", "Doom RPG", "Action", "id Software", 2005, "👹", false, "Turn-based tactical RPG shooter in the classic DOOM universe.");
            add(3, "gangstar-crime-city", "Gangstar: Crime City", "Action", "Gameloft", 2006, "🏙️", false, "Open world crime adventure set in the streets of Los Angeles.");
            add(4, "assassins-creed", "Assassin's Creed Mobile", "Action", "Gameloft", 2007, "🗡️", false, "Altaïr's stealth parkour quest across the Holy Land.");
            add(5, "prince-of-persia", "Prince of Persia: The Two Thrones", "Action", "Gameloft", 2005, "⏳", false, "Acrobatic sword-fighting and time manipulation platformer.");
            add(6, "metal-slug-4", "Metal Slug 4 Mobile", "Action", "SNK Playmore", 2005, "💥", false, "Fast-paced run-and-gun arcade warfare with heavy weaponry.");
            add(7, "wolfenstein-rpg", "Wolfenstein RPG", "Action", "EA Mobile", 2008, "🏰", false, "Comic turn-based dungeon crawler battle against Castle Wolfenstein.");
            add(8, "castlevania-shadows", "Castlevania: Order of Shadows", "Action", "Konami", 2007, "🦇", false, "Classic vampire hunting whip action platformer.");
            add(9, "zombie-infection", "Zombie Infection", "Action", "Gameloft", 2008, "🧟", false, "Survival horror combat against undead hordes in South America.");
            add(10, "splinter-cell-double", "Splinter Cell: Double Agent", "Action", "Gameloft", 2006, "🕶️", false, "Sam Fisher's intense covert ops and stealth infiltration.");
            add(11, "splinter-cell-pandora", "Splinter Cell: Pandora Tomorrow", "Action", "Gameloft", 2004, "🎯", false, "Night-vision tactical stealth espionage missions.");
            add(12, "brothers-in-arms", "Brothers in Arms: Earned in Blood", "Action", "Gameloft", 2005, "🎖️", false, "WWII squad-based tactical combat in Normandy.");
            add(13, "modern-combat-2", "Modern Combat 2: Black Pegasus", "Action", "Gameloft", 2010, "🔫", false, "Top-tier modern military mobile action campaigns.");
            add(14, "nova-vanguard", "N.O.V.A. Near Orbit Vanguard Alliance", "Action", "Gameloft", 2009, "🛸", false, "Sci-fi futuristic combat against alien invaders.");
            add(15, "call-of-duty-4", "Call of Duty 4: Modern Warfare", "Action", "Glu Mobile", 2007, "💣", false, "Intense modern military shooter across warzones.");
            add(16, "call-of-duty-black-ops", "Call of Duty: Black Ops Mobile", "Action", "Glu Mobile", 2010, "🎖️", false, "Cold War covert operations and tactical combat.");
            add(17, "spiderman-toxic", "Spider-Man: Toxic City", "Action", "Gameloft", 2009, "🕷️", false, "Web-slinging superhero combat through New York City.");
            add(18, "the-dark-knight", "The Dark Knight Mobile", "Action", "Glu Mobile", 2008, "🦇", false, "Gotham City vigilante action against the Joker.");
            add(19, "transformers-rotf", "Transformers: Revenge of the Fallen", "Action", "Glu Mobile", 2009, "🤖", false, "Autobots vs Decepticons transforming battle.");
            add(20, "alien-quarantine", "Alien Quarantine", "Action", "Gameloft", 2013, "👽", false, "Atmospheric sci-fi survival horror on an infected starship.");

            // Arcade & Classics (21-40)
            add(21, "snake-retro", "Nokia Snake II", "Arcade", "Nokia", 2000, "🐍", true, "The timeless Nokia mobile phone snake game with labyrinth walls.");
            add(22, "brick-breaker", "Bounce Brick Breaker", "Arcade", "Nokia / J2ME", 2001, "🧱", true, "Paddle arcade physics with bouncing balls and combo bricks.");
            add(23, "bounce", "Bounce Classic", "Arcade", "Nokia", 2001, "🔴", true, "Roll and jump the red ball through rings and dangerous spikes.");
            add(24, "retro-asteroids", "Galaxy Asteroids", "Arcade", "Classic Java", 2002, "☄️", true, "360-degree space shooter dodging and blasting space asteroids.");
            add(25, "pixel-racer", "Pixel Highway Racer", "Arcade", "Retro J2ME", 2003, "🏎️", true, "High-speed highway obstacle dodging with retro nitro boosts.");
            add(26, "flappy-java", "Flappy Java", "Arcade", "Retro Java", 2014, "🐤", true, "Flap through retro pipes with precise tap timing.");
            add(27, "tetris-mobile", "Tetris Mobile", "Arcade", "EA Mobile", 2006, "🟦", false, "The world's favorite falling block puzzle on mobile.");
            add(28, "pac-man-mobile", "Pac-Man Mobile", "Arcade", "Namco", 2002, "🟡", false, "Chomp dots and dodge ghosts in iconic retro mazes.");
            add(29, "space-invaders", "Space Invaders", "Arcade", "Taito", 2003, "👾", false, "Classic alien waves descending upon defensive shields.");
            add(30, "sonic-the-hedgehog", "Sonic The Hedgehog Mobile", "Arcade", "Sega Mobile", 2006, "🦔", false, "Spin dash and loop-de-loop through Green Hill Zone.");
            add(31, "crazy-taxi-2d", "Crazy Taxi 2D", "Arcade", "Sega Mobile", 2003, "🚕", false, "Crazy driving through heavy city traffic to deliver fares.");
            add(32, "block-breaker-deluxe", "Block Breaker Deluxe", "Arcade", "Gameloft", 2004, "💎", false, "Neon nightlife themed brick busting with insane powerups.");
            add(33, "rayman-3", "Rayman 3 Mobile", "Arcade", "Gameloft", 2003, "🎪", false, "Helicopter hair platforming through lush fantasy realms.");
            add(34, "rayman-raving-rabbids", "Rayman Raving Rabbids", "Arcade", "Gameloft", 2006, "🐰", false, "Wacky minigame madness against the crazy Rabbids.");
            add(35, "super-mario-j2me", "Super Mario Land J2ME", "Arcade", "Homebrew", 2004, "🍄", false, "Nostalgic portable plumber jumping adventures.");
            add(36, "mega-man-mobile", "Mega Man Mobile", "Arcade", "Capcom", 2005, "🤖", false, "Blue bomber blaster platforming against Robot Masters.");
            add(37, "street-fighter-2", "Street Fighter II", "Arcade", "Capcom", 2006, "🥋", false, "Hadouken! World warrior martial arts tournament.");
            add(38, "boulder-dash", "Boulder Dash Mobile", "Arcade", "First Star", 2004, "💎", false, "Dig caverns, collect gems, and avoid falling rocks.");
            add(39, "bomberman-mobile", "Bomberman Mobile", "Arcade", "Living Mobile", 2004, "💣", false, "Blast maze barriers and rival bombers with timed explosives.");
            add(40, "moorhuhn-mobile", "Moorhuhn Mobile", "Arcade", "Phenomedia", 2003, "🐔", false, "Fast reflex chicken shooting carnival arcade madness.");

            // Racing & Speed (41-55)
            add(41, "asphalt-3", "Asphalt 3: Street Rules", "Racing", "Gameloft", 2006, "🏎️", false, "Underground supercars, police pursuits, and nitro drifts.");
            add(42, "asphalt-4", "Asphalt 4: Elite Racing", "Racing", "Gameloft", 2008, "🏁", false, "Exotic street races from Paris to Dubai in licensed supercars.");
            add(43, "nfs-most-wanted", "Need for Speed: Most Wanted", "Racing", "EA Mobile", 2005, "🚓", false, "Blacklist street racing and intense police takedowns.");
            add(44, "nfs-underground-2", "Need for Speed Underground 2", "Racing", "EA Mobile", 2004, "🚘", false, "Custom neon tuners, dyno tuning, and drag racing.");
            add(45, "rally-pro-contest", "Rally Pro Contest 3D", "Racing", "Fishlabs", 2005, "🚙", false, "Revolutionary 3D mobile rally physics on dirt and snow.");
            add(46, "ferrari-gt-evolution", "Ferrari GT: Evolution", "Racing", "Gameloft", 2008, "🏎️", false, "Official Ferrari test tracks and classic Prancing Horses.");
            add(47, "moto-gp-08", "Moto GP 08", "Racing", "I-play", 2008, "🏍️", false, "High-octane motorcycle championship racing.");
            add(48, "ducati-extreme", "Ducati Extreme", "Racing", "Hands-On", 2006, "🏍️", false, "Knee-down superbike circuits across the globe.");
            add(49, "3d-moto-racing", "3D Moto Racing", "Racing", "I-play", 2005, "🏁", false, "Pure 3D superbike speedway showdown.");
            add(50, "driver-vegas", "Driver: Vegas", "Racing", "Gameloft", 2006, "🎰", false, "Undercover wheelman vehicular getaway through Sin City.");
            add(51, "fast-and-furious", "Fast & Furious: Fugitive", "Racing", "I-play", 2007, "💨", false, "Quarter-mile nitro drag races and cross-country chases.");
            add(52, "townsmen-racing", "Townsmen Racing", "Racing", "HandyGames", 2006, "🚜", false, "Medieval cart racing with humorous sheep obstacles.");
            add(53, "burnout-mobile", "Burnout Mobile", "Racing", "EA Mobile", 2007, "💥", false, "Aggressive crashes, takedowns, and speed burnout boosts.");
            add(54, "crash-nitro-kart", "Crash Nitro Kart", "Racing", "Vivendi", 2004, "🦊", false, "Wumpa fruit kart combat with Crash Bandicoot.");
            add(55, "midnight-club-3", "Midnight Club 3: DUB Edition", "Racing", "Rockstar Games", 2005, "🌃", false, "Cruising tuned muscle cars and luxury SUVs at night.");

            // Puzzle & Brain (56-75)
            add(56, "diamond-rush", "Diamond Rush", "Puzzle", "Gameloft", 2006, "💎", false, "Explorer puzzle adventure through Angkor Wat and Bavaria.");
            add(57, "tower-bloxx", "Tower Bloxx", "Puzzle", "Digital Chocolate", 2005, "🏗️", false, "Drop swinging skyscraper floors to build dizzying towers.");
            add(58, "bubble-bash", "Bubble Bash", "Puzzle", "Gameloft", 2006, "🫧", false, "Color-matching bubble cannon fun on tropical African shores.");
            add(59, "bejeweled", "Bejeweled", "Puzzle", "PopCap Games", 2004, "✨", false, "The definitive gem-swapping match-3 cascade puzzle.");
            add(60, "zuma-mobile", "Zuma Mobile", "Puzzle", "Glu Mobile", 2005, "🐸", false, "Stone frog ball shooter clearing rolling ancient spirals.");
            add(61, "bobby-carrot", "Bobby Carrot", "Puzzle", "FDG Entertainment", 2004, "🥕", false, "Navigate clever farm mazes and traps to harvest carrots.");
            add(62, "bobby-carrot-5", "Bobby Carrot 5: Level Up!", "Puzzle", "FDG Entertainment", 2008, "🐇", false, "The biggest rabbit puzzle adventure with dragon realms.");
            add(63, "plants-vs-zombies", "Plants vs. Zombies J2ME", "Puzzle", "PopCap Games", 2010, "🌻", false, "Defend your backyard lawn using peashooters and cherry bombs.");
            add(64, "chuzzle-mobile", "Chuzzle Mobile", "Puzzle", "PopCap Games", 2006, "🧶", false, "Cute googly-eyed fuzzy creature matching puzzle.");
            add(65, "peggle-mobile", "Peggle Mobile", "Puzzle", "PopCap Games", 2007, "🟠", false, "Extreme fever peg bouncing pachinko arcade delight.");
            add(66, "luxor-2-mobile", "Luxor 2 Mobile", "Puzzle", "MumboJumbo", 2007, "🪲", false, "Egyptian winged scarab marble shooting quest.");
            add(67, "diner-dash", "Diner Dash", "Puzzle", "Glu Mobile", 2006, "🍽️", false, "Flo's high-speed restaurant management and customer seating.");
            add(68, "cafe-sudoku", "Café Sudoku", "Puzzle", "Digital Chocolate", 2006, "🔢", false, "Relaxing logic grid numbers in an inviting coffeehouse.");
            add(69, "tornado-mania", "Tornado Mania!", "Puzzle", "Digital Chocolate", 2006, "🌪️", false, "Control a whimsical vortex to collect buildings and build utopia.");
            add(70, "rollercoaster-rush", "Rollercoaster Rush", "Puzzle", "Digital Chocolate", 2006, "🎢", false, "Control coaster brake and throttle for maximum passenger thrill.");
            add(71, "sokoban-3d", "Sokoban 3D", "Puzzle", "Living Mobile", 2005, "📦", false, "The classic Japanese warehouse crate pushing logic puzzle.");
            add(72, "lemmings-mobile", "Lemmings Mobile", "Puzzle", "Glu Mobile", 2006, "⛏️", false, "Assign parachute, dig, and build roles to save green-haired lemmings.");
            add(73, "diamond-twister", "Diamond Twister", "Puzzle", "Gameloft", 2008, "💍", false, "High-stakes diamond heist cascading gem puzzles.");
            add(74, "uno-3d", "UNO 3D Mobile", "Puzzle", "Gameloft", 2008, "🃏", false, "The classic color and number card game with wild draw 4s.");
            add(75, "texas-holdem-poker", "Texas Hold'em Poker", "Puzzle", "Gameloft", 2006, "♠️", false, "All-in tournament poker bluffs in world casinos.");

            // Sports & Athletics (76-88)
            add(76, "real-football-2008", "Real Football 2008", "Sports", "Gameloft", 2007, "⚽", false, "Championship European soccer matches with pro formations.");
            add(77, "fifa-07-mobile", "FIFA 07 Mobile", "Sports", "EA Mobile", 2006, "🥅", false, "Authentic licensed national teams and league tournaments.");
            add(78, "pes-2009", "Pro Evolution Soccer 2009", "Sports", "Konami", 2008, "🏟️", false, "Smooth tactical passing and scoring mechanics.");
            add(79, "playman-summer", "Playman World Athletics", "Sports", "RealNetworks", 2005, "🏃", false, "100m sprint, long jump, and javelin gold medal decathlon.");
            add(80, "playman-winter", "Playman Winter Games", "Sports", "RealNetworks", 2006, "🎿", false, "Slalom skiing, bobsled, and speed skating snow games.");
            add(81, "midnight-pool-3d", "Midnight Pool 3D", "Sports", "Gameloft", 2005, "🎱", false, "8-ball and 9-ball barroom trick shots and wagers.");
            add(82, "midnight-bowling-3d", "Midnight Bowling 3D", "Sports", "Gameloft", 2006, "🎳", false, "Strikes, spares, and spin curves in retro neon lanes.");
            add(83, "nba-live-08", "NBA Live 08", "Sports", "EA Mobile", 2007, "🏀", false, "Hardwood slam dunks, 3-pointers, and full-court presses.");
            add(84, "tiger-woods-07", "Tiger Woods PGA Tour 07", "Sports", "EA Mobile", 2006, "⛳", false, "Precision golf fairway drives and green putts.");
            add(85, "tony-hawk-4", "Tony Hawk's Pro Skater 4", "Sports", "Activision", 2003, "🛹", false, "Kickflips, grinds, and halfpipe combos on 4 wheels.");
            add(86, "skate-mobile", "Skate Mobile", "Sports", "EA Mobile", 2007, "🛹", false, "Realistic street skateboarding flip trick mastery.");
            add(87, "super-dynamite-fishing", "Super Dynamite Fishing", "Sports", "HandyGames", 2008, "🎣", false, "Crazy TNT blast fishing for monster catches.");
            add(88, "guitar-hero-3", "Guitar Hero III Mobile", "Sports", "Hands-On", 2007, "🎸", false, "Rock out rhythm solos to classic rock anthems.");

            // RPG & Strategy (89-100)
            add(89, "galaxy-on-fire-2", "Galaxy On Fire 2", "RPG", "Fishlabs", 2009, "🚀", false, "Epic open space trading, mining, and dogfighting universe.");
            add(90, "deep-3d", "Deep 3D", "RPG", "Fishlabs", 2006, "🌊", false, "Atmospheric underwater submarine exploration and combat.");
            add(91, "ancient-empires-2", "Ancient Empires II", "RPG", "Macrospace", 2005, "⚔️", false, "Masterpiece turn-based tactical fantasy kingdom battles.");
            add(92, "heroes-might-magic", "Heroes of Might & Magic", "RPG", "Gameloft", 2007, "🏰", false, "Turn-based kingdom warfare, artifacts, and mystical beasts.");
            add(93, "age-of-empires-3", "Age of Empires III Mobile", "RPG", "Glu Mobile", 2007, "🏹", false, "Colonial conquest, resource gathering, and siege engines.");
            add(94, "townsmen-6", "Townsmen 6", "RPG", "HandyGames", 2009, "👑", false, "French Revolution era town management and resource logistics.");
            add(95, "gothic-3-beginning", "Gothic 3: The Beginning", "RPG", "HandyGames", 2008, "🗡️", false, "Dark fantasy open world quests, spells, and orc combat.");
            add(96, "devils-and-demons", "Devils and Demons", "RPG", "HandyGames", 2009, "🔥", false, "Turn-based party combat against hellish demon lords.");
            add(97, "worms-forts", "Worms Forts: Under Siege", "RPG", "THQ Wireless", 2005, "🪱", false, "Fortress building and ballistic artillery worm warfare.");
            add(98, "worms-2007", "Worms 2007", "RPG", "THQ Wireless", 2007, "💣", false, "Holy Hand Grenades and exploding sheep worm comedy.");
            add(99, "simcity-mobile", "SimCity Mobile", "RPG", "EA Mobile", 2007, "🏙️", false, "Zone residential, commercial, and manage city disasters.");
            add(100, "the-sims-3", "The Sims 3 Mobile", "RPG", "EA Mobile", 2009, "🏡", false, "Build homes, socialize, fulfill aspirations, and live life.");
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

        public String getPlayer() { return player; }
        public String getGame() { return game; }
        public int getScore() { return score; }
        public long getTimestamp() { return timestamp; }

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
            addScore(new ScoreEntry("Duke", "space-impact", 4200, System.currentTimeMillis() - 86400000L * 2));
            addScore(new ScoreEntry("Neo", "snake-retro", 3100, System.currentTimeMillis() - 86400000L * 3));
            addScore(new ScoreEntry("PixelKing", "brick-breaker", 2850, System.currentTimeMillis() - 86400000L));
            addScore(new ScoreEntry("RetroGamer", "space-impact", 2600, System.currentTimeMillis() - 3600000L));
            addScore(new ScoreEntry("NokiaFan", "snake-retro", 1950, System.currentTimeMillis() - 7200000L));
            addScore(new ScoreEntry("RedBall", "bounce", 3400, System.currentTimeMillis() - 14400000L));
            addScore(new ScoreEntry("Astro", "retro-asteroids", 2200, System.currentTimeMillis() - 5400000L));
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
        private final GameCatalog catalog;

        public GamesHandler(GameCatalog catalog) {
            this.catalog = catalog;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            String query = exchange.getRequestURI().getQuery();
            String category = null;
            String search = null;

            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("category=")) {
                        category = param.substring(9);
                    } else if (param.startsWith("search=")) {
                        search = param.substring(7);
                    }
                }
            }

            List<GameInfo> list = catalog.filter(category, search);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i).toJson());
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendJsonResponse(exchange, 200, sb.toString());
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
        private final GameCatalog catalog;

        public StatusHandler(GameCatalog catalog) {
            this.catalog = catalog;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Runtime runtime = Runtime.getRuntime();
            String json = "{\"status\":\"ONLINE\",\"app\":\"100 Java Games Retro Vault\",\"totalGames\":" +
                    catalog.getTotalGamesCount() + ",\"javaVersion\":\"" + System.getProperty("java.version") +
                    "\",\"totalMemory\":" + runtime.totalMemory() + ",\"freeMemory\":" + runtime.freeMemory() + "}";
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
