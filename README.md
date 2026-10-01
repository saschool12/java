# Java Games Retro Arcade 🕹️

Play classic J2ME-style retro mobile games in your browser, powered by Java 17 and ready for 1-click deployment on **Vercel**, **Railway**, and **Render**.

![Java Retro Games](https://img.shields.io/badge/Java-17-orange?logo=java)
![Vercel Ready](https://img.shields.io/badge/Vercel-Edge%20Ready-black?logo=vercel)
![Gradle](https://img.shields.io/badge/Gradle-8.14.3-blue?logo=gradle)

---

## 🎮 Playable Games Included

1. **🚀 Space Impact (J2ME Classic)**: Pilot your spaceship, dodge alien lasers, take down heavy enemy cruisers, and fight through infinite waves.
2. **🐍 Nokia Snake II**: The legendary green LCD monochrome phone classic. Eat apples, collect bonus creatures, and avoid running into your tail or walls.
3. **🧱 Bounce / Brick Breaker**: Paddle arcade action with realistic ball reflections, destruction combos, and level progression.

---

## ✨ Features

- **Retro Mobile Chassis**: Authentic retro phone layout with D-pad, action buttons (A/B), and keyboard controls (Arrow keys / WASD + Space).
- **8-Bit Sound Synthesizer**: Web Audio API retro chimes, laser chirps, explosion rumbles, and audio mute toggle (zero audio assets required).
- **Embedded Java 17 Backend**: Built-in HTTP server using Java's standard `jdk.httpserver` with zero heavy dependencies.
- **REST Leaderboard API**: Real-time high-score submission and tracking (`GET /api/scores`, `POST /api/scores`, `GET /api/games`).
- **Instant Vercel Deployment**: Configured with [`vercel.json`](file:///workspace/java/vercel.json) to deploy immediately to Vercel Edge.

---

## 🚀 Quick Start

### Run Locally with Java
```bash
# 1. Run tests
./gradlew test

# 2. Start the local Java arcade server
./gradlew run
```
Open **`http://localhost:8080`** in your browser.

---

## 🌐 Deploy to Vercel

1. Go to [vercel.com/new](https://vercel.com/new).
2. Select repository **`saschool12/java`**.
3. Click **Deploy**. Vercel will immediately deploy the retro game arcade to your personal `.vercel.app` URL!

---

## 🕹️ Controls

| Action | Keyboard | Touch / On-Screen |
| :--- | :--- | :--- |
| **Move Up / Down / Left / Right** | Arrow Keys or `W, A, S, D` | D-Pad Buttons |
| **Fire / Action (A)** | `Space` or Enter | Pink `[A]` Button |
| **Secondary Action (B)** | `X` | Cyan `[B]` Button |
| **Pause / Resume** | `P` | `[PAUSE]` Button |
| **Toggle Audio** | Click Audio Button | `[AUDIO]` Button |
