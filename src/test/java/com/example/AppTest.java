package com.example;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {
    @Test
    void appGreetingIsPresent() {
        App app = new App();
        assertNotNull(app.getGreeting());
        assertTrue(app.getGreeting().contains("All 100 Games Fully Playable"));
    }

    @Test
    void gameCatalogContainsExactly100Games() {
        App.GameCatalog catalog = new App.GameCatalog();
        assertEquals(100, catalog.getTotalGamesCount(), "Game catalog must contain exactly 100 games");
    }

    @Test
    void all100GamesArePlayable() {
        App.GameCatalog catalog = new App.GameCatalog();
        for (App.GameInfo game : catalog.getAllGames()) {
            assertTrue(game.isPlayable(), "Game " + game.getTitle() + " must be playable!");
        }
    }

    @Test
    void gameCatalogFiltersByCategoryAndSearch() {
        App.GameCatalog catalog = new App.GameCatalog();

        List<App.GameInfo> actionGames = catalog.filter("Action", null);
        assertNotNull(actionGames);
        assertFalse(actionGames.isEmpty());

        List<App.GameInfo> asphaltSearch = catalog.filter(null, "asphalt");
        assertNotNull(asphaltSearch);
        assertTrue(asphaltSearch.size() >= 2);
    }

    @Test
    void scoreRepositoryTracksLeaderboard() {
        App.ScoreRepository repo = new App.ScoreRepository();
        repo.addScore(new App.ScoreEntry("Duke99", "space-impact", 8888, System.currentTimeMillis()));

        List<App.ScoreEntry> top = repo.getTopScores("space-impact", 5);
        assertNotNull(top);
        assertTrue(top.size() >= 1);
        assertEquals("Duke99", top.get(0).getPlayer());
        assertEquals(8888, top.get(0).getScore());
    }
}
