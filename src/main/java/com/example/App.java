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
import java.util.List;
import java.util.Random;

public class App {
    private final int port;
    private HttpServer server;
    private final QuestionBank questionBank = new QuestionBank();

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
        return "Java Q&A Quiz & Random Question Generator is running!";
    }

    public QuestionBank getQuestionBank() {
        return questionBank;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/questions", new QuestionsHandler(questionBank));
        server.createContext("/api/questions/random", new RandomQuestionHandler(questionBank));
        server.createContext("/api/questions/check", new CheckAnswerHandler(questionBank));
        server.createContext("/api/stats", new StatsHandler(questionBank));
        server.setExecutor(null);
        server.start();
        System.out.println("Java Q&A Application running at http://localhost:" + port);
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

    public static class Question {
        private final int id;
        private final String question;
        private final String codeSnippet;
        private final List<String> options;
        private final int correctIndex;
        private final String explanation;
        private final String topic;
        private final String difficulty;

        public Question(int id, String question, String codeSnippet, List<String> options, int correctIndex, String explanation, String topic, String difficulty) {
            this.id = id;
            this.question = question;
            this.codeSnippet = codeSnippet;
            this.options = options;
            this.correctIndex = correctIndex;
            this.explanation = explanation;
            this.topic = topic;
            this.difficulty = difficulty;
        }

        public int getId() { return id; }
        public String getQuestion() { return question; }
        public String getCodeSnippet() { return codeSnippet; }
        public List<String> getOptions() { return options; }
        public int getCorrectIndex() { return correctIndex; }
        public String getExplanation() { return explanation; }
        public String getTopic() { return topic; }
        public String getDifficulty() { return difficulty; }

        public String toJson(boolean includeAnswer) {
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"id\":").append(id).append(",");
            sb.append("\"question\":\"").append(escape(question)).append("\",");
            sb.append("\"codeSnippet\":\"").append(escape(codeSnippet)).append("\",");
            sb.append("\"topic\":\"").append(escape(topic)).append("\",");
            sb.append("\"difficulty\":\"").append(escape(difficulty)).append("\",");
            sb.append("\"options\":[");
            for (int i = 0; i < options.size(); i++) {
                sb.append("\"").append(escape(options.get(i))).append("\"");
                if (i < options.size() - 1) sb.append(",");
            }
            sb.append("]");
            if (includeAnswer) {
                sb.append(",\"correctIndex\":").append(correctIndex);
                sb.append(",\"explanation\":\"").append(escape(explanation)).append("\"");
            }
            sb.append("}");
            return sb.toString();
        }

        private static String escape(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
        }
    }

    public static class QuestionBank {
        private final List<Question> questions = new ArrayList<>();
        private final Random random = new Random();

        public QuestionBank() {
            initQuestions();
        }

        public List<Question> getAllQuestions() {
            return Collections.unmodifiableList(questions);
        }

        public int getTotalCount() {
            return questions.size();
        }

        public Question getQuestionById(int id) {
            for (Question q : questions) {
                if (q.getId() == id) return q;
            }
            return null;
        }

        public Question getRandomQuestion(String topic, String difficulty) {
            List<Question> filtered = filterQuestions(topic, difficulty);
            if (filtered.isEmpty()) {
                filtered = questions;
            }
            return filtered.get(random.nextInt(filtered.size()));
        }

        public List<Question> filterQuestions(String topic, String difficulty) {
            List<Question> res = new ArrayList<>();
            String t = (topic == null || topic.isBlank()) ? null : topic.trim().toLowerCase();
            String d = (difficulty == null || difficulty.isBlank()) ? null : difficulty.trim().toLowerCase();

            for (Question q : questions) {
                boolean matchTopic = (t == null || t.equals("all") || q.getTopic().toLowerCase().contains(t));
                boolean matchDiff = (d == null || d.equals("all") || q.getDifficulty().toLowerCase().equals(d));
                if (matchTopic && matchDiff) {
                    res.add(q);
                }
            }
            return res;
        }

        private void add(int id, String q, String code, List<String> opts, int correctIdx, String exp, String topic, String diff) {
            questions.add(new Question(id, q, code, opts, correctIdx, exp, topic, diff));
        }

        private void initQuestions() {
            add(1, "Is Java pass-by-value or pass-by-reference?",
                    null,
                    List.of("Strictly pass-by-value", "Strictly pass-by-reference", "Pass-by-reference for Objects and pass-by-value for primitives", "Depends on JVM implementation"),
                    0,
                    "Java is strictly pass-by-value at all times. When an object is passed, a copy of the reference (pointer address) is passed by value.",
                    "Basics", "Medium");

            add(2, "What is the result of evaluating the following expression?",
                    "System.out.println(10 + 20 + \"Java\" + 10 + 20);",
                    List.of("1020Java1020", "30Java1020", "30Java30", "Compilation Error"),
                    1,
                    "Operator + evaluates left-to-right. 10 + 20 produces integer 30, then 30 + \"Java\" converts to String \"30Java\", and subsequent additions concatenate as strings.",
                    "Basics", "Easy");

            add(3, "How does HashMap in Java 8+ handle hash collisions when a bucket exceeds 8 nodes?",
                    null,
                    List.of("Throws a HashCollisionException", "Rehashes into an AVL Tree", "Transforms the bucket linked list into a balanced Red-Black Tree", "Doubles the bucket capacity immediately"),
                    2,
                    "In Java 8+, when a single bucket linked list reaches TREEIFY_THRESHOLD (8) and the table capacity is at least 64, it converts to a Red-Black Tree (TreeNode) improving lookup from O(n) to O(log n).",
                    "Collections", "Hard");

            add(4, "Which of the following Map implementations does NOT permit null keys or null values?",
                    null,
                    List.of("HashMap", "LinkedHashMap", "ConcurrentHashMap", "WeakHashMap"),
                    2,
                    "ConcurrentHashMap does not allow null keys or null values to prevent ambiguity in multithreaded environments where get(key) returning null could mean either absent or mapped to null.",
                    "Collections", "Medium");

            add(5, "What is the primary guarantee provided by the 'volatile' keyword on a variable in Java?",
                    null,
                    List.of("Prevents deadlocks completely", "Guarantees thread visibility across CPU caches and prevents instruction reordering", "Makes compound operations like count++ atomic", "Locks the object monitor"),
                    1,
                    "volatile establishes a happens-before relationship, guaranteeing changes made by one thread are immediately visible to all other threads and preventing compiler/CPU instruction reordering. It does NOT make non-atomic operations like ++ atomic.",
                    "Concurrency", "Hard");

            add(6, "Can you override a private or static method in a subclass in Java?",
                    null,
                    List.of("Yes, both can be overridden", "Only static methods can be overridden", "Neither can be overridden; static methods are hidden, not overridden", "Only private methods can be overridden"),
                    2,
                    "Private methods are not visible to subclasses and cannot be overridden. Static methods belong to the class, not instances, so redefining them in a subclass is method hiding, not overriding.",
                    "OOP", "Medium");

            add(7, "What is the difference between '==' and '.equals()' when comparing two String objects?",
                    "String s1 = new String(\"hello\");\nString s2 = new String(\"hello\");",
                    List.of("They are identical", "'==' checks reference memory identity, while '.equals()' checks string content value", "'.equals()' checks reference address only", "'==' checks character values"),
                    1,
                    "'==' checks if both references point to the exact same memory address on the heap. '.equals()' compares the actual sequence of characters.",
                    "Basics", "Easy");

            add(8, "What will be the output of this code snippet?",
                    "String s = \"Java\";\ns.concat(\" 17\");\nSystem.out.println(s);",
                    List.of("Java 17", "Java", "null", "Compilation Error"),
                    1,
                    "String objects in Java are immutable. s.concat(\" 17\") returns a new String, but the return value is ignored, leaving 's' unchanged as \"Java\".",
                    "Basics", "Easy");

            add(9, "What is the contract between equals() and hashCode()?",
                    null,
                    List.of("If two objects are equal according to equals(), their hashCode() MUST be equal", "If two objects have the same hashCode(), they MUST be equal according to equals()", "They have no relationship", "hashCode() must return a unique integer for every distinct object"),
                    0,
                    "If two objects are equal via equals(), they must produce the same hashCode(). However, unequal objects may share the same hashCode (hash collision).",
                    "OOP", "Medium");

            add(10, "What is the output of the following stream pipeline?",
                    "List<String> list = List.of(\"a\", \"bb\", \"ccc\");\nlong count = list.stream().filter(s -> s.length() > 1).count();\nSystem.out.println(count);",
                    List.of("1", "2", "3", "0"),
                    1,
                    "The filter predicate 's -> s.length() > 1' retains \"bb\" (length 2) and \"ccc\" (length 3), resulting in a count of 2.",
                    "Modern Java", "Easy");

            add(11, "What is the difference between Checked and Unchecked exceptions in Java?",
                    null,
                    List.of("Checked exceptions extend RuntimeException; Unchecked do not", "Checked exceptions must be declared in throws or caught at compile-time; Unchecked (RuntimeExceptions) do not", "Unchecked exceptions are fatal JVM errors like OutOfMemoryError", "There is no difference in Java 17"),
                    1,
                    "Checked exceptions (subclasses of Exception except RuntimeException) are checked at compile time and must be handled or declared. Unchecked exceptions (subclasses of RuntimeException) do not require explicit handling.",
                    "Basics", "Medium");

            add(12, "What was introduced in Java 14/16 as a concise way to create immutable data carriers?",
                    null,
                    List.of("sealed classes", "record classes", "data classes", "structs"),
                    1,
                    "Java Records (introduced in Java 14 preview and finalized in Java 16) provide a compact syntax for declaring transparent, immutable data-carrier classes with automatic constructor, getters, equals, hashCode, and toString.",
                    "Modern Java", "Medium");

            add(13, "What does the 'final' keyword do when applied to a class?",
                    null,
                    List.of("Makes all fields of the class immutable", "Prevents the class from being instantiated", "Prevents the class from being subclassed (inherited)", "Makes all methods in the class static"),
                    2,
                    "A 'final' class cannot be extended by any other class (e.g. java.lang.String, java.lang.Integer are final classes).",
                    "OOP", "Easy");

            add(14, "Which garbage collection root is NOT a valid GC root in HotSpot JVM?",
                    null,
                    List.of("Active Java thread local variables in stack frames", "Static variables in loaded classes", "Unreferenced objects in the young generation", "JNI Global references"),
                    2,
                    "GC roots include thread stack variables, JNI references, active threads, and class static references. Unreferenced objects in heap are unreachable garbage collected targets, not GC roots.",
                    "JVM", "Hard");

            add(15, "What interface must an object implement to be eligible for use in a 'try-with-resources' statement?",
                    null,
                    List.of("java.lang.Cloneable", "java.lang.AutoCloseable", "java.io.Serializable", "java.lang.Runnable"),
                    1,
                    "try-with-resources automatically closes any resource that implements java.lang.AutoCloseable (or java.io.Closeable) at the end of the block.",
                    "Modern Java", "Medium");

            add(16, "What is the time complexity of searching for an element in a standard ArrayList by index?",
                    null,
                    List.of("O(1)", "O(n)", "O(log n)", "O(n log n)"),
                    0,
                    "ArrayList is backed by a contiguous array, allowing random direct memory access via index in O(1) constant time.",
                    "Collections", "Easy");

            add(17, "What happens if two threads attempt to call synchronized methods on the SAME object simultaneously?",
                    null,
                    List.of("Both execute concurrently", "One thread acquires the object monitor lock, and the other thread blocks until released", "JVM throws an IllegalMonitorStateException", "Deadlock occurs automatically"),
                    1,
                    "Every Java object has an intrinsic monitor lock. When a thread executes a synchronized instance method, it acquires that object's lock, blocking other threads until the method completes.",
                    "Concurrency", "Medium");

            add(18, "What will this code print?",
                    "Integer a = 127;\nInteger b = 127;\nSystem.out.println(a == b);",
                    List.of("true", "false", "Compilation Error", "NullPointerException"),
                    0,
                    "Java caches Integer objects within the range -128 to 127 (IntegerCache). Autoboxing values in this range returns the same cached reference, so 'a == b' evaluates to true.",
                    "Basics", "Hard");

            add(19, "Which keyword in Java 17 is used to restrict which other classes can extend or implement an interface/class?",
                    null,
                    List.of("restricted", "sealed", "locked", "protected"),
                    1,
                    "Sealed classes and interfaces (finalized in Java 17) use the 'sealed' modifier and 'permits' clause to explicitly restrict which classes may inherit from them.",
                    "Modern Java", "Medium");

            add(20, "What is the difference between Callable and Runnable in Java concurrency?",
                    null,
                    List.of("Runnable cannot run on Thread", "Callable's call() method can return a result and throw checked exceptions; Runnable's run() cannot", "Callable is only for parallel streams", "They are identical in modern Java"),
                    1,
                    "Callable<V> has V call() throws Exception, which returns a value and can throw checked exceptions. Runnable has void run() which cannot return values or throw checked exceptions.",
                    "Concurrency", "Medium");
        }
    }

    static class QuestionsHandler implements HttpHandler {
        private final QuestionBank bank;

        public QuestionsHandler(QuestionBank bank) {
            this.bank = bank;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            String query = exchange.getRequestURI().getQuery();
            String topic = null;
            String difficulty = null;

            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("topic=")) {
                        topic = param.substring(6);
                    } else if (param.startsWith("difficulty=")) {
                        difficulty = param.substring(11);
                    }
                }
            }

            List<Question> list = bank.filterQuestions(topic, difficulty);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i).toJson(true));
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendJsonResponse(exchange, 200, sb.toString());
        }
    }

    static class RandomQuestionHandler implements HttpHandler {
        private final QuestionBank bank;

        public RandomQuestionHandler(QuestionBank bank) {
            this.bank = bank;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            String query = exchange.getRequestURI().getQuery();
            String topic = null;
            String difficulty = null;

            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("topic=")) {
                        topic = param.substring(6);
                    } else if (param.startsWith("difficulty=")) {
                        difficulty = param.substring(11);
                    }
                }
            }

            Question q = bank.getRandomQuestion(topic, difficulty);
            sendJsonResponse(exchange, 200, q.toJson(true));
        }
    }

    static class CheckAnswerHandler implements HttpHandler {
        private final QuestionBank bank;

        public CheckAnswerHandler(QuestionBank bank) {
            this.bank = bank;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            InputStream is = exchange.getRequestBody();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            String body = buffer.toString(StandardCharsets.UTF_8);

            int questionId = extractInt(body, "questionId", 1);
            int selectedIndex = extractInt(body, "selectedIndex", -1);

            Question q = bank.getQuestionById(questionId);
            if (q == null) {
                sendJsonResponse(exchange, 404, "{\"error\":\"Question not found\"}");
                return;
            }

            boolean isCorrect = (selectedIndex == q.getCorrectIndex());
            String json = "{" +
                    "\"correct\":" + isCorrect + "," +
                    "\"correctIndex\":" + q.getCorrectIndex() + "," +
                    "\"explanation\":\"" + Question.escape(q.getExplanation()) + "\"" +
                    "}";
            sendJsonResponse(exchange, 200, json);
        }

        private int extractInt(String json, String key, int def) {
            String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
            return def;
        }
    }

    static class StatsHandler implements HttpHandler {
        private final QuestionBank bank;

        public StatsHandler(QuestionBank bank) {
            this.bank = bank;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Runtime runtime = Runtime.getRuntime();
            String json = "{\"status\":\"ONLINE\",\"app\":\"Java Q&A Quiz & Generator\"," +
                    "\"totalQuestions\":" + bank.getTotalCount() + "," +
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
