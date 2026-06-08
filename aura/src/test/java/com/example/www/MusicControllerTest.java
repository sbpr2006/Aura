package com.example.www;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
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
class MusicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistSongsRepository playlistSongsRepository;

    private Users user;
    private Songs song;

    @BeforeEach
    void setUp() {
        playlistSongsRepository.deleteAll();
        playlistRepository.deleteAll();
        songRepository.deleteAll();
        musicRepository.deleteAll();

        user = Users.builder()
                .username("musicuser")
                .email("music@example.com")
                .password("plainpassword")
                .name("Music User")
                .bio("I love beats!")
                .build();
        user = musicRepository.save(user);

        song = Songs.builder()
                .title("Aura Vibe")
                .artist("Beatmaker")
                .album("Lofi Vol 1")
                .repeatedCount(5L)
                .build();
        song = songRepository.save(song);
    }

    @Test
    void testRegisterPage() throws Exception {
        mockMvc.perform(get("/usr/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register.html"));
    }

    @Test
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/usr/loginPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testAddUserSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/usr/addUser")
                        .param("username", "newjoiner")
                        .param("email", "newjoiner@example.com")
                        .param("password", "newpass")
                        .param("name", "New Joiner")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/home"));

        Users loggedUser = (Users) session.getAttribute("loggedUser");
        assertNotNull(loggedUser);
        assertEquals("newjoiner", loggedUser.getUsername());
        assertEquals("newjoiner", session.getAttribute("username"));
    }

    @Test
    void testAddUserFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Pass missing email, which violates nullable = false
        mockMvc.perform(post("/usr/addUser")
                        .param("username", "invaliduser")
                        .param("password", "pass")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("messageType", "error"));

        assertNull(session.getAttribute("loggedUser"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/usr/login")
                        .param("username", "musicuser")
                        .param("password", "plainpassword")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/home"));

        assertEquals("musicuser", session.getAttribute("username"));
        assertEquals("music@example.com", session.getAttribute("email"));
    }

    @Test
    void testLoginFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/usr/login")
                        .param("username", "musicuser")
                        .param("password", "wrongpassword")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("login.html"))
                .andExpect(model().attribute("message", "Invalid Username or Password"))
                .andExpect(model().attribute("messageType", "error"));

        assertNull(session.getAttribute("loggedUser"));
    }

    @Test
    void testLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        mockMvc.perform(get("/usr/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/loginPage"));

        assertTrue(session.isInvalid());
    }

    @Test
    void testHomePageNotLoggedIn() throws Exception {
        mockMvc.perform(get("/usr/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/loginPage"));
    }

    @Test
    void testHomePageLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        mockMvc.perform(get("/usr/home").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("index.html"))
                .andExpect(model().attributeExists("songs"))
                .andExpect(model().attributeExists("playlists"));
    }

    @Test
    void testProfileNotLoggedIn() throws Exception {
        mockMvc.perform(get("/usr/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/loginPage"));
    }

    @Test
    void testProfileLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        mockMvc.perform(get("/usr/profile").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    void testUpdateProfile() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        mockMvc.perform(post("/usr/updateProfile")
                        .param("name", "Updated Name")
                        .param("username", "musicuser_updated")
                        .param("email", "music_updated@example.com")
                        .param("bio", "Updated Bio")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("success", "Profile Updated Successfully"));

        Users updatedUser = (Users) session.getAttribute("loggedUser");
        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("musicuser_updated", updatedUser.getUsername());
        assertEquals("music_updated@example.com", updatedUser.getEmail());
        assertEquals("Updated Bio", updatedUser.getBio());
    }

    @Test
    void testChangePasswordSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        mockMvc.perform(post("/usr/changePassword")
                        .param("currentPassword", "plainpassword")
                        .param("newPassword", "newsecret")
                        .param("confirmPassword", "newsecret")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attribute("success", "Password Updated Successfully"));

        Users updated = musicRepository.findByUsername("musicuser").orElse(null);
        assertNotNull(updated);
        assertEquals("newsecret", updated.getPassword());
    }

    @Test
    void testChangePasswordWrongCurrent() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        mockMvc.perform(post("/usr/changePassword")
                        .param("currentPassword", "wrongcurrent")
                        .param("newPassword", "newsecret")
                        .param("confirmPassword", "newsecret")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attribute("error", "Current Password Incorrect"));
    }

    @Test
    void testChangePasswordNewMismatched() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        mockMvc.perform(post("/usr/changePassword")
                        .param("currentPassword", "plainpassword")
                        .param("newPassword", "newsecret")
                        .param("confirmPassword", "different")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attribute("error", "Passwords Do Not Match"));
    }

    @Test
    void testPlaylistsPage() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        Playlists playlist = Playlists.builder().playlistName("Rock List").user(user).build();
        playlistRepository.save(playlist);

        mockMvc.perform(get("/usr/playlists").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("playlists"))
                .andExpect(model().attributeExists("playlists"))
                .andExpect(model().attributeExists("favouriteSongs"))
                .andExpect(model().attributeExists("playlistSongCounts"));
    }

    @Test
    void testOpenFavouritePlaylist() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        mockMvc.perform(get("/usr/myplaylist/favourites").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("myList"))
                .andExpect(model().attributeExists("playlist"))
                .andExpect(model().attributeExists("playlistSongs"));

        Playlists fav = playlistRepository.findByUser_UserIdAndPlaylistName(user.getUserId(), "FavouriteList");
        assertNotNull(fav);
    }

    @Test
    void testOpenPlaylist() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        Playlists p1 = Playlists.builder().playlistName("Custom List").user(user).build();
        playlistRepository.save(p1);

        mockMvc.perform(get("/usr/myplaylist/" + p1.getPlaylistId()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("myList"))
                .andExpect(model().attribute("playlist", p1))
                .andExpect(model().attribute("isOwner", true))
                .andExpect(model().attributeExists("playlistSongs"));
    }

    @Test
    void testNowPlaying() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        // Standard flow
        mockMvc.perform(get("/usr/nowPlaying")
                        .param("songId", song.getSongId().toString())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("now_playing"))
                .andExpect(model().attribute("song", song))
                .andExpect(model().attribute("isFavourite", false))
                .andExpect(model().attributeExists("userPlaylists"));

        // Missing songId param -> redirect to /usr/home
        mockMvc.perform(get("/usr/nowPlaying").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usr/home"));
    }

    @Test
    void testIncreaseRepeatedCount() throws Exception {
        // Pre-count is 5
        mockMvc.perform(post("/usr/song/incrementRepeat")
                        .param("songId", song.getSongId().toString()))
                .andExpect(status().isOk());

        Songs updated = songRepository.findBySongId(song.getSongId());
        assertEquals(6L, updated.getRepeatedCount());
    }

    @Test
    void testUpdatePlayedCount() throws Exception {
        // Testing count refresh endpoint
        mockMvc.perform(post("/usr/playlist/updatePlayedCount"))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchPage() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", user);

        mockMvc.perform(get("/usr/search").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("search"));
    }

    @Test
    void testSearchSongsApi() throws Exception {
        mockMvc.perform(get("/usr/searchSongs").param("query", "Vibe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Aura Vibe"));
    }

    @Test
    void testAddRecentSearch() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/usr/addRecentSearch")
                        .param("query", "Beatmaker")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("ADDED"));

        List<String> recent = (List<String>) session.getAttribute("recentSearches");
        assertNotNull(recent);
        assertEquals(1, recent.size());
        assertEquals("Beatmaker", recent.get(0));

        // Add duplicate query -> should be reordered without duplicate size increase
        mockMvc.perform(post("/usr/addRecentSearch")
                        .param("query", "Beatmaker")
                        .session(session))
                .andExpect(status().isOk());

        recent = (List<String>) session.getAttribute("recentSearches");
        assertEquals(1, recent.size());
    }
}
