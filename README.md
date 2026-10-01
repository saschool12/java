# Java Web Application

A full-stack Java 17 and HTML web application ready for local execution and instant deployment on **Vercel**, **Railway**, or **Render**.

## Features

- **Frontend**: Responsive, modern web interface with interactive API tester and code playground ([`index.html`](file:///workspace/java/index.html)).
- **Backend**: Lightweight embedded HTTP web server built with standard Java 17 `jdk.httpserver` ([`App.java`](file:///workspace/java/src/main/java/com/example/App.java)).
- **Vercel Ready**: Preconfigured with [`vercel.json`](file:///workspace/java/vercel.json) for instant edge deployment of the web UI.
- **REST Endpoints**:
  - `GET /` - Serves the modern website.
  - `GET /api/greeting` - Returns greeting JSON.
  - `GET /api/status` - Returns JVM system health and memory metrics.

## Running Locally

### 1. Build and Test
```bash
./gradlew test
```

### 2. Start the Java Web Server
```bash
./gradlew run
```
Then open `http://localhost:8080` in your browser.

## Deploying to Vercel

1. Go to [vercel.com/new](https://vercel.com/new).
2. Import repository `saschool12/java`.
3. Click **Deploy**. Vercel will host the website automatically.
