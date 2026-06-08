package com.example.www;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.beats.AuraApplication;
import com.beats.model.PlaylistSongs;
import com.beats.model.Playlists;
import com.beats.model.Songs;
import com.beats.model.Users;
import com.beats.repository.MusicRepository;
import com.beats.repository.PlaylistRepository;
import com.beats.repository.PlaylistSongsRepository;
import com.beats.repository.SongRepository;

@SpringBootTest(classes = AuraApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PlaylistControlllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistSongsRepository playlistSongsRepository;

    @Autowired
    private SongRepository songRepository;

    private Users user;
    private Songs song;
    private Playlists playlist;

    @BeforeEach
    void setUp() {
        playlistSongsRepository.deleteAll();
        playlistRepository.deleteAll();
        songRepository.deleteAll();
        musicRepository.deleteAll();

        user = Users.builder()
                .username("playlistuser")
                .email("playlist@example.com")
                .password("password")
                .name("Playlist User")
                .build();
        user = musicRepository.save(user);

        song = Songs.builder()
                .title("Aura Master")
                .artist("Sia")
                .repeatedCount(0L)
                .build();
        song = songRepository.save(song);

        playlist = Playlists.builder()
                .playlistName("Existing Playlist")
                .user(user)
                .build();
        playlist = playlistRepository.save(playlist);
    }

    @Test
    void testCreatePlaylistNotLoggedIn() throws Exception {
        mockMvc.perform(post("/playlist/create")
                        .param("playlistName", "New List"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/loginPage"));
    }

    @Test
    void testCreatePlaylistLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        mockMvc.perform(post("/playlist/create")
                        .param("playlistName", "Summer Anthems")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/playlists"));

        assertEquals("Playlist Created Successfully", session.getAttribute("success"));

        // Verify it exists in database
        Playlists created = playlistRepository.findByUser_UserIdAndPlaylistName(user.getUserId(), "Summer Anthems");
        assertNotNull(created);
    }

    @Test
    void testDeletePlaylist() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Add a song to playlist first to test cascading deletes
        PlaylistSongs ps = PlaylistSongs.builder().playlist(playlist).song(song).build();
        playlistSongsRepository.save(ps);

        mockMvc.perform(post("/playlist/delete/" + playlist.getPlaylistId())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/playlists"));

        assertEquals("Playlist Deleted Successfully", session.getAttribute("success"));

        // Check deleted from DB
        assertNull(playlistRepository.findByPlaylistId(playlist.getPlaylistId()));
        assertEquals(0, playlistSongsRepository.findByPlaylist_PlaylistId(playlist.getPlaylistId()).size());
    }

    @Test
    void testRenamePlaylist() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/playlist/rename")
                        .param("playlistId", playlist.getPlaylistId().toString())
                        .param("playlistName", "Renamed List")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/playlist"));

        assertEquals("Playlist Renamed Successfully", session.getAttribute("success"));

        Playlists updated = playlistRepository.findByPlaylistId(playlist.getPlaylistId());
        assertEquals("Renamed List", updated.getPlaylistName());
    }

    @Test
    void testToggleFavourite() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // 1. Not Logged In
        mockMvc.perform(post("/playlist/toggleFavourite/" + song.getSongId()))
                .andExpect(status().isOk())
                .andExpect(content().string("LOGIN_REQUIRED"));

        // 2. Logged In -> Added
        mockMvc.perform(post("/playlist/toggleFavourite/" + song.getSongId())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("ADDED"));

        Playlists favs = playlistRepository.findByUser_UserIdAndPlaylistName(user.getUserId(), "FavouriteList");
        assertNotNull(favs);
        assertTrue(playlistSongsRepository.findByPlaylistPlaylistIdAndSongSongId(favs.getPlaylistId(), song.getSongId()) != null);

        // 3. Logged In -> Removed (toggle again)
        mockMvc.perform(post("/playlist/toggleFavourite/" + song.getSongId())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("REMOVED"));

        assertNull(playlistSongsRepository.findByPlaylistPlaylistIdAndSongSongId(favs.getPlaylistId(), song.getSongId()));
    }

    @Test
    void testRemoveSongFromPlaylist() throws Exception {
        MockHttpSession session = new MockHttpSession();

        PlaylistSongs ps = PlaylistSongs.builder().playlist(playlist).song(song).build();
        playlistSongsRepository.save(ps);

        mockMvc.perform(post("/playlist/removeSong")
                        .param("playlistId", playlist.getPlaylistId().toString())
                        .param("songId", song.getSongId().toString())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/myplaylist/" + playlist.getPlaylistId()));

        assertEquals("Song removed from playlist successfully", session.getAttribute("success"));
        assertNull(playlistSongsRepository.findByPlaylistPlaylistIdAndSongSongId(playlist.getPlaylistId(), song.getSongId()));
    }

    @Test
    void testAddSongByName() throws Exception {
        mockMvc.perform(post("/playlist/addSongByName")
                        .param("playlistId", playlist.getPlaylistId().toString())
                        .param("songName", "Aura Master"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/myplaylist/" + playlist.getPlaylistId()));

        assertNotNull(playlistSongsRepository.findByPlaylistPlaylistIdAndSongSongId(playlist.getPlaylistId(), song.getSongId()));
    }

    @Test
    void testAddSongById() throws Exception {
        mockMvc.perform(post("/playlist/addSongById")
                        .param("playlistId", playlist.getPlaylistId().toString())
                        .param("songId", song.getSongId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/nowPlaying?songId=" + song.getSongId()));

        assertNotNull(playlistSongsRepository.findByPlaylistPlaylistIdAndSongSongId(playlist.getPlaylistId(), song.getSongId()));
    }

    @Test
    void testPlayAll() throws Exception {
        MockHttpSession session = new MockHttpSession();

        PlaylistSongs ps = PlaylistSongs.builder().playlist(playlist).song(song).build();
        playlistSongsRepository.save(ps);

        mockMvc.perform(get("/playlist/playall/" + playlist.getPlaylistId())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("myPlaylist"))
                .andExpect(model().attributeExists("songs"));

        List<Songs> queue = (List<Songs>) session.getAttribute("queue");
        Integer currentIndex = (Integer) session.getAttribute("currentIndex");

        assertNotNull(queue);
        assertEquals(1, queue.size());
        assertEquals("Aura Master", queue.get(0).getTitle());
        assertEquals(0, currentIndex);
    }
}
