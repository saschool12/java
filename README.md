# 100 Java Games Retro Vault 🕹️

A comprehensive **100-in-1** retro mobile gaming arcade celebrating the golden era of **J2ME Java games (2000–2010)**. Built with Java 17, Gradle, and ready for instant deployment on **Vercel**, **Railway**, and **Render**.

![100 Java Games](https://img.shields.io/badge/Catalog-100%20Games-gold?logo=java)
![Java 17](https://img.shields.io/badge/Java-17-orange?logo=java)
![Vercel Ready](https://img.shields.io/badge/Vercel-Edge%20Ready-black?logo=vercel)

---

## 🎮 The 100 Java Games Catalog

Explore and search all 100 legendary J2ME titles across 6 categories:

| Category | Count | Notable Classic Titles |
| :--- | :--- | :--- |
| **Action & Adventure** | 20 | Space Impact, Doom RPG, Gangstar, Assassin's Creed, Prince of Persia, Splinter Cell, Metal Slug 4, N.O.V.A., Call of Duty 4 |
| **Arcade & Classics** | 20 | Nokia Snake II, Bounce Classic, Brick Breaker, Tetris, Pac-Man, Space Invaders, Sonic, Mega Man, Street Fighter II |
| **Racing & Speed** | 15 | Asphalt 3: Street Rules, Asphalt 4, NFS Most Wanted, NFS Underground 2, Rally Pro Contest 3D, Ferrari GT, Moto GP |
| **Puzzle & Brain** | 20 | Diamond Rush, Tower Bloxx, Bubble Bash, Bejeweled, Zuma, Bobby Carrot, Plants vs. Zombies J2ME, Peggle, Café Sudoku |
| **Sports & Athletics** | 13 | Real Football 2008, FIFA 07, PES 2009, Playman World Athletics, Midnight Pool 3D, Tony Hawk 4, NBA Live 08 |
| **RPG & Strategy** | 12 | Galaxy On Fire 2, Ancient Empires II, Heroes of Might & Magic, Age of Empires III, Townsmen 6, Gothic 3, Worms Forts |

---

## 🚀 Features

- **Retro Mobile Chassis & Controls**: Classic mobile phone frame with responsive on-screen **D-Pad** (▲ ◀ ▶ ▼) and action buttons (**[A] / [B]**), touch-optimized for mobile Android devices and keyboard (`Arrows` / `WASD` + `Space`).
- **8-Bit Sound Synthesizer**: Web Audio API sound generator for retro laser chirps, explosion rumbles, powerup chimes, and audio mute toggle (no external audio files needed).
- **Search & Filter**: Real-time search across all 100 games by title, developer (Gameloft, Nokia, EA Mobile, Digital Chocolate, Konami), or genre.
- **Java 17 REST API**:
  - `GET /api/games` – Returns all 100 games (supports `?category=...` and `?search=...`).
  - `GET /api/scores` & `POST /api/scores` – High score leaderboard.
  - `GET /api/status` – JVM memory and server health metrics.
- **Vercel Ready**: Preconfigured [`vercel.json`](file:///workspace/java/vercel.json) for 1-click edge deployment.

---

## 💻 Quick Start

### Run Locally with Java
```bash
# 1. Run unit tests
./gradlew test

# 2. Start the local server
./gradlew run
```
Open **`http://localhost:8080`** in your browser.

---

## 🌐 Deploy to Vercel

1. Visit [**vercel.com/new**](https://vercel.com/new).
2. Connect your GitHub repository [**`saschool12/java`**](https://github.com/saschool12/java).
3. Click **Deploy** to launch the 100-game retro arcade live!
