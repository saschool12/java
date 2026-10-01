package com.example;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {
    @Test
    void appGreetingIsPresent() {
        App app = new App();
        assertNotNull(app.getGreeting());
        assertTrue(app.getGreeting().contains("Java Retro Games Hub"));
    }

    @Test
    void appConfiguresPort() {
        App app = new App(9999);
        assertEquals(9999, app.getPort());
    }

    @Test
    void scoreRepositoryTracksLeaderboard() {
        App.ScoreRepository repo = new App.ScoreRepository();
        repo.addScore(new App.ScoreEntry("Champion", "space-impact", 9999, System.currentTimeMillis()));

        List<App.ScoreEntry> top = repo.getTopScores("space-impact", 5);
        assertNotNull(top);
        assertTrue(top.size() >= 1);
        assertEquals("Champion", top.get(0).getPlayer());
        assertEquals(9999, top.get(0).getScore());
    }
}
