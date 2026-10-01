# Java Q&A Quiz & Random Question Generator ☕

An interactive, full-stack Java 17 learning and interview preparation platform featuring a **random question generator**, instant answer verification, streak tracking, detailed explanations, and code highlighting. Ready for 1-click deployment on **Vercel**, **Railway**, and **Render**.

![Java 17](https://img.shields.io/badge/Java-17-orange?logo=java)
![Vercel Ready](https://img.shields.io/badge/Vercel-Edge%20Ready-black?logo=vercel)
![Gradle](https://img.shields.io/badge/Gradle-8.14.3-blue?logo=gradle)

---

## 🎯 Features

- **🎲 Random Question Generator**: Instantly generate randomized Java questions filtered by topic and difficulty.
- **📚 Comprehensive Java Question Bank**: Curated real-world interview and OCPJP certification questions covering:
  - **Basics & Syntax**: Pass-by-value semantics, String immutability, operator precedence, Integer cache.
  - **OOP (Object-Oriented Programming)**: Polymorphism, method hiding vs overriding, `equals()` and `hashCode()` contract.
  - **Collections Framework**: `HashMap` treeification in Java 8+, `ConcurrentHashMap` null policies, `ArrayList` internals.
  - **Concurrency & Multithreading**: `volatile` memory visibility, `Callable` vs `Runnable`, intrinsic monitor synchronization.
  - **JVM & Memory**: GC roots, heap memory reachability, class loading.
  - **Modern Java**: Streams API, Java 16 Records, Java 17 Sealed classes, `try-with-resources`.
- **⚡ Instant Feedback & Explanations**: Immediate visual indicators (green checkmark / red error) and in-depth conceptual explanations.
- **🔥 Gamification**: Live score tracking, combo streak counters, and Web Audio API synthesized sound effects.
- **🌐 Dual Deployment**:
  - **Local Java Server**: Native embedded Java 17 HTTP server via `./gradlew run` on port 8080.
  - **Vercel Edge**: 100% serverless edge compatibility with zero configuration using [`vercel.json`](file:///workspace/java/vercel.json).

---

## 🔌 Java REST API

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/api/questions/random` | `GET` | Returns a random question (supports `?topic=...&difficulty=...`). |
| `/api/questions` | `GET` | Returns all questions matching optional filters. |
| `/api/questions/check` | `POST` | Validates answer: `{"questionId": 1, "selectedIndex": 0}`. |
| `/api/stats` | `GET` | Returns question bank count, JVM metrics, and server status. |

---

## 🚀 Quick Start

### Run Locally with Java
```bash
# 1. Run unit tests
./gradlew test

# 2. Start the local Java Q&A web server
./gradlew run
```
Open **`http://localhost:8080`** in your browser.

---

## 🌐 Deploy to Vercel

1. Open [**vercel.com/new**](https://vercel.com/new).
2. Select your repository [**`saschool12/java`**](https://github.com/saschool12/java).
3. Click **Deploy**. Vercel will host the interactive Java Q&A platform live at your personal `.vercel.app` domain!
