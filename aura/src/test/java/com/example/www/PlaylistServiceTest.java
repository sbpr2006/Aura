package com.example.www;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.beats.AuraApplication;
import com.beats.model.Playlists;
import com.beats.model.Songs;
import com.beats.model.Users;
import com.beats.repository.MusicRepository;
import com.beats.repository.PlaylistRepository;
import com.beats.repository.PlaylistSongsRepository;
import com.beats.repository.SongRepository;
import com.beats.services.PlaylistService;

@SpringBootTest(classes = AuraApplication.class)
@ActiveProfiles("test")
@Transactional
class PlaylistServiceTest {

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistSongsRepository playlistSongsRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private MusicRepository musicRepository;

    private Users user;
    private Songs song;

    @BeforeEach
    void setUp() {
        playlistSongsRepository.deleteAll();
        playlistRepository.deleteAll();
        songRepository.deleteAll();
        musicRepository.deleteAll();

        user = Users.builder()
                .username("playlistuser")
                .email("playlistuser@example.com")
                .password("pass")
                .name("Playlist User")
                .build();
        user = musicRepository.save(user);

        song = Songs.builder()
                .title("Aura Song")
                .artist("Aura Artist")
                .repeatedCount(10L)
                .build();
        song = songRepository.save(song);
    }

    @Test
    void testSaveAndDeletePlaylist() {
        Playlists playlist = Playlists.builder()
                .playlistName("My Test Playlist")
                .user(user)
                .playedCount(0L)
                .build();

        playlistService.savePlaylist(playlist);
        assertNotNull(playlist.getPlaylistId());

        Playlists found = playlistService.getPlaylistById(playlist.getPlaylistId());
        assertEquals("My Test Playlist", found.getPlaylistName());

        playlistService.deletePlaylist(playlist.getPlaylistId());
        assertNull(playlistService.getPlaylistById(playlist.getPlaylistId()));
    }

    @Test
    void testGetPlaylistsByUser() {
        Playlists p1 = Playlists.builder().playlistName("List 1").user(user).build();
        Playlists p2 = Playlists.builder().playlistName("List 2").user(user).build();
        playlistRepository.save(p1);
        playlistRepository.save(p2);

        List<Playlists> playlists = playlistService.getPlaylistsByUser(user.getUserId());
        assertEquals(2, playlists.size());
    }

    @Test
    void testGetTrendingPlaylists() {
        Playlists p1 = Playlists.builder().playlistName("List A").user(user).playedCount(50L).build();
        Playlists p2 = Playlists.builder().playlistName("List B").user(user).playedCount(100L).build();
        Playlists p3 = Playlists.builder().playlistName("List C").user(user).playedCount(10L).build();
        playlistRepository.save(p1);
        playlistRepository.save(p2);
        playlistRepository.save(p3);

        List<Playlists> trending = playlistService.getTrendingPlaylists();
        assertEquals(3, trending.size());
        assertEquals("List B", trending.get(0).getPlaylistName()); // 100
        assertEquals("List A", trending.get(1).getPlaylistName()); // 50
        assertEquals("List C", trending.get(2).getPlaylistName()); // 10
    }

    @Test
    void testFindByUserIdAndPlaylistName() {
        Playlists p1 = Playlists.builder().playlistName("Search Name").user(user).build();
        playlistRepository.save(p1);

        Playlists found = playlistService.findByUserIdAndPlaylistName(user.getUserId(), "Search Name");
        assertNotNull(found);
        assertEquals(p1.getPlaylistId(), found.getPlaylistId());
    }

    @Test
    void testFindByUserIdAndPlaylistId() {
        Playlists p1 = Playlists.builder().playlistName("Search ID").user(user).build();
        playlistRepository.save(p1);

        Playlists found = playlistService.findByUserIdAndPlaylistId(user.getUserId(), p1.getPlaylistId());
        assertNotNull(found);
        assertEquals("Search ID", found.getPlaylistName());
    }

    @Test
    void testGetOrCreateFavouriteList() {
        // Initially should not exist
        Playlists pNull = playlistRepository.findByUser_UserIdAndPlaylistName(user.getUserId(), "FavouriteList");
        assertNull(pNull);

        // Get or Create
        Playlists fav = playlistService.getOrCreateFavouriteList(user);
        assertNotNull(fav);
        assertEquals("FavouriteList", fav.getPlaylistName());
        assertEquals(user.getUserId(), fav.getUser().getUserId());

        // Call again -> should return the existing one
        Playlists fav2 = playlistService.getOrCreateFavouriteList(user);
        assertEquals(fav.getPlaylistId(), fav2.getPlaylistId());
    }

    @Test
    void testAddAndRemoveSongFromPlaylist() {
        Playlists playlist = Playlists.builder().playlistName("Action List").user(user).build();
        playlistRepository.save(playlist);

        // Add
        playlistService.addSongToPlaylist(playlist.getPlaylistId(), song);
        assertTrue(playlistService.songExistsInPlaylist(playlist.getPlaylistId(), song.getSongId()));
        assertEquals(1, playlistService.getPlaylistSongCount(playlist.getPlaylistId()));

        // Avoid Duplicate Add
        playlistService.addSongToPlaylist(playlist.getPlaylistId(), song);
        assertEquals(1, playlistService.getPlaylistSongCount(playlist.getPlaylistId()));

        // Remove
        playlistService.removeSongFromPlaylist(playlist.getPlaylistId(), song.getSongId());
        assertFalse(playlistService.songExistsInPlaylist(playlist.getPlaylistId(), song.getSongId()));
        assertEquals(0, playlistService.getPlaylistSongCount(playlist.getPlaylistId()));
    }

    @Test
    void testIsSongInFavourites() {
        assertFalse(playlistService.isSongInFavourites(user, song.getSongId()));

        Playlists fav = playlistService.getOrCreateFavouriteList(user);
        playlistService.addSongToPlaylist(fav.getPlaylistId(), song);

        assertTrue(playlistService.isSongInFavourites(user, song.getSongId()));
    }

    @Test
    void testRefreshAllPlaylistCounts() {
        Playlists playlist = Playlists.builder().playlistName("Refresh List").user(user).playedCount(0L).build();
        playlistRepository.save(playlist);

        playlistService.addSongToPlaylist(playlist.getPlaylistId(), song);

        // Pre-condition count is 0
        Playlists dbPlaylist = playlistRepository.findByPlaylistId(playlist.getPlaylistId());
        assertEquals(0L, dbPlaylist.getPlayedCount());

        // Refresh counts
        playlistService.refreshAllPlaylistCounts();

        // Should update playedCount to song.repeatedCount (10L)
        Playlists refreshedPlaylist = playlistRepository.findByPlaylistId(playlist.getPlaylistId());
        assertEquals(10L, refreshedPlaylist.getPlayedCount());
    }
}
