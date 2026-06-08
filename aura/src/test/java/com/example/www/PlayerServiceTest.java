package com.example.www;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.beats.model.Songs;
import com.beats.services.PlayerService;

class PlayerServiceTest {

    private PlayerService playerService;
    private List<Songs> testSongs;

    @BeforeEach
    void setUp() {
        playerService = new PlayerService();
        testSongs = new ArrayList<>();

        Songs song1 = new Songs();
        song1.setSongId(1L);
        song1.setTitle("Song One");
        song1.setArtist("Artist One");

        Songs song2 = new Songs();
        song2.setSongId(2L);
        song2.setTitle("Song Two");
        song2.setArtist("Artist Two");

        Songs song3 = new Songs();
        song3.setSongId(3L);
        song3.setTitle("Song Three");
        song3.setArtist("Artist Three");

        testSongs.add(song1);
        testSongs.add(song2);
        testSongs.add(song3);
    }

    @Test
    void testGetCurrentSongEmptyQueue() {
        assertNull(playerService.getCurrentSong());
    }

    @Test
    void testNextSongEmptyQueue() {
        assertNull(playerService.nextSong());
    }

    @Test
    void testPreviousSongEmptyQueue() {
        assertNull(playerService.previousSong());
    }

    @Test
    void testSetQueueAndGetCurrentSong() {
        playerService.setQueue(testSongs);
        Songs current = playerService.getCurrentSong();
        assertEquals(1L, current.getSongId());
        assertEquals("Song One", current.getTitle());
    }

    @Test
    void testNextSongNavigation() {
        playerService.setQueue(testSongs);
        
        // From index 0 -> 1
        Songs next = playerService.nextSong();
        assertEquals(2L, next.getSongId());

        // From index 1 -> 2
        next = playerService.nextSong();
        assertEquals(3L, next.getSongId());

        // From index 2 -> 0 (wrap around)
        next = playerService.nextSong();
        assertEquals(1L, next.getSongId());
    }

    @Test
    void testPreviousSongNavigation() {
        playerService.setQueue(testSongs);

        // From index 0 -> 2 (wrap around to the end of the queue)
        Songs prev = playerService.previousSong();
        assertEquals(3L, prev.getSongId());

        // From index 2 -> 1
        prev = playerService.previousSong();
        assertEquals(2L, prev.getSongId());

        // From index 1 -> 0
        prev = playerService.previousSong();
        assertEquals(1L, prev.getSongId());
    }
}
